package fs2

import scala.concurrent.duration._

private[fs2] trait TaskPlatform {

  implicit class JvmSyntax[A](val self: Task[A]) {
    
    /**
     * Run this `Task` and block until its result is available. This will
     * throw any exceptions generated by the `Task`. To return exceptions
     * in an `Either`, use `unsafeAttemptRun()`.
     */
    def unsafeRun(): A = self.get.run match {
      case Left(e) => throw e
      case Right(a) => a
    }

    /** Like `unsafeRun`, but returns exceptions as values. */
    def unsafeAttemptRun(): Either[Throwable,A] =
      try self.get.run catch { case t: Throwable => Left(t) }

    /**
     * Run this `Task` and block until its result is available, or until
     * `timeout` has elapsed, at which point a `TimeoutException`
     * will be thrown and the `Future` will attempt to be canceled.
     */
    def unsafeRunFor(timeout: FiniteDuration): A = self.get.runFor(timeout) match {
      case Left(e) => throw e
      case Right(a) => a
    }

    /**
     * Like `unsafeRunFor`, but returns exceptions as values. Both `TimeoutException`
     * and other exceptions will be folded into the same `Throwable`.
     */
    def unsafeAttemptRunFor(timeout: FiniteDuration): Either[Throwable,A] =
      self.get.attemptRunFor(timeout).right flatMap { a => a }
  }
}
