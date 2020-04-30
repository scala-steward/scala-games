package games.TicTacToe.game

import cats.data.NonEmptyList
import org.specs2.mutable.Specification
import games.typeClasses.encoderDecoderParity
import games.TicTacToe.{X, O}

class RowSpec extends Specification {
  "Encoding/Decoder" >> {
    encoderDecoderParity[Row](NonEmptyList.fromListUnsafe(
      List(
        Row(Some(X), Some(O), None),
        Row(None, Some(X), Some(O)),
        Row(Some(X), Some(O), None)
      ).zip(
        List(
          "X O .",
          ". X O",
          "X O ."
        ))
    ))
  }

  "full" >> {
    Row.full(Row(None, None, None)) must beFalse
    Row.full(Row(Some(X), None, None)) must beFalse
    Row.full(Row(Some(X), Some(X), None)) must beFalse
    Row.full(Row(Some(X), Some(X), Some(X))) must beTrue
  }
}