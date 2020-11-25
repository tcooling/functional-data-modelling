package fdm

import scala.annotation.tailrec

/**
 * Scala's type system supports _subtyping_, a feature not common in functional programming
 * languages. Subtyping allows the compiler to see and enforce subset / superset relationships
 * between different types. For example, all dogs are animals, and this fact can be encoded using
 * subtyping in any programming language that supports the feature.
 */
object subtyping {
  trait Animal
  trait Dog       extends Animal
  trait Cat       extends Animal
  object Midnight extends Cat
  object Ripley   extends Dog

  // A <: B means A is a subtype of B, A extends B
  // Dog <: Animal means Dog is a subtype of Animal

  // Animal >: Dog means Animal is superset of dog
  // Animal contains dog, flipped subtype

  type IsSubtypeOf[A, B >: A]
  type IsSupertypeOf[A, B <: A]

  /**
   * EXERCISE 1
   *
   * Determine the relationship between `Animal` and `Dog`, and encode that using either
   * `IsSubtypeOf` or `IsSupertypeOf`.
   */
  type Exercise1 = Animal IsSupertypeOf Dog

  /**
   * EXERCISE 2
   *
   * Determine the relationship between `Dog` and `Animal` (in that order), and encode that using
   * either `IsSubtypeOf` or `IsSupertypeOf`.
   */
  type Exercise2 = Dog IsSubtypeOf Animal

  /**
   * EXERCISE 3
   *
   * Determine the relationship between `Animal` and `Cat`, and encode that using either
   * `IsSubtypeOf` or `IsSupertypeOf`.
   */
  type Exercise3 = Animal IsSupertypeOf Cat

  /**
   * EXERCISE 4
   *
   * Determine the relationship between `Cat` and `Animal` (in that order), and encode that using
   * either `IsSubtypeOf` or `IsSupertypeOf`.
   */
  type Exercise4 = Cat IsSubtypeOf Animal

  /**
   * EXERCISE 5
   *
   * In generic data types and methods, the type operators `<:` ("is a subtype of") and `>:`
   * ("is a supertype of") may be used to enforce subtyping / supertyping constraints on type
   * parameters.
   *
   * In this exercise, use the right type operator such that the examples that should compile do
   * compile, but the examples that should not compile do not compile.
   */
  def originalIsInstanceOf[A <: Animal, B](a: A): Unit = ???

  // Fix with bounds such that first 2 compile, e.g. Dog/Cat are supertypes of Ripley/Midnight
  def isInstanceOf[A, B >: A](a: A): Unit = ???

  // Reminder - Ripley (Dog), Midnight (Cat)
  lazy val mustCompile1: Unit = isInstanceOf[Ripley.type, Dog](Ripley)
  lazy val mustCompile2: Unit = isInstanceOf[Midnight.type, Cat](Midnight)
  // lazy val mustNotCompile1: Unit = isInstanceOf[Ripley.type, Cat](Ripley)
  // lazy val mustNotCompile2: Unit = isInstanceOf[Midnight.type, Dog](Midnight)

  /**
   * EXERCISE 6
   *
   * The following data type imposes no restriction on the guests who stay at the hotel. Using
   * the subtyping or supertyping operators, ensure that only animals may stay at the hotel.
   */
  final case class OriginalPetHotel[A](rooms: List[A])

  // A must be a subtype of Animal
  final case class PetHotel[A <: Animal](rooms: List[A])
}

/**
 * Generic ("parametrically polymorphic") data types with simple, unadorned type parameters are
 * referred to as "invariant". For some invariant generic data type `Box[A]`, there is no
 * relationship between the types `Box[A]` and `Box[B]`, unless `A` and `B` are the same types,
 * in which case, `Box[A]` is the same type as `Box[B]`. If there is a subtyping relationship
 * between `A` and `B`, for example, if `A <: B`, then `Box[A]` is unrelated to (a unique type
 * from) `Box[B]`. In Java, all generic types are invariant, which leads to some significant pains.
 */
