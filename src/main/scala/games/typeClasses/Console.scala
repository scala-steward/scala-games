package games.typeClasses

trait Console[F[_]] {
  def readLine: F[String]

  def printLine(text: String): F[Unit]
}