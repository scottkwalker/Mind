package nodes.legalNeighbours

import nodes.IntegerMFactory
import nodes.helpers.{ICreateChildNodes, IScope}
import com.google.inject.Inject
import nodes.memoization.{Memoize, Memoize1}
import play.api.libs.json.{Json, JsValue, Writes}
import java.util.concurrent.CountDownLatch
import play.api.libs.json.Json._
import scala.annotation.tailrec
import scala.collection.mutable

final class LegalNeighboursImpl @Inject()(intToFactory: FactoryIdToFactory) extends LegalNeighbours {
  implicit val mapWrites = new Writes[Map[IScope, Either[CountDownLatch, Seq[Int]]]] {
    implicit val eitherWrites = new Writes[Either[CountDownLatch, Seq[Int]]] {
      def writes(o: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
        o.fold(
          countDownLatchContent => ???,
          intContent => "intContent" -> Json.toJson(intContent)
        )
      )
    }
    
    def writes(o: Map[IScope, Either[CountDownLatch, Seq[Int]]]): JsValue = {
      val keyAsString = o.filter(kv => kv._2.isRight). // Only completed values.
        map(kv => kv._1.toString -> kv._2) // Json keys must be strings.
      Json.toJson(keyAsString)
    }
  }

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = legalForScope2(scope, neighbours).map(intToFactory.convert)



  private[this] val cache = mutable.Map.empty[IScope, Either[CountDownLatch, Seq[Int]]]

  /**
   * What to do if we do not find the value already in the memo
   * table.
   */
  @tailrec private[this] def missing(key: IScope, neighbours: Seq[Int]): Seq[Int] = {
    synchronized {
      // With the lock, check to see what state the value is in.
      cache.get(key) match {
        case None =>
          // If it's missing, then claim the slot by putting in a CountDownLatch that will be completed when the value is
          // available.
          val latch = new CountDownLatch(1)
          cache += (key -> Left(latch))

          // The latch wrapped in Left indicates that the value needs to be computed in this thread, and then the
          // latch counted down.
          Left(latch)
        case Some(other) =>
          // This is either the latch that will indicate that the work has been done, or the computed value.
          Right(other)
      }
    } match {
      case Right(Right(computed)) => computed // The computation is already done.
      case Right(Left(latch)) =>
        // Someone else is doing the computation.
        latch.await()
        // This recursive call will happen when there is an exception computing the value, or if the value is
        // currently being computed.
        missing(key, neighbours)
      case Left(latch) =>
        // Compute the value outside of the synchronized block.
        val calculated =
          try {
            f(key, neighbours)
          } catch {
            case t: Throwable =>
              // If there was an exception running the computation, then we need to make sure we do not
              // starve any waiters before propagating the exception.
              synchronized {
                cache -= key
              }
              latch.countDown()
              throw t
          }

        // Update the memo table to indicate that the work has been done, and signal to any waiting threads that the
        // work is complete.
        synchronized {
          cache += (key -> Right(calculated))
        }
        latch.countDown()
        calculated
    }
  }

  def f(scope: IScope, neighbours: Seq[Int]): Seq[Int] = {
    if (scope.hasDepthRemaining) neighbours.filter {
      neighbourId =>
        val factory = intToFactory.convert(neighbourId)
        factory.neighbourIds.isEmpty || missing(key = scope.incrementDepth, neighbours = factory.neighbourIds).length > 0
    }
    else Seq.empty
  }

  private def legalForScope2(key: IScope, neighbours: Seq[Int]): Seq[Int] = {
    cache.get(key) match {
      case Some(Right(b)) => b
      case _ => f(key, neighbours)
    }
  }
}