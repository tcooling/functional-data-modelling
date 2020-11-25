package fdm

/**
 * Scala data types constructed from enums and case classes may be *recursive*: that is, a top-
 * level definition may contain references to values of the same type.
 */
object recursive {

  /**
   * EXERCISE 1
   *
   * Create a recursive data type that models a user of a social network, who has friends; and
   * whose friends may have other friends, and so forth.
   */
  final case class User(name: String, friends: List[User])

  /**
   * EXERCISE 2
   *
   * Create a recursive data type that models numeric operations on integers, including addition,
   * multiplication, and subtraction.
   */
  sealed trait NumericExpression
  object NumericExpression {
    final case class Literal(value: Int)                                            extends NumericExpression
    final case class Addition(lhs: NumericExpression, rhs: NumericExpression)       extends NumericExpression
    final case class Multiplication(lhs: NumericExpression, rhs: NumericExpression) extends NumericExpression
    final case class Subtraction(lhs: NumericExpression, rhs: NumericExpression)    extends NumericExpression

  }

  /**
   * EXERCISE 3
   *
   * Create a `EmailTrigger` recursive data type which models the conditions in which to trigger
   * sending an email. Include common triggers like on purchase, on shopping cart abandonment, etc.
   */
  sealed trait EmailTrigger
  object EmailTrigger {
    case object OnPurchase                                         extends EmailTrigger
    final case class Both(left: EmailTrigger, right: EmailTrigger) extends EmailTrigger
  }

}

/**
 * As Scala is an eager programming language, in which expressions are evaluated eagerly, generally
 * from left to right, top to bottom, the tree-like data structures created with case classes and
 * enumerations do not contain cycles. However, with some work, you can model cycles. Cycles are
 * usually for fully general-purpose graphs.
 */
object cyclically_recursive {

  final case class Snake(food: Snake)

  /**
   * EXERCISE 1
   *
   * Create a snake that is eating its own tail. In order to do this, you will have to use a
   * `lazy val`.
   */
  // Use lazy when don't want to compute value now but later, when call evaluate on the lazy val
  lazy val snake: Snake = Snake(snake)

  /**
   * EXERCISE 2
   *
   * Create two employees "Tim" and "Tom" who are each other's coworkers. You will have to change
   * the `coworker` field from `Employee` to `() => Employee` (`Function0`), also called a "thunk",
   * and you will have to use a `lazy val` to define the employees.
   */
  final case class Employee(name: String, coworker: () => Employee)
  // Note: cannot pass in parameter by name for lazy case class field, need () => Employee
  // cannot do variable: => Employee
  // Can get round this in companion apply method

  // Would work without () => name but normally needed for recursive data types along with lazy vals
  lazy val tim: Employee = Employee("Tim", () => tom)
  lazy val tom: Employee = Employee("Tom", () => tim)

  // Verify linkage works
  tim.coworker() == tom

  /**
   * EXERCISE 3
   *
   * Develop a List-like recursive structure that is sufficiently lazy, it can be appended to
   * itself.
   */
  sealed trait LazyList[+A] extends Iterable[A]
  object LazyList {
    def apply[A](el: A): LazyList[A] = new LazyList[A] {
      def iterator: Iterator[A] = Iterator.apply(el)
    }

    // The syntax `=>` means a "lazy parameter". Such parameters are evaluated wherever they are
    // referenced "by name".
    def concat[A](left: => LazyList[A], right: => LazyList[A]): LazyList[A] =
      new LazyList[A] {
        def iterator: Iterator[A] = Iterator.concat(left).concat(right)
      }
  }

  lazy val infiniteList: LazyList[Int] = LazyList.concat(LazyList(1), infiniteList)
}