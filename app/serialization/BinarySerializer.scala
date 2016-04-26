package serialization

final class BinarySerializer {

  def write[A](o: A): Array[Byte] = {
    val ba = new java.io.ByteArrayOutputStream()
    val out = new java.io.ObjectOutputStream(ba)
    out.writeObject(o)
    out.close()
    ba.toByteArray
  }

  def read[A](buffer: Array[Byte]): A = {
    val in =
      new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(buffer))
    in.readObject().asInstanceOf[A]
  }
}
