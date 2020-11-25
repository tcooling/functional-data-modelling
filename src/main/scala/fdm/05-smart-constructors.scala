package fdm

/**
 * Sometimes we don't want to take the time to model data precisely. For example, we might want to
 * model an email address with a string, even though most strings are not valid email addresses.
 *
 * In such cases, we can save time by using a smart constructor, which lets us ensure we model
 * only valid data, but without complicated data types.
 */
object smart_constructors {

  // VISA security code: 3 digits
  // Digit: 0 - 9

  // For keeping bad data out of your application
  final case class SecurityCode(digit1: Digit, digit2: Digit, digit3: Digit)
  sealed trait Digit
  object Digit {
    case object _0 extends Digit
    case object _1 extends Digit
    case object _2 extends Digit
    case object _3 extends Digit
    case object _4 extends Digit
    case object _5 extends Digit
    case object _6 extends Digit
    case object _7 extends Digit
    case object _8 extends Digit
    case object _9 extends Digit
  }

  def process(securityCode: SecurityCode): Unit = ???

  // Need side effects: consider use of Zio?

  // Case class has copy method, can bypass private constructor and access copy method hence
  // the sealed abstract, use sealed abstract not final
  sealed abstract case class Email private (value: String)
  object Email {
    def fromString(email: String): Option[Email] =
      if (email.matches("""/\w+@\w+.com""")) Some(new Email(email) {}) else None
  }

  /**
   * EXERCISE 1
   *
   * Create a smart constructor for `NonNegative` which ensures the integer is always non-negative.
   */
  sealed abstract case class NonNegative private (value: Int)
  object NonNegative {
    def fromInt(value: Int): Option[NonNegative] = if (value >= 0) Some(new NonNegative(value) {}) else None
  }
  // Remember since the constructor is private, so the companion object needs to call it and
  // since it is an abstract class {} is used when creating the NonNegative number

  /**
   * EXERCISE 2
   *
   * Create a smart constructor for `Age` that ensures the integer is between 0 and 120.
   */
  sealed abstract case class Age private (value: Int)
  object Age {
    def fromInt(value: Int): Option[Age] = if (value >= 0 && value <= 120) Some(new Age(value) {}) else None
  }

  /**
   * EXERCISE 3
   *
   * Create a smart constructor for password that ensures some security considerations are met.
   */
  sealed abstract case class Password private (value: String)
  object Password {
    def validate(v: String): Either[String, Password] =
      if (v.length < 10) Left("Password not long enough")
      else if (v.contains(" ")) Left("Password should not contain spaces")
      else Right(new Password(v) {})
  }
}

object applied_smart_constructors {

  /**
   * EXERCISE 1
   *
   * Identify the weaknesses in this data type, and use smart constructors (and possibly other
   * techniques) to correct them.
   */
  final case class OldBankAccount(id: String, name: String, balance: Double, opened: java.time.Instant)

  // Do not need smart constructor for Bank account, should validate individual pieces
  final case class BankAccount(id: BankAccountId, holder: AccountHolder, balance: Double, opened: java.time.Instant)

  // Good to use new models for fields of bank account
  sealed abstract case class BankAccountId private (value: String)

  final case class AccountHolder(id: String, name: String)

  /**
   * EXERCISE 2
   *
   * Identify the weaknesses in this data type, and use smart constructors (and possibly other
   * techniques) to correct them.
   */
  final case class OriginalPerson(age: Int, name: String, salary: Double)

  final case class Person(age: Age, name: String, salary: Salary)

  sealed abstract class Age private (value: Int)
  object Age {
    def fromInt(value: Int): Option[Age] = if (value >= 0 && value <= 120) Some(new Age(value) {}) else None
  }

  sealed abstract case class Salary private (value: Double)
  object Salary {
    def fromDouble(value: Double): Option[Salary] = if (value >= 0) Some(new Salary(value) {}) else None
  }

  /**
   * EXERCISE 3
   *
   * Identify the weaknesses in this data type, and use smart constructors (and possibly other
   * techniques) to correct them.
   */
  final case class OriginalSecurityEvent(machine: String, timestamp: String, eventType: Int)
  object OriginalEventType {
    val PortScanning    = 0
    val DenialOfService = 1
    val InvalidLogin    = 2
  }

  final case class SecurityEvent(machine: String, timestamp: String, eventType: EventType)

  sealed abstract case class EventType private (value: Double)
  object EventType {
    private val eventTypes: Set[Int] = Set(
      OriginalEventType.DenialOfService,
      OriginalEventType.InvalidLogin,
      OriginalEventType.PortScanning
    )

    def fromInt(value: Int): Option[EventType] = if (eventTypes.contains(value)) Some(new EventType(value) {}) else None
  }
}
