//package games
//
//import cats.syntax.apply._
//import cats.syntax.flatMap._
//import cats.syntax.functor._
//import cats.{Applicative, Monad}
//import cats.effect.ExitCode
//import games.typeClasses.Console
//
//package object Connect4 {
//  def run[F[_]: Monad](implicit console: Console[F],
//                       applicative: Applicative[F]): F[ExitCode] = {
//    import console._, applicative._
//
//    for {
//      _ <- ???
//    } yield ExitCode.Success
//  }
//}
