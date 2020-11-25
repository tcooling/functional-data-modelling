package fdm

import java.util.UUID

import scala.annotation.tailrec

/**
 * The following exercises test your ability to model various entities using case classes.
 */
object product_modeling {

  /**
   * EXERCISE 1
   *
   * Using a case class, create a model of a product, which has a name, description, and a price.
   *
   */
  final case class Product(name: String, description: String, price: Double)

  /**
   * EXERCISE 2
   *
   * Using a case class, create a model of a a user profile, which has a picture URL, and text-
   * based location (indicating the geographic area where the user is from).
   */
  final case class UserProfile(pictureURL: java.net.URL, location: String)

  /**
   * EXERCISE 3
   *
   * Using a case class, create a model of an item that can be posted on LinkedIn's feed. This
   * item contains a subject and some text.
   */
  final case class FeedItem(subject: String, text: String)

  /**
   * EXERCISE 4
   *
   * Using a case class, create a model of an event, which has an event id, a timestamp, and a
   * map of properties (String/String).
   */
  final case class Event(eventId: String, timestamp: java.time.Instant, properties: Map[String, String])

}

/**
 * The following exercises test your ability to model various entities using enums.
 */
object sum_modeling {

  /**
   * EXERCISE 1
   *
   * Using an enum, create a model of a color, which could be `Red`, `Green`, `Blue`, or `Custom`,
   * and if `Custom`, then it should store `red`, `green`, and `blue` components individually, as
   * an integer (`Int`) value.
   */
  sealed trait Color
  object Color {
    case object Red                                          extends Color
    case object Green                                        extends Color
    case object Blue                                         extends Color
    final case class Custom(red: Int, green: Int, blue: Int) extends Color
  }

  /**
   * EXERCISE 2
   *
   * Using an enum, create a model of an web event, which could be either a page load for a certain
   * URL, a click on a particular button, or a click to a specific URL.
   */
  sealed trait WebEvent
  object WebEvent {
    final case class PageLoad(url: String)       extends WebEvent
    final case class ClickButton(button: String) extends WebEvent
    final case class ClickURL(url: String)       extends WebEvent
  }

  /**
   * EXERCISE 3
   *
   * Using an enum, create a model of an age bracket, which could be baby, child, young adult,
   * teenager, adult, mature adult, or senior adult.
   */
  sealed trait AgeBracket
  object AgeBracket {
    case object Baby        extends AgeBracket
    case object Child       extends AgeBracket
    case object YoungAdult  extends AgeBracket
    case object Teenager    extends AgeBracket
    case object Adult       extends AgeBracket
    case object MatureAdult extends AgeBracket
    case object SeniorAdult extends AgeBracket

  }

  /**
   * EXERCISE 4
   *
   * Using an enum, create a model of a step in a JSON pipeline, which could be transform,
   * aggregate, or save to file.
   * aggregate.
   */
  type Json
  sealed trait JsonPipelineStep
  object JsonPipelineStep {
    final case class Transform(fn: Json => Json)         extends JsonPipelineStep
    final case class Aggregate(fn: (Json, Json) => Json) extends JsonPipelineStep
    final case class SaveToFile(fn: Json => Unit)        extends JsonPipelineStep
  }

}

/**
 * The following exercises test your ability to model various entities using both case classes and
 * enums.
 */
object mixed_modeling {

  /**
   * EXERCISE 1
   *
   * Using only case classes and enums, create a model of an order for an e-commerce platform, which
   * would consist of a number of items, each with a certain price, and an overall price, including
   * shipping and handling charges.
   */
  final case class Order(
    id: UUID,
    customer: Customer,
    shipmentAndHandlingCharges: Double,
    items: List[OrderItem]
  ) {
    def itemsTotal: Double = items.map(_.orderTotal).sum
    def totalPrice: Double = itemsTotal + shipmentAndHandlingCharges
  }

  final case class Customer(id: UUID, name: String, email: String)

  final case class OrderItem(product: OrderProduct, quantity: Int) {
    def orderTotal: Double = product.price * quantity
  }

  final case class OrderProduct(id: UUID, name: String, price: Double)

  /**
   * EXERCISE 2
   *
   * Using only case classes and enums, create a model of an `Email`, which contains a subject,
   * a body, a recipient, and a from address.
   */
  final case class Email(subject: String, body: String, recipient: List[EmailAddress], fromAddress: EmailAddress)

  final case class EmailAddress(address: String)

  /**
   * EXERCISE 3
   *
   * Using only case classes and enums, create a model of a page layout for a content-management
   * system, which could consist of predefined elements, such as a news feed, a photo gallery,
   * and other elements, arranged in some well-defined way relative to each other.
   */
  final case class PageLayout(element: PageElement)

  sealed trait PageElement
  object PageElement {
    case object NewsFeed                                 extends PageElement
    case object PhotoGallery                             extends PageElement
    final case class Row(elements: List[PageElement])    extends PageElement
    final case class Column(elements: List[PageElement]) extends PageElement
  }

