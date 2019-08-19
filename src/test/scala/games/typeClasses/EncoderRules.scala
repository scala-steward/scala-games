package games.typeClasses

import org.specs2.matcher.MatchResult

object EncoderRules {
  def reflectiveToDecoder[T: Encoder : Decoder](value: String): MatchResult[String] =
    Encoder[T].encode(Decoder[T].decode(value).get) must_=== value
}