object invariance {
  trait Animal
  trait Dog       extends Animal
  trait Cat       extends Animal
  object Midnight extends Cat
  object Ripley   extends Dog

  // +A means you can only return A
  // -A means you only put A in method params

  // Pet hotel is invariant in A
  // Invariance - Pet hotel of dog vs pet hotel of animal are of no relation to each other
  trait PetHotel[-A <: Animal] {
    def book(pet: A): Unit = println(s"Booked a room for ${pet}")
  }

  def bookRipley(dogHotel: PetHotel[Dog]): Unit = dogHotel.book(Ripley)

  def bookMidnight(catHotel: PetHotel[Cat]): Unit = catHotel.book(Midnight)

  def printAnimal(animal: Animal): Unit = println(animal)

  printAnimal(Ripley)
  printAnimal(Midnight)

  def exampleAnimalHotel(animalHotel: PetHotel[Animal]): Unit = {
    animalHotel.book(Midnight: Animal)
    animalHotel.book(Ripley: Animal)
  }

  /**
   * EXERCISE 1
   *
   * Assuming you have a `PetHotel` that can book any kind of `Animal`. Use this pet hotel to try
   * to call the `bookRipley` and `bookMidnight` functions, to book these two pets at the hotel.
   *
   * Take note of your findings.
   */
  def bookMidnightAndRipley(animalHotel: PetHotel[Animal]): Unit = {
    // Dog <: Animal, Dog is a subtype of Animal
    // Want PetHotel[Dog] >: PetHotel[Animal], PetHotel[Dog] should be supertype of PetHotel[Animal]
    // Want that Dog is subtype of animal to imply that PetHotel of Dog is supertype of PetHotel Animal
    bookRipley(animalHotel) // bookRipley expects PetHotel[Dog] but gets PetHotel[Animal]
    bookMidnight(animalHotel) // bookMidnight expects PetHotel[Cat] but gets PetHotel[Animal]
  }

  // 1. Can always feed a function a subtype of what it is looking for
  // 2. bookRipley is looking for PetHotel[Dog]
  // 3. We can feed bookRipley a subtype of PetHotel[Dog]
  // 4. Assume PetHotel[Animal] is a subtype of PetHotel[Dog]
  // 5. THEN: We can feed bookRipley a PetHotel[Animal]

  // Below: not true now but if it were would solve our problems
  // <: Subtype
  // If Dog <: Animal implies PetHotel[Animal] <: PetHotel[Dog]
  // A <: B implies PetHotel[A] >: PetHotel[B]

  def expectAnimal(animal: Animal) = ()

  expectAnimal(Ripley)

  trait PetDeliveryService[+A <: Animal] {
    def acceptDelivery: A
  }

  def acceptRipley(delivery: PetDeliveryService[Ripley.type]): Ripley.type = delivery.acceptDelivery

  def acceptDog(delivery: PetDeliveryService[Dog]): Dog = delivery.acceptDelivery

  def acceptAnimal(delivery: PetDeliveryService[Animal]): Animal = delivery.acceptDelivery

  /**
   * EXERCISE 2
   *
   * Assuming you have a `PetDeliveryService` that can deliver `Ripley`, try to use the service
   * to call `acceptRipley` (to accept delivery of `Ripley`), `acceptDog` (to accept delivery of
   * a dog, not necessarily Ripley), and `acceptAnimal` (to accept delivery of an animal, not
   * necessarily a dog).
   *
   * Take note of your findings.
   */
  def acceptRipleyDogAnimal(delivery: PetDeliveryService[Ripley.type]): Unit = {
    // Expect: PetDeliveryService[Dog]
    // Actual: PetDeliveryService[Ripley.type]
    // PetDeliveryService[Ripley.type] <: PetDeliveryService[Dog]
    // Want A <: B to imply PetDeliveryService[A] <: PetDeliveryService[B]
    acceptRipley(delivery)
    acceptDog(delivery)
    acceptAnimal(delivery)
  }
}

