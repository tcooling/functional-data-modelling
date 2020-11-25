package fdm

import java.util.UUID

/**
 * Some anti-patterns emerge when doing data modeling in Scala. Learning to spot and refactor such
 * anti-patterns is a very valuable skill that will keep data models precise and easy to maintain
 * in the face of changing business requirements.
 *
 */
/**
 * One anti-pattern common in object-oriented data modeling technique is to define different
 * interfaces for different aspects of a data model, relying on inheritance (which is a form of
 * type intersection) to combine multiple aspects into a new type. While this approach is flexible,
 * it's often _too_ flexible, allowing combinations that don't make sense, and making it hard to
 * deal with combinations in a principled way.
 *
 * In this case, refactoring to enumerations (sealed traits _without_ overlaps) and case classes
 * can not only disallow combinations that don't make sense, but give us a principled way to
 * handle all the combinations that do make sense.
 */
object eliminate_intersection {
  trait Event {
    def eventId: String
  }
  trait UserEvent extends Event {
    def userId: String
  }
  trait TimestampedEvent extends Event {
    def timestamp: java.time.Instant
  }
  trait DeviceEvent extends Event {
    def deviceId: String
  }

  object adt {

    /**
     * EXERCISE 1
     *
     * Create a pure case class / enumeration model of `Event`, which permits events which are
     * user events OR device events (but NOT both), and which permits events that have timestamps
     * or lack timestamps; but which always have event ids.
     */
    sealed trait Event {
      def eventId: String
      def timestamp: Option[java.time.Instant]
    }
    object Event {
      final case class UserEvent(eventId: String, userId: String, timestamp: Option[java.time.Instant])     extends Event
      final case class DeviceEvent(eventId: String, deviceId: String, timestamp: Option[java.time.Instant]) extends Event
    }

    // Below demonstrates why the above Event sealed trait may not be ideal
    // Potentially not ideal due to fixed type
    trait IdentifiedUUID {
      def id: UUID
    }

    // Could use a Type Class
    trait Identified[A] {
      def idOf(a: A): java.util.UUID
    }

    def idOf[A](a: A)(implicit identified: Identified[A]): java.util.UUID = identified.idOf(a)

    def printId[A: Identified](a: A): Unit = println(idOf(a))

  }
}

/**
 * Another anti-pattern is when all cases of an enumeration share the same field, with the same
 * type and the same meaning. This duplication makes maintenance of the hierarchy more difficult,
 * and also makes it difficult to take advantage of built-in functionality like the `copy` method
 * that comes for free with all case classes.
 *
 * In this case, we can apply an "extract product" refactoring, which turns the enumeration into a
 * case class, and pushes the differences between the cases deeper, into a field of the case class
 * (modeled with a new enumeration).
 */
object extract_product {

  /**
   * EXERCISE 1
   *
   * The following cases of the `AdvertisingEvent` enum all share the `pageUrl` and `data` fields,
   * which have the same type and meaning in each case. Apply the _extract product_ refactoring so
   * that  `AdvertisingEvent` becomes a case class, which stores `pageUrl` and `data`, and
   * introduce a new field called `eventType`, which captures event-specific details for the
   * different event types.
   */
  sealed trait OldAdvertisingEvent {
    def pageUrl: String
    def data: String
  }
  object OldAdvertisingEvent {
    final case class Impression(pageUrl: String, data: String)                 extends OldAdvertisingEvent
    final case class Click(pageUrl: String, elementId: String, data: String)   extends OldAdvertisingEvent
    final case class Action(pageUrl: String, actionName: String, data: String) extends OldAdvertisingEvent
  }

  // Refactor above as they all have same fields
  final case class AdvertisingEvent(pageUrl: String, data: String, event: SystemEventType)
  sealed trait EventType
  object EventType {
    case object Impression                      extends SystemEventType
    final case class Click(elementId: String)   extends SystemEventType
    final case class Action(actionName: String) extends SystemEventType
  }

