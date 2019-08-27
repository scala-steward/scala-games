
# Tic-Tac-Toe in FP Scala


It tends to be hard to understand functional concepts without a context. And what’s a better setting than a simple game of TicTacToe. A quick reminder: TicTacToe is a game where you have a board of 9 squares, and you and your opponent are trying to fill the board with X’s or O’s in specific configurations. You can learn more about the rules and strategies on Wikipedia [https://en.wikipedia.org/wiki/Tic-tac-toe](https://en.wikipedia.org/wiki/Tic-tac-toe).

## Game Algebra

Let’s start by defining our basic building block of the game as an Algebraic Data Tyoe (commonly known as an ADT):

```scala
sealed trait Square

object X extends Square
object O extends Square
```

If we imagine the board at the beginning of the game, it can also be empty, so we need a notion of an empty **Square**. We can represent it with `Option[Square]` . Where `Some[Square]` is a Square with a set value and `None: Option[Square]` is a square with an empty value. A *Row* would look like this:

```scala
case class Row(col1: Option[Square], col2: Option[Square], col3: Option[Square])
```

As mentioned before our **Game** has nine squares, so 3 x 3 squares. So the game might look like:

```scala
case class Game(row1: Row, row2: Row, row3: Row)
```

The only thing missing for game-related logic is to define a way to talk about a **Square** position on the game board. So let’s define the concept of **Coordinates**.

```scala
sealed trait ColumnCoordinate

case object Col1 extends ColumnCoordinate
case object Col2 extends ColumnCoordinate
case object Col3 extends ColumnCoordinate

sealed trait RowCoordinate

case object Row1 extends RowCoordinate
case object Row2 extends RowCoordinate
case object Row3 extends RowCoordinate

case class Coordinate(column: ColumnCoordinate, row: RowCoordinate)
```

Because we will use either **X** or **O,** our decision also needs to have information about the **Square**. We can define a **Move** in the game as:

```scala
case class Move(square: Square, coordinates: Coordinate)
```

## Game Mechanics

So now we have all of our building blocks, and we can start working on game mechanics. One of the first things we can start working on is the ability to progress in the game. Let’s describe our move function by taking into account that we want only to fill non-empty squares:

```scala
def move(game: Game, move: Move): Option[Game] = {
  import game._
  move.coordinates match {
    case Coordinate(Col1, Row1) if row1.col1.isEmpty => Some(copy(row1 = row1.copy(col1 = Some(move.square))))
    case Coordinate(Col2, Row1) if row1.col2.isEmpty => Some(copy(row1 = row1.copy(col2 = Some(move.square))))
    case Coordinate(Col3, Row1) if row1.col3.isEmpty => Some(copy(row1 = row1.copy(col3 = Some(move.square))))

		case Coordinate(Col1, Row2) if row2.col1.isEmpty => Some(copy(row2 = row2.copy(col1 = Some(move.square))))
		    case Coordinate(Col2, Row2) if row2.col2.isEmpty => Some(copy(row2 = row2.copy(col2 = Some(move.square))))
		    case Coordinate(Col3, Row2) if row2.col3.isEmpty => Some(copy(row2 = row2.copy(col3 = Some(move.square))))

		case Coordinate(Col1, Row3) if row3.col1.isEmpty => Some(copy(row3 = row3.copy(col1 = Some(move.square))))
		    case Coordinate(Col2, Row3) if row3.col2.isEmpty => Some(copy(row3 = row3.copy(col2 = Some(move.square))))
		    case Coordinate(Col3, Row3) if row3.col3.isEmpty => Some(copy(row3 = row3.copy(col3 = Some(move.square))))

		case _ => None
  }
}
```

Now let’s start thinking about how can we complete the game. There are two basic ways. Either someone **wins,** or there is a **draw** when the board is full. Let’s start from **draw** case because it only requires us to know if we can make any more moves. To figure that out, we need to know if each *Row* in the **Game** is full.

```scala
def full(row: Row): Boolean = Seq(row.col1, row.col2, row.col3).forall(_.isDefined)

def full(game: Game): Boolean = Seq(game.row1, game.row2, game.row3).forall(Row.full)
```

The winning case is a bit more involved. It requires us to test if either one of the rows/columns/diagonals has been filled with the same symbol:

```scala
def winner(game: Game): Option[Square] = {
  import game._

	def winnerRow(row: Row): Option[Square] = row match {
	    case Row(Some(X), Some(X), Some(X)) => Some(X)
	    case Row(Some(O), Some(O), Some(O)) => Some(O)
	    case _ => None
	  }
	 
	def winnerColumn(colNr: Int): Option[Square] = if(colNr > 0 && colNr < 4) {
	    winnerRow(colNr match {
	      case 1 => Row(row1.col1, row2.col1, row3.col1)
	      case 2 => Row(row1.col2, row2.col2, row3.col2)
	      case 3 => Row(row1.col3, row2.col3, row3.col3)
	    })
	  } else {
	    None
	  }

	lazy val diagonalWinner = (row1, row2, row3) match {
	  case (Row(Some(X), _, _), 
	        Row(_, Some(X), _), 
	        Row(_, _, Some(X))) => Some(X)
	  case (Row(Some(O), _, _), 
	        Row(_, Some(O), _), 
	        Row(_, _, Some(O))) => Some(O)

	case (Row(_, _, Some(X)), 
	        Row(_, Some(X), _), 
	        Row(Some(X), _, _)) => Some(X)
	  case (Row(_, _, Some(O)), 
	        Row(_, Some(O), _), 
	        Row(Some(O), _, _)) => Some(O)
	  case _ => None
	}

    winnerRow(row1) <+>
    winnerRow(row2) <+>
    winnerRow(row3) <+>
    winnerColumn(1) <+>
    winnerColumn(2) <+>
    winnerColumn(3) <+>
    diagonalWinner
}
```

The last few bits of mechanics we need is a way to have an empty game ready quickly, and a list of all the possibles moves:

```scala
val empty = Game(
  Row(None, None, None),
  Row(None, None, None),
  Row(None, None, None)
)

val combinations: List[Coordinate] = for {
  column <- List(Col1, Col2, Col3)
  row <- List(Row1, Row2, Row3)
} yield Coordinate(column, row)
```

## Displaying the game

To be able to interact with the **Console**, we need a way to encode our **game** into a String. Let's use TypeClasses for that:

```scala
trait Encoder[T] {
  def encode(value: T): String
}

object Encoder {
  def apply[T](implicit ev: Encoder[T]): Encoder[T] = ev

implicit class EncoderClass[T: Encoder](value: T) {
    def encode: String = Encoder[T].encode(value)
  }
}

val empty = '.'
val x = 'X'
val o = 'O'

def toSymbol(value: Square): Char = value match {
  case X => x
  case O => o
}

implicit val squareEncoder: Encoder[Square] = (value: Square) => toSymbol(value).toString

implicit val squareOptEncoder: Encoder[Option[Square]] = (t: Option[Square]) => t.map(_.encode).getOrElse(empty.toString)

implicit val rowEncoder: Encoder[Row] = (t: Row) => Seq(t.col1, t.col2, t.col3).map(_.encode).mkString(" ")

// and for the game moves

implicit val columnCoordinatesEncoder: Encoder[ColumnCoordinate] = {
  case Col1 => "A"
  case Col2 => "B"
  case Col3 => "C"
}

implicit val rowCoordinatesEncoder: Encoder[RowCoordinate] = {
  case Row1 => "1"
  case Row2 => "2"
  case Row3 => "3"
}

implicit val coordinatesEncoder: Encoder[Coordinate] = (t: Coordinate) => t.column.encode + t.row.encode
```

We also need a decoder to read inputs from the **Console.** They might fail, so the result needs to be an `Option` type:

```scala
trait Decoder[T] {
  def decode(value: String): Option[T]
}

object Decoder {
  def apply[T](implicit ev: Decoder[T]): Decoder[T] = ev

  implicit class DecoderClass[T: Decoder](value: String) {
    def decode: Option[T] = Decoder[T].decode(value)
  }
}

implicit val squareDecoder: Decoder[Square] = (value: String) => value.headOption.flatMap {
  case Square.x => Some(X)
  case Square.o => Some(O)
  case _ => None
}

implicit val coordinatesDecoder: Decoder[Coordinate] = (value: String) => if (value.length == 2) {
  Applicative[Option].map2(
    Decoder[ColumnCoordinate].decode(value.charAt(0).toString),
    Decoder[RowCoordinate].decode(value.charAt(1).toString)
  )(Coordinate)
} else {
  None
}
```

The last, but not least thing is we want to display our game in a way that would let the user decide what move to make.

```scala
def displayGameOnConsole(game: Game): String = {
  def encodeRow(row: Row, coordinate: RowCoordinate) = s"${coordinate.encode} ${row.encode}"

Seq(
    "  " + Seq[ColumnCoordinate](Col1, Col2, Col3).map(_.encode).mkString(" "),
    encodeRow(game.row1, Row1),
    encodeRow(game.row2, Row2),
    encodeRow(game.row3, Row3)
  ).mkString("\n")

// this make it look like:
//   A B C
//1  X . O
//2  . X O
//3  . . X
```

## Interacting with the World

The last bit for us to interact with the world is to define constraints on how we talk to the **Console**:

```scala
trait Console[F[_]] {
  def readLine: F[String]

def printLine(text: String): F[Unit]
}
```

Also, let’s keep all the text not related to game mechanics in one place:

```scala
object Text {
  val invalidChoice = "Invalid choice, try again"
  val draw = "It was a draw"
  def winner(square: Square) = s"${square.encode} won"
  def nextMove(square: Square) = s"Your next move with ${square.encode}:"
  def chooseText(square: Square) = s"Choose initial symbol: ${square.encode} or ${Square.opposite(square).encode}"

val validInputs: String = coordinates.combinations.map(_.encode).mkString(" ")
}
```

And finally, the game interaction with the **Console**:

```scala
def run[F[_]: Monad](implicit console: Console[F], applicative: Applicative[F]): F[ExitCode] = {
  import console._, applicative._

  // we retry if user input was invalid
  def retry[T](readValue: F[Option[T]]): F[T] = readValue.flatMap { element =>
    if(element.isDefined) pure(element.get) else printLine(Text.invalidChoice) > retry(readValue)
  }

  // we show current state of the game and take user input
  def readGame(square: Square, currentGame: Game): F[Game] = for {
    _ <- printLine(displayGameOnConsole(currentGame))
    _ <- printLine(Text.nextMove(square))
    _ <- printLine(Text.validInputs)
    nextStage <- retry(readLine.map(Decoder[Coordinate].decode).map(_.flatMap(coordinates => game.move(currentGame, Move(square, coordinates)))))
  } yield nextStage

  // we loop the game until someone wins or there is a draw
  def gameLoop(square: Square, gameState: Game): F[Option[Square]] = readGame(square, gameState).flatMap{ currentGame =>
    if(game.full(currentGame))
      pure(None: Option[Square])
    else {
      val winner = game.winner(currentGame)
      if(winner.isDefined)
        pure(winner)
      else
        gamo(Square.opposite(square), currentGame)
    }
  }

for {
    _ <- printLine(Text.chooseText(X)) // choose symbol
    square <- retry(readLine.map(Decoder[Square].decode))
    result <- gameLoop(square, game.empty)
    _ <- printLine(result.map(Text.winner).getOrElse(Text.draw))
  } yield ExitCode.Success
}
```

To run the game, we can provide it with an interpreter and `Main` method:

```scala
object Main extends IOApp {

implicit val ioInstance: Console[IO] = new Console[IO] {
    import scala.io.StdIn

override def readLine: IO[String] = IO(StdIn.readLine)
    override def printLine(text: String): IO[Unit] = IO(println(text))
  }

override def run(args: List[String]): IO[ExitCode] = {
    TicTacToe.run[IO]
  }
}
```

## Conclusion

When learning any programming concept, it’s common to look for simple problems to solve that would gradually increase in complexity. You might try [Project Euler](https://projecteuler.net/), [HackerRank](https://www.hackerrank.com/), [Codility](https://app.codility.com/programmers/) or [Exercism](https://exercism.io/tracks). If you reach a plateau, try building some simple games you love like [Chess](https://www.youtube.com/watch?v=ScS8Q32lMxA), TicTacToe, or [Hangman](https://en.wikipedia.org/wiki/Hangman) (I'm gonna work on this soon!).

## Resources

* [Source code](https://github.com/dmarticus/scala-games/tree/master/src/main/scala/games/TicTacToe) — Git repository with all the code + tests

* Inspiration for this writeup is the [**FP to the Max**](https://www.youtube.com/watch?v=sxudIMiOo68) video tutorial
