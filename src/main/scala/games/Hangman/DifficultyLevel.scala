package games.Hangman

import games.typeClasses.{Decoder, Encoder}
import games.typeClasses.Encoder._

sealed trait DifficultyLevel {
  def lives: Lives
}

case object Easy extends DifficultyLevel {
  val lives: Lives = Lives.max
}

case object Medium extends DifficultyLevel {
  val lives: Lives = Lives.avg
}

case object Hard extends DifficultyLevel {
  val lives: Lives = Lives.min
}

object DifficultyLevel {
  implicit val difficultyEncoder: Encoder[DifficultyLevel] = {
    case Easy   => "easy"
    case Medium => "medium"
    case Hard   => "hard"
  }

  implicit val difficultyDecoder: Decoder[DifficultyLevel] = (value: String) =>
    List[DifficultyLevel](Easy, Medium, Hard).find(_.encode == value)
}