  /**
   * EXERCISE 2
   *
   * The following cases of `Card` enum all share the `points` field, and this field has the same
   * type and meaning in each case. Apply the _extract product_ refactoring so that `Card` becomes a
   * case class, and introduce a new field called `cardType`, which captures card-specific details
   * for the different event types.
   */
  sealed trait OldCard
  object OldCard {
    final case class Clubs(points: Int)    extends OldCard
    final case class Diamonds(points: Int) extends OldCard
    final case class Spades(points: Int)   extends OldCard
    final case class Hearts(points: Int)   extends OldCard
  }

  // Refactor to below
  final case class Card(points: Int, cardType: CardType)
  sealed trait CardType
  object CardType {
    case object Clubs    extends CardType
    case object Diamonds extends CardType
    case object Spades   extends CardType
    case object Hearts   extends CardType
  }

  /**
   * EXERCISE 3
   *
   * Apply the _extract product_ refactoring to `Event`.
   */
  sealed trait OldEvent
  object OldEvent {
    final case class UserPurchase(userId: String, amount: Double, timestamp: java.time.Instant)        extends OldEvent
    final case class UserReturn(userId: String, itemId: String, timestamp: java.time.Instant)          extends OldEvent
    final case class SystemRefund(orderId: String, refundAmount: Double, timestamp: java.time.Instant) extends OldEvent
  }

  // Refactor to below
  final case class Event(timestamp: java.time.Instant, eventType: SystemEventType)
  sealed trait SystemEventType
  object SystemEventType {
    final case class UserPurchase(userId: String, amount: Double)        extends SystemEventType
    final case class UserReturn(userId: String, itemId: String)          extends SystemEventType
    final case class SystemRefund(orderId: String, refundAmount: Double) extends SystemEventType
  }
}

/**
 * Another anti-pattern occurs when many fields of a case class are optional (or `null`). This
 * indicates there is a "missing enumeration" that can be extracted from the case class, which
 * bundles fields that always appear together.
 *
 * In this case, the "extract sum" refactoring involves identifying patterns of optional fields
 * that always appear together, and pulling them out into the cases of a new enumeration.
 */
object extract_sum {
  final case class Job(title: String, salary: Double)

  final case class Enrollment(university: String, credits: Int, year: java.time.YearMonth)

  /**
   * EXERCISE 1
   *
   * Extract out a missing enumeration from the following data type.
   */
  final case class OriginalPerson(
    name: String,
    job: Option[Job],                                // employed
    employmentDate: Option[java.time.LocalDateTime], // if employed
    school: Option[Enrollment]                       // full-time student
  )

  // Refactor above to below
  final case class Person(name: String, status: CareerStatus)
  sealed trait CareerStatus
  object CareerStatus {
    final case class Employed(job: Job, employmentDate: java.time.LocalDateTime) extends CareerStatus
    final case class Student(school: Enrollment)                                 extends CareerStatus
  }

  /**
   * EXERCISE 2
   *
   * Extract out a missing enumeration from the following data type.
   */
  final case class OriginalEvent(
    deviceId: Option[String], // sensor initiated event
    userId: Option[String],   // user initiated event
    timestamp: java.time.Instant,
    reading: Option[Double], // for sensors
    click: Option[String],   // for user click events
    purchase: Option[String] // for user order events
  )

  // Refactor above to below
  final case class Event(timestamp: java.time.Instant, eventType: EventType)
  sealed trait EventType
  object EventType {
    final case class DeviceEvent(deviceId: String, reading: Option[Double]) extends EventType

    final case class UserEvent(
      userId: String,
      reading: Option[Double],
      click: Option[String],
      purchase: Option[String]
    ) extends EventType
  }