/**
 * So-called declaration-site variance is a feature of Scala that allows you to declare, when you
 * define a data type, whether each type parameter should be invariant, covariant, or contravariant.
 * Invariant is the default, and confers no special treatment. Covariance and contravariance, on
 * the other hand, can help improve the usability of generic data types by allowing Scala to
 * safely infer suptyping and supertype relationships between the generic data types when their
 * type parameters have subtyping and supertype relationships.
 *
 * Covariance can be used on any type parameter that appears in "output" position from all methods
 * of a generic data type. The intuition is that covariance on a type parameter means that the
 * data type has a "surplus" (+) of elements of that type "coming out" of it.
 */
object covariance {
  trait Animal
  trait Dog       extends Animal
  trait Cat       extends Animal
  object Midnight extends Cat
  object Ripley   extends Dog

  /**
   * EXERCISE 1
   *
   * Declare `PetDeliveryService` to be covariant on the type parameter `A`. This is legal since
   * `A` never occurs as input to any method on `PetDeliveryService` (it occurs only as output of
   * the `acceptDelivery` method).
   */
  // Covariance, think of the plus (+) means PetDeliveryService has surplus of A's, stream of A's etc
  // Add + sign for subtyping, verify that none of methods inside this class accept A values
  trait PetDeliveryService[+A <: Animal] {
    def acceptDelivery: A

    // Below does not compile as no PetDeliveryService methods can accept an A
    //def book(a: A): Unit= ()

    // Below works, cannot assume A1 is an A
    def workBook[A1 >: A](a: A1): Unit = ()
  }

  def acceptRipley(delivery: PetDeliveryService[Ripley.type]): Ripley.type = delivery.acceptDelivery

  def acceptDog(delivery: PetDeliveryService[Dog]): Dog = delivery.acceptDelivery

  def acceptAnimal(delivery: PetDeliveryService[Animal]): Animal = delivery.acceptDelivery

  /**
   * EXERCISE 2
   *
   * Assuming you have a `PetDeliveryService` that can deliver `Ripley`, try to use the service
   * to call `acceptRipley` (to accept delivery of `Ripley`), `acceptDog` (to accept delivery of
   * a dog, not necessarily Ripley), and `acceptAnimal` (to accept delivery of an animal, not
   * necessarily a dog).
   *
   * Take note of your findings.
   */
  def acceptRipleyDogAnimal(delivery: PetDeliveryService[Ripley.type]): Unit = {
    // Making A (on the PetDeliveryService) covariant implies that for two types C (Cat) and D (Dog) where D is a
    // subtype of C, then List[D] is a subtype of List[C]
    acceptRipley(delivery)
    acceptDog(delivery)
    acceptAnimal(delivery)
  }

  /**
   * EXERCISE 3
   *
   * The rules for covariance imply that in any method that *wants* to take a covariant type
   * parameter as input, it must instead allow any supertype of the type parameter as input.
   *
   * This makes Scala's type system sound. Without it, it would not be safe to allow subtyping
   * on the generic data type based on subtyping of the type parameter.
   *
   * Following the pattern shown in `concat`, make an `append` method that compiles.
   */
  sealed trait List[+A] {
    def concat[A1 >: A](that: List[A1]): List[A1] = ???

    def append[A1 >: A](a: A1): List[A1] = ???
  }
}

/**
 * Contravariance can be used on any type parameter that appears in "input" position from all
 * methods of a generic data type. The intuition is that contravariance on a type parameter means
 * that the data type has a "deficit" (-) of elements of that type, requiring you feed them in.
 */
object contravariance {
  trait Animal
  trait Dog       extends Animal
  trait Cat       extends Animal
  object Midnight extends Cat
  object Ripley   extends Dog

