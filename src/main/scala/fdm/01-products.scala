package fdm

/**
 * Scala supports tuples, which are a generic way to store two or more pieces of information at
 * the same time. For example, the tuple `("Alligator", 42)` stores both a string, as the first
 * component in the tuple, and an integer, as the second component in the tuple.
 *
 * Tuples come with lots of free functionality, including constructors, deconstructors, equality
 * and hash code, string representation for debugging, and a copy method.
 *
 * The elements of a tuple are accessed "by index". For example, `("a", 1)._1` retrieves the first
 * element of the tuple, which is the string `"a"`.
 *
 * Tuples are immutable: once constructed, they cannot change. However, there are simple ways to
 * created a new tuple from an existing tuple, in which some element has been "changed".
 *
 * Scala supports tuples of any "arity": that is, tuples can have as many elements as necessary.
 *
 * Tuples are examples of "anonymous products": they are types formed using "product composition"
 * of other types.
 */
object tuples {

  // Product - contain n different terms
  // Tuples only make sense when used temporarily, normally case class better so can get name access

  /**
   * EXERCISE 1
   *
   * Using both a type alias, and Scala's tuple types, construct a type called `Person` that can
   * hold both the name of a `Person` (as a `String`), together with the age of the `Person` (as an
   * `Int`).
   */
  type Name   = String
  type Age    = Int
  type Person = (Name, Age)

  /**
   * EXERCISE 2
   *
   * Using the `Person` type alias that you just created, construct a value that has type `Person`,
   * whose name is "Sherlock Holmes", and whose age is 42.
   */
  lazy val sherlockHolmes: Person = ("Sherlock Holmes", 42)

  /**
   * EXERCISE 3
   *
   * Using both a type alias, and Scala's tuple types, construct a type called `CreditCard` that can
   * hold a credit card number (as a `String`), a credit card expiration date (as a
   * `java.time.YearMonth`), a full name (as a `String`), and a security code (as a `Short`).
   */
  type CreditCard = (String, java.time.YearMonth, String, Short)

  /**
   * EXERCISE 4
   *
   * Using the `CreditCard` type alias that you just created, construct a value that has type
   * `CreditCard`, with details invented by you.
   */
  lazy val creditCard: CreditCard = ("123456789", java.time.YearMonth.of(2020, 11), "Tom", 123)
}

/**
 * Scala supports case classes, which are a generic way to store two or more pieces of
 * information at the same time, where each piece of information can have a user-defined label
 * associated with it. Case classes are nicer than tuples because the elements of the tuple can
 * be accessed by identifiers, instead of by indices. Like tuples, case classes come with lots of
 * free functionality, including constructors, deconstructors, equality and hash code, string
 * representation for debugging, and a copy method.
 *
 * Case classes can be thought of as "records" that have zero or more "fields"; or they can be
 * thought of as defining "tables" that have zero or more "columns".
 *
 * Case classes are immutable: once constructed, they cannot change. However, there are simple ways
 * to created a new value from an existing value, in which some field has been "changed".
 *
 * Case classes are examples of "labeled products": they are types formed using "product
 * composition"  of other types, where each term of the product can be accessed by a user-defined
 * (unique) label.
 */
object case_class_basics {

  // T2 and T3 will be the same type
  type T2 = (String, Int)
  type T3 = (String, Int)

  // Person1 and Person2 are different types
  final case class Person1(name: String, age: Int)

  final case class Person2(name: String, age: Int)

  /**
   * EXERCISE 1
   *
   * Using case classes, construct a type called `Person` that can hold both the name of a `Person`
   * (as a `String` stored in a field called `name`), together with the age of the `Person` (as an
   * `Int` stored in a field called `age`).
   */
  final case class Person(name: String, age: Int)

  /**
   * EXERCISE 2
   *
   * Using the `Person` case class that you just created, construct a value that has type `Person`,
   * whose name is "Sherlock Holmes", and whose age is 42.
   */
  lazy val sherlockHolmes: Person = Person(name = "Sherlock Holmes", age = 42)

  /**
   * EXERCISE 3
   *
   * Using case classes, construct a type called `CreditCard` that can hold a credit card number (as
   * a `String` stored in a field called `number`), a credit card expiration date (as a
   * `java.time.YearMonth` stored in a field called `expDate`), a full name (as a `String` stored in
   * a field called `name`), and a security code (as a `Short` in a field called `securityCode`).
   */
  // Good practice to make case classes final, generally don't want to inherit from a case class
  final case class CreditCard(number: String, exp: java.time.YearMonth, name: String, securityCode: Short)