  /**
   * EXERCISE 3
   *
   * Extract out a missing enumeration from the following data type.
   */
  final case class OriginalCreditCard(
    digit16: Option[CardNumber.Digit16], // For VISA
    digit15: Option[CardNumber.Digit15], // For AMEX
    securityCode: Option[Digit4]         // For VISA
  )

  // Refactored credit card
  sealed trait CreditCard {
    def cardNumber: CardNumber
  }
  object CreditCard {
    final case class VISA(cardNumber: CardNumber.Digit16, securityCode: Digit4) extends CreditCard
    final case class AMEX(cardNumber: CardNumber.Digit15)                       extends CreditCard
  }

  sealed trait CardNumber
  object CardNumber {
    final case class Digit16(group1: Digit4, group2: Digit4, group3: Digit4, group4: Digit4) extends CardNumber
    final case class Digit15(group1: Digit4, group2: Digit4, group3: Digit4, group4: Digit3) extends CardNumber
  }

  final case class Digit4(v1: Digit, v2: Digit, v3: Digit, v4: Digit)
  final case class Digit3(v1: Digit, v2: Digit, v3: Digit)

  sealed trait Digit {
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
}

/**
 * Another anti-pattern is when a code base has (unsealed) traits that are used for storing data.
 * Such code that encourage runtime type checking using `isInstanceOf` or equivalent, which is
 * neither safe, nor easy to maintain as new subtypes are introduced into the code base.
 *
 * In this case, we can apply a "seal trait" refactoring, which involves sealing the data trait,
 * and relocating all classes that implement the trait into the same file, together with the trait.
 * This will ensure we match against all subtypes in a pattern match, making our code safer and
 * giving us compiler assistance to track down places we need to update when we add a new subtype.
 */
object seal_data_trait {

  /**
   * EXERCISE 1
   *
   * Apply the seal trait refactoring to the following `Document` data model, so the compiler will
   * know about and enforce
   */
  trait OriginalDocument {
    def documentId: String
  }
  trait OwnedDocument extends OriginalDocument {
    def ownerId: String
  }
  trait AnonymousDocument extends OriginalDocument
  trait GroupOwnedDocument extends OriginalDocument {
    def ownerIds: List[String]
  }

  // Refactor to below
  final case class Document(documentId: String, documentType: DocumentType)
  sealed trait DocumentType
  object DocumentType {
    final case class OwnedDocument(ownerId: String)             extends DocumentType
    case object AnonymousDocument                               extends DocumentType
    final case class GroupOwnedDocument(ownerIds: List[String]) extends DocumentType
  }
}

/**
 * Wildcard pattern matches on enumerations that change frequently are an anti-pattern, because
 * when new cases are added, there is no way for the compiler to remind developers to update
 * business logic involving the enumeration, which leads to subtle bugs in application logic.
 *
 * In this case, we can apply a "eliminate wildcard" refactoring that involves explicitly matching
 * against all cases of an enumeration.
 */
object eliminate_wildcard {
  trait Phone
  def sendText(phone: Phone, message: String): Unit = println(s"Sending a text to ${phone}: ${message}")

  /**
   * EXERCISE 1
   *
   * Eliminate the wild card pattern match, and instead move to matching each case individually.
   */
  def pageDeveloperOriginal(event: Event, onCall: Phone): Unit = event match {
    case Event.ServerDown => sendText(onCall, "The server is down, please look into it right away!")
    case _                =>
  }

  // Do not use wildcard pattern matches on unstable sub types (in an ideal world, not always possible)
  def pageDeveloper(event: Event, onCall: Phone): Unit = event match {
    case Event.ServerDown       => sendText(onCall, "The server is down, please look into it right away!")
    case Event.ServiceRestarted => sendText(onCall, "The server has been restarted.")
    case Event.BillingOverage   => sendText(onCall, "Please look into your billing charges!")
  }

