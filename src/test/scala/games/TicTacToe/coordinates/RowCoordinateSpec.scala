package games.TicTacToe.coordinates

import cats.data.NonEmptyList
import org.specs2.mutable.Specification
import games.typeClasses.encoderDecoderParity

class RowCoordinateSpec extends Specification {
  "Encoding/Decoder" >> {
    encoderDecoderParity[RowCoordinate](NonEmptyList.fromListUnsafe(
      List(Row1, Row2, Row3).zip(
        List("1",  "2",  "3"))
    ))
  }
