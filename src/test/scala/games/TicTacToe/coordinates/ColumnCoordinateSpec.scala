package games.TicTacToe.coordinates

import cats.data.NonEmptyList
import org.specs2.mutable.Specification
import games.typeClasses.encoderDecoderParity

class ColumnCoordinateSpec extends Specification {
  "Encoding/Decoder" >> {
    encoderDecoderParity[ColumnCoordinate](NonEmptyList.fromListUnsafe(
      List(Col1, Col2, Col3).zip(
        List("A",  "B",  "C"))
    ))
  }
}