  /**
   * EXERCISE 2
   *
   * Add another type of event, such as `BillingOverage`, which might require immediate attention
   * from a devops engineer. The Scala compiler will make you update existing code safely.
   */
  sealed trait Event
  object Event {
    case object ServerDown       extends Event
    case object ServiceRestarted extends Event
    case object BillingOverage   extends Event // Will now get non exhaustive match error on pattern match
  }
}

/**
 * Another anti-pattern is when code uses pattern matching to perform a so-called "type case".
 * Type cases are when the runtime type of a value is checked, rather than deconstructing the
 * value using one of the cases of the enumeration. Matching cases of an enumeration helps ensure
 * that no implementation details leak into pattern matches, discourages using traits in any other
 * way than for enumerations, and helps give developers a chance to update logic when new
 * fields are added to case classes.
 */
object eliminate_typecase {
  sealed trait OriginalEvent
  sealed trait IdentifiedEvent extends OriginalEvent {
    def id: String
  }
  final case class Click(href: String)                extends OriginalEvent
  final case class Purchase(id: String, item: String) extends IdentifiedEvent

  /**
   * EXERCISE 1
   *
   * Refactor this code to eliminate the type case. You may have to refactor the data model too.
   */
  def logIdentifiedEvents(event: OriginalEvent): Unit =
    event match {
      // Below case uses runtime type reflection, isInstanceOf, does not always work due to type erasure
      case identified: IdentifiedEvent => println("User event: " + identified.id)
      case _                           =>
    }

  // Refactor so not matching on type case
  sealed trait Event
  object Event {
    final case class Click(href: String)                extends Event
    final case class Purchase(id: String, item: String) extends Event
  }

  def logIdentifiedEvents(event: Event): Unit = event match {
    case Event.Purchase(id, _) => println("User event: " + id)
    case Event.Click(_)        => // do nothing
  }
}

/**
 * Another anti-pattern is creating many variables of the same type in a given scope by pattern
 * matching on recursive data structures and giving values of the same type different names.
 *
 * In this case, we can apply the "shadow variable" refactoring and deliberately choose to
 * shadow variables of the same name and type in outer scopes, reducing the possibility that we
 * accidentally refer to a variable in an outer scope.
 */
object nested_shadowing {

  /**
   * EXERCISE 1
   *
   * Identify the bug caused by too many variables of the same type in the same scope, and fix it
   * by applying the shadow variable refactoring.
   */
  def originalCount[A](list: List[A]): Int = list match {
    case Nil       => 0
    case _ :: tail => 1 + originalCount(list)
  }

  // Fixed version - rename tail to list
  // Note: disagree with this, does not feel right
  def count[A](list: List[A]): Int = list match {
    case Nil       => 0
    case _ :: list => 1 + originalCount(list)
  }

  sealed trait UserBehavior
  object UserBehavior {
    case object Purchase                                                 extends UserBehavior
    case object Return                                                   extends UserBehavior
    case object Anything                                                 extends UserBehavior
    final case class Sequence(first: UserBehavior, second: UserBehavior) extends UserBehavior
    final case class Not(behavior: UserBehavior)                         extends UserBehavior
  }

  import UserBehavior._

  /**
   * EXERCISE 2
   *
   * Identify the bug caused by too many variables of the same type in the same scope, and fix it
   * by applying the shadow variable refactoring.
   */
  def originalAnalyzePattern(b: UserBehavior): Boolean = b match {
    case Purchase                => true
    case Return                  => true
    case Anything                => false
    case Sequence(first, second) => originalAnalyzePattern(first) || originalAnalyzePattern(second)
    case Not(value)              => originalAnalyzePattern(b)
  }

  // Fixed version - shadow b parameter in Not case
  def analyzePattern(b: UserBehavior): Boolean = b match {
    case Purchase => true
    case Return   => true
    case Anything => false
    case Sequence(first, second) => originalAnalyzePattern(first) || originalAnalyzePattern(second)
    case Not(b) => originalAnalyzePattern(b)
  }
}
