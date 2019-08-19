package games.typeClasses

import org.specs2.matcher.MatchResult

object DecoderRules {
  def reflectiveToEncoder[T: Encoder : Decoder](value: T): MatchResult[Option[T]] =
    Decoder[T].decode(Encoder[T].encode(value)) must beSome(value)
}