  /**
   * EXERCISE 4
   *
   * Using the `CreditCard` case class that you just created, construct a value that has type
   * `CreditCard`, with details invented by you.
   */
  lazy val creditCard: CreditCard = CreditCard(
    number = "328723493274",
    exp = java.time.YearMonth.of(2020, 11),
    name = "Tom",
    securityCode = 123
  )
}

/**
 * Scala's case classes come equipped with useful functionality that all "data classes" should
 * have. In particular, they have equality and hash code built in, and it does exactly what you
 * would expect: operate on the fields of the case class.
 *
 * In addition, case classes have built in copy methods, which can be used to create new values
 * that are modified in some way with respect to an original value.
 */
object case_class_utilities {

  final case class Person(name: String, age: Int)

  /**
   * EXERCISE 1
   *
   * Construct and compare two values of type `Person` to see if they are equal to each other.
   * Compare using the `==` method, which is available on every value of type `Person`.
   */
  lazy val comparison: Boolean = Person(name = "Tom", age = 1) == Person(name = "Will", age = 2)

  /**
   * EXERCISE 2
   *
   * Construct and compute the hash codes of two values of type `Person` to see if they are equal to
   * each other. By law, if two values are equal, their hash codes must also be equal. Compute the
   * hash code of the `Person` values by calling the `hashCode` method, which is available on every
   * value of type `Person`.
   */
  lazy val tom: Person             = Person(name = "Tom", age = 1)
  lazy val will: Person            = Person(name = "Will", age = 2)
  lazy val hashComparison: Boolean = tom.hashCode() == will.hashCode()

  /**
   * EXERCISE 3
   *
   * Create a copy of the `sherlockHolmes` value, but with the age changed to be 10 years less than
   * it is currently. Create the copy using the `copy` method, which is available on every value
   * of type `Person`. Note that using named parameters, you need only specify the field you wish
   * to change in the copy operation.
   */
  lazy val sherlockHolmes: Person = Person(name = "Sherlock Holmes", age = 42)
  lazy val youngerHolmes: Person  = sherlockHolmes.copy(age = sherlockHolmes.age - 10)

  // Note: copy is hard to use on deeply nested models, could use optics library, e.g. monocle

}

/**
 * Both tuples and case classes can be used in pattern matching. Pattern matching can be used to
 * pull out fields from the products and store them in named variables, as well as to selectively
 * match for specific patterns of information stored in a value.
 */
object product_patterns {

  final case class Person(name: String, age: Int)

  lazy val sherlockHolmes: Person = Person(name = "Sherlock Holmes", age = 42)

  /**
   * EXERCISE 1
   *
   * In this pattern match on the value `sherlockHolmes`, the name is being extracted and stored
   * into a variable called `name`, while the age is being extracted and stored into a variable
   * called `age`. On the right hand side of the arrow (`=>`), use a Scala `println` statement to
   * print out the name and the age of the specified person.
   */
  def example1(): Unit = sherlockHolmes match {
    case Person(name, age) =>
      println(s"Name: $name")
      println(s"Age: $age")
  }

  // Deconstruction assignment
  val Person(name, age) = sherlockHolmes

  /**
   * EXERCISE 2
   *
   * Pattern match on this tuple and extract out the name of the product into a variable called
   * `name`, and the price of the product into a variable called `price`. Then use a Scala
   * `println` statement to print out the name and price of the product.
   */
  def example2(): Unit = ("Suitcase", 19.95) match {
    case (name, price) =>
      println(s"Name: $name")
      println(s"Price: $price")
  }

  final case class Employee(name: String, address: Address)

  final case class Address(street: String, number: Int)

  val dilbert: Employee = Employee(name = "Dilbert", address = Address(street = "Baker", number = 221))

  /**
   * EXERCISE 3
   *
   * Pattern match on `dilbert` and extract out and print the address number. This will involve
   * using a nested pattern match.
   */
  dilbert match {
    case Employee(_, Address(_, number)) => println(s"Address number: $number")
  }

  /**
   * EXERCISE 4
   *
   * Pattern matches can contain literal values, in which case those slots in the pattern are
   * matched using the `equals` method of the value. Pattern match on `dilbert` again, but this
   * time, with two cases: the case where the name is equal to `"Dilbert"`, and all other cases.
   * Print out the name in each case. Note the ordering of case evaluation, which proceeds from top
   * to bottom.
   */
  dilbert match {
    case Employee("Dilbert", _) => println("Dilbert")
    case Employee(name, _)      => println(name)
  }