  /**
   * EXERCISE 1
   *
   * Declare `PetHotel` to be contravariant on the type parameter `A`. This is legal since `A`
   * never occurs as output from any method on `PetHotel` (it occurs only as input to the `book`
   * method).
   */
  // A <: B implies F[A] >: F[B], A is a subtype of B implies F[A] is a supertype of F[B]
  // Contravariance = - sign
  // Cannot produce A's only accept them
  trait PetHotel[-A <: Animal] {
    def book(pet: A): Unit = println(s"Booked a room for ${pet}")

    // Below returns A's so will not compile
    // def wontCompile(): A = ???
  }

  // +A means you can only return A
  // -A means you only put A in method params

  def bookRipley(dogHotel: PetHotel[Dog]): Unit = dogHotel.book(Ripley)

  def bookMidnight(catHotel: PetHotel[Cat]): Unit = catHotel.book(Midnight)

  /**
   * EXERCISE 2
   *
   * Assuming you have a `PetHotel` that can book any kind of `Animal`. Use this pet hotel to try
   * to call the `bookRipley` and `bookMidnight` functions, to book these two pets at the hotel.
   *
   * Take note of your findings.
   */
  def bookMidnightAndRipley(animalHotel: PetHotel[Animal]): Unit = {
    bookRipley(animalHotel)
    bookMidnight(animalHotel)
  }

  /**
   * EXERCISE 3
   *
   * The rules for contravariance imply that in any method that *wants* to take another generic
   * data structure with the same type parameter, it must instead allow that type parameter to be
   * any subtype of the type parameter.
   *
   * This makes Scala's type system sound.
   *
   * Following the pattern shown in `merge`, make a `fallback` method that compiles.
   */
  sealed trait Consumer[-A] {
    def merge[A1 <: A](that: Consumer[A1]): Consumer[A1] = ???

    def fallback[A1 <: A](that: Consumer[A1]): Consumer[A1] = ???
  }
}

/**
 * Type parameters are channels of information: and as channels, they can be used, or ignored.
 * Different types can be used to "ignore" a type parameter depending on whether the parameter is
 * declared as covariant or contravariant (or invariant).
 */
object variance_zeros {

  /**
   * EXERCISE 1
   *
   * The type `Nothing` can be used when a covariant type parameter is not being used. For example,
   * an empty list does not use any element type, because it has no elements.
   */
  // Possible due to subtyping due to Nothing
  type Answer1           = Nothing
  type UnusedListElement = List[Answer1]

  /**
   * EXERCISE 2
   *
   * The type `Any` can be used when a contravariant type parameter is not being used. For example,
   * a constant function does not use its input element.
   */
  type Answer2                 = Any
  type UnusedFunctionInput[+B] = Answer2 => B
}

// TODO: complete this section
object advanced_variance {

  /**
   * EXERCISE 1
   *
   * Given that a workflow is designed to consume some input, and either error or produce an
   * output value, choose the appropriate variance for the workflow type parameters.
   */
  // TODO: which variance to use?
  final case class Workflow[Input, Error, Output](run: Input => Either[Error, Output]) {
    def map[NewOutput](f: Output => NewOutput): Workflow[Input, Error, NewOutput] = Workflow(i => run(i).map(f))

    /**
     * EXERCISE 2
     *
     * Add the appropriate variance annotations to the following method, and see if you can
     * implement it by following its types.
     */
    @tailrec
    def flatMap[NewOutput <: Output](f: Output => Workflow[Input, Error, NewOutput]): Workflow[Input, Error, NewOutput] =
      Workflow.apply(run).flatMap(f)

    /**
     * EXERCISE 3
     *
     * Add the appropriate variance annotations to the following method, and see if you can
     * implement it by following its types.
     */
    def fallback(that: Workflow[Input, Error, Output]): Workflow[Input, Error, Output] = Workflow(i => run(i) match {
      case Left(_) => that.run(i)
      case result@Right(_) => result
    })
  }
}
