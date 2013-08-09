package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
//import com.google.inject.Guice
//import com.tzavellas.sse.guice.ScalaModule
//import com.google.inject.name.Names

class EmptySpec extends Specification {
  "Empty" should {

    /*lazy val injector = Guice.createInjector(new ScalaModule {
      def configure() {
        bind[Empty].to[Empty]
      }
    })*/

    "throw if you ask canTerminate" in {
      //val sud = injector.getInstance(classOf[Empty])
      Empty().canTerminate(1) must throwA[scala.RuntimeException]
    }

    "throw if you ask toRawScala" in {
      Empty().toRawScala must throwA[scala.RuntimeException]
    }

    "validate false" in {
      Empty().validate(10) mustEqual false
    }
  }
}