  /**
   * EXERCISE 5
   *
   * Every case of a pattern match may contain a conditional expression, in which case the pattern
   * is matched only if both the base pattern matches, and the boolean expression evaluates to true.
   *
   * Pattern match on `dilbert` again and have two cases: the first one matches any address name
   * that starts with the string `"B"`, and a catch all case that matches all patterns. In both
   * cases, print out the name of the street.
   */
  dilbert match {
    case Employee(_, Address(street, _)) if street.startsWith("B") => println(s"Street: $street")
    case Employee(_, Address(street, _))                           => println(s"Street: $street")
  }

  /**
   * EXERCISE 6
   *
   * Any piece of a pattern match may be captured and placed into a new variable by using the as-
   * pattern syntax `x @ ...`, where `x` is any legal variable name. The variable introduced by an
   * as-pattern may be used both in any conditional, or inside the case of the pattern match.
   *
   * In this exercise, pattern match on dilbert, and give a name `a` to the inner `Address`, and
   * then print out that `a` in the case expression.
   */
  dilbert match {
    case Employee(_, a @ Address(_, _)) => println(s"Address: $a")
  }

  // Type casting is bad practice, e.g. a: Address in pattern match due to run time type check
  dilbert match {
    case Employee(_, a: Address) => println(s"Address: $a")
  }

  /**
   * EXERCISE 7
   *
   * Using the `|` symbol, you can match against two alternatives, providing neither introduces
   * new variables. In the context of case classes, this symbol provides a nice way to look for
   * one among a small number of constant values.
   *
   * In this exercise, match for the name "Dilbert" or the name "dilbert", and print out the
   * address of the employee.
   */
  dilbert match {
    case Employee("Dilbert" | "dilbert", address) => println(s"Address: $address")
    case _                                        => // do nothing
  }

  // Can use as pattern syntax when using '|'
  dilbert match {
    case Employee(name @ ("Dilbert" | "dilbert"), address) => println(s"$name lives at $address")
    case _                                                 => // do nothing
  }

  // Pattern matching is deconstructing and filtering
  // Can use arbitrary run time checks, e.g. if guards

  // Pattern match works on case classes due to Unapply method, so can build your own pattern matching
  // by adding an unapply method, will also work for for comprehensions etc.

}

/**
 * Scala's case classes can be generic, which means the types defined by case classes may have
 * generic type parameters. This allows building general-purpose and versatile data structures
 * that can have different types "plugged" into them in different contexts.
 */
object case_class_generics {

  /**
   * EXERCISE 1
   *
   * Convert this non-generic case class into a generic case class, by introducing a new type
   * parameter, called `Payload`, and use this type parameter to define the type of the field called
   * `payload` already defined inside the case class.
   */
  final case class EventNonGeneric(id: String, name: String, time: java.time.Instant, payload: String)

  // Event is now type constructor, was concrete, now it's generic due to the Type constructor
  final case class Event[Payload](id: String, name: String, time: java.time.Instant, payload: Payload)

  /**
   * EXERCISE 2
   *
   * Construct a type alias called `EventString`, which is an `Event` but with a `String` payload.
   */
  type EventString = Event[String]

  /**
   * EXERCISE 2
   *
   * Construct an event that has a payload type of `Int`.
   */
  // Don't need to specify type parameter, compiler will infer it
  lazy val eventInt: Event[Int] = Event(
    id = "123",
    name = "click",
    time = java.time.Instant.ofEpochMilli(23132L),
    payload = 42
  )

  /**
   * EXERCISE 3
   *
   * Convert this non-generic class into a generic class, by introducing a new type parameter,
   * called `Body`, which represents the body type of the request, and use this type parameter to
   * define the type of the field called `body` already defined inside the case class.
   */
  final case class RequestNonGeneric(body: EventNonGeneric, sender: String)

  final case class Request[T](body: Event[T], sender: String)

  // Much better to just use T here rather than e.g. [T, Body <: Event[T]]
  // More advanced way of doing the above with requirements for Body e.g.
  // Tips for more advanced Scala programmers: try not to use type bounds and
  // reconstruct at edges, e.g. below

  final case class AdvancedRequest[T](body: T, sender: String) {
    // Requires type T has specific structure
    def time[B](implicit ev: T <:< Event[B]): java.time.Instant = ev(body).time
  }
}