  lazy val layout: PageLayout = PageLayout(element = PageElement.Column(elements = List(
    PageElement.Row(elements = List(PageElement.NewsFeed)),
    PageElement.Row(elements = List(PageElement.PhotoGallery))
  )
  )
  )

  /**
   * EXERCISE 4
   *
   * Using only case classes and enums, create a model of a rule that describes the conditions for
   * triggering an email to be sent to a shopper on an e-commerce website.
   */
  sealed trait EmailTriggerRule
  object EmailTriggerRule {
    final case class SendEmail(email: Email, event: ECommerceEvent) extends EmailTriggerRule
    case object DoNotSend                                           extends EmailTriggerRule
  }

  sealed trait ECommerceEvent
  object ECommerceEvent {
    case object Purchase            extends ECommerceEvent
    case object RecommendedProducts extends ECommerceEvent
  }

}

object basic_dm_graduation {

  sealed trait Command
  object Command {
    case object Look                      extends Command
    case object Quit                      extends Command
    final case class LookAt(what: String) extends Command
    final case class Go(where: String)    extends Command
    final case class Take(item: String)   extends Command
    final case class Drop(item: String)   extends Command
    final case class Fight(who: String)   extends Command

    def fromString(string: String): Option[Command] =
      string.trim.toLowerCase.split("\\s+").toList match {
        case "go" :: where :: Nil          => Some(Go(where))
        case "look" :: Nil                 => Some(Look)
        case "look" :: "at" :: what :: Nil => Some(LookAt(what))
        case "take" :: item :: Nil         => Some(Take(item))
        case "drop" :: item :: Nil         => Some(Drop(item))
        case "fight" :: who :: Nil         => Some(Fight(who))
        case ("quit" | "exit") :: Nil      => Some(Quit)
        case _                             => None
      }
  }

  /**
   * EXERCISE
   *
   * Using case classes and sealed traits (and whatever other data types you like), design a game
   * world that can be used to play a simple text-based role playing game.
   *
   * The data type should model the player, non-player characters, and items available to pick up
   * or drop in the game world.
   */
  final case class State(currentLocation: Location, player: Character, gameMap: GameMap) {
    def items: List[Item] =
      gameMap.itemsAt.getOrElse(currentLocation, Nil)

    def npcs: List[Character] =
      gameMap.npcsAt.getOrElse(currentLocation, Nil)

    def describe: String =
      currentLocation.description + "\n"
    (if (items.nonEmpty) {
       "Around you, you see the following items:\n" + items.map(_.description).mkString("\n")
     } else "") +
      (if (npcs.nonEmpty) {
         "Nearby, you notice the following characters:\n" + npcs.map(_.name).mkString("\n")
       } else "")
  }

  final case class Character(
    name: String,
    stats: CharStats,
    clazz: CharClass,
    status: CharStatus,
    equipped: List[Item],
    inventory: List[Item]
  )

  final case class Item(name: String, description: String, itemType: ItemType)
  sealed trait ItemType
  object ItemType {
    final case class Weapon(damage: Int, durability: Int) extends ItemType
    final case class Food(energyBoost: Int)               extends ItemType
    final case class HealingPotion(healthBoost: Int)      extends ItemType
  }

  final case class Location(name: String, description: String)

  final case class GameMap(
    set: Set[Location],
    connected: Location => Set[Location],
    itemsAt: Map[Location, List[Item]],
    npcsAt: Map[Location, List[Character]]
  )

  final case class CharStats(health: Int, charClass: CharClass, charStatus: CharStatus)

  sealed trait CharClass
  object CharClass {
    case object Wizard  extends CharClass
    case object Warrior extends CharClass
  }

  sealed trait CharStatus
  object CharStatus {
    case object Normal   extends CharStatus
    case object Poisoned extends CharStatus
    case object Cursed   extends CharStatus
  }

  def describe(state: State): String =
    "You are playing this game."

  import Command._

  def process(state: State, command: Command): (String, Option[State]) =
    command match {
      case Look => (state.describe, Some(state))

      case Quit => ("You quitted", None)

      case LookAt(what) =>
        state.items.find(_.name == what) match {
          case Some(value) => (value.description, Some(state))
          case None        => (s"There is no ${what} to look at.", Some(state))
        }

      case Go(where)  => ???
      case Take(item) => ???
      case Drop(item) => ???
      case Fight(who) => ???
    }

  def main(args: Array[String]): Unit = {
    @tailrec
    def loop(state: State): Unit = {
      println(describe(state))

      val line = scala.io.StdIn.readLine()

      Command.fromString(line) match {
        case None =>
          println("Unrecognized command")
          loop(state)

        case Some(command) =>
          process(state, command) match {
            case (output, next) =>
              println(output)
              next match {
                case Some(value) => loop(value)
                case None        => println("Goodbye!")
              }
          }
      }
    }
  }
}
