package games.Hangman

import cats.data.NonEmptyList

trait WordService[F[_]] {
  def getWord: F[NonEmptyList[Char]]
}
