package fdm

object phantom_types {

  /**
   * EXERCISE 1
   *
   * Add a phantom type parameter to `Socket`, which can keep track of the state of the socket:
   * either `Created` or `Connected`. Use this type parameter in the methods below to improve their
   * type safety.
   */
  // Type params only exist here to track the state
  type Created
  type Connected

  // Don't store any runtime info at all in type parameter
  trait Socket[State]

  // Makes much safer, cannot read from non connected socket
  def createSocket(): Socket[Created] = ???

  def connectSocket(address: String, socket: Socket[Created]): Socket[Connected] = ???

  def readSocket(socket: Socket[Connected]): Array[Byte] = ???

  /**
   * EXERCISE 2
   *
   * Introduce a type parameter to this data type to model whether a `Path` is a file, a directory,
   * or either a file or a directory. Use this to improve the type safety of the `readFile` and
   * `listDirectory` methods.
   *
   * Note: In order to ensure safety, you will have to make the constructors of `Path` private, so
   * that outside code cannot call those constructors with just any type parameter. This is a
   * requirement of using phantom types properly.
   */
  type File
  type Directory

  // Sometimes it’s useful for a trait to be able to use the fields or methods of a class it is mixed in to, this
  // can be done by specifying a self type for the trait using `self =>`
  sealed trait Path[PathType] { self =>
    // In the implicit, Directory is the upper bound, PathType must be subtype of Directory
    // See: https://apiumhub.com/tech-blog-barcelona/scala-generics-generalized-type-constraints/
    // The implicit is used to ensure that the PathType is a subtype of Directory
    def dir(name: String)(implicit ev: PathType <:< Directory): Path[Directory] =
      Path.ChildOf(self, name)

    def file(name: String)(implicit ev: PathType <:< Directory): Path[File] =
      Path.ChildOf(self, name)
  }

  object Path {
    private case object Root                                         extends Path[Directory]
    private final case class ChildOf[A](path: Path[_], name: String) extends Path[A]

    def root: Path[Directory]                                                                                       = Root
    def test[A](path: Path[Either[File, Directory]], isFile: Path[File] => A, isDirectory: Path[Directory] => A): A = ???

    def getDirectory(path: String): Path[Directory] = ???
    def getFile(path: String): Path[File]           = ???
  }

  def readFile(path: Path[File]): String = ???

  def listDirectory(path: Path[Directory]): List[Path[Either[File, Directory]]] = ???

  def render(path: Path[_]): String = ???

  /**
   * EXERCISE 3
   *
   * Phantom types work well with intersection types (`with` in Scala 2.x). They have many
   * wide-ranging applications, including making builder patterns safer.
   *
   * Introduce a phantom type parameter for `PersonBuilder`, and arrange such that the setters
   * add a new type into a type intersection, and that the build method requires both age and name
   * to be set in order to build the person.
   *
   * Note: As before, you must make the constructors of the data type with a phantom type parameter
   * private, so they cannot be called from outside code.
   */
  type SetAge
  type SetName

  // With is an intersection type, creates new type from existing types
  class PersonBuilder[+SoFar] private (private val age: Option[Int], private val name: Option[String]) {
    def age(v: Int): PersonBuilder[SoFar with SetAge] = new PersonBuilder(age = Some(v), name = name)

    def name(s: String): PersonBuilder[SoFar with SetName] = new PersonBuilder(age = age, name = Some(s))
  }

  object PersonBuilder {
    val personBuilder: PersonBuilder[Any] = new PersonBuilder(None, None)

    // PersonBuilder[SetAge with SetName] is the same as PersonBuilder[SetAge & SetName]
    def build(personBuilder: PersonBuilder[SetAge with SetName]): Person =
      Person(personBuilder.name.get, personBuilder.age.get)
  }

  final case class Person(name: String, age: Int)

  import PersonBuilder.personBuilder

  // Parameters can be passed in in any order
  PersonBuilder.build(personBuilder.name("Sherlock Holmes").age(42))
  PersonBuilder.build(personBuilder.age(42).name("Sherlock Holmes"))

  // Must satisfy PersonBuilder intersection type in PersonBuilder.build (PersonBuilder[SetAge with SetName])
  // Below will not compile:
  // PersonBuilder.build(personBuilder.name("Sherlock Holmes"))
  // PersonBuilder.build(personBuilder.age(42))

  // TODO: possible to fix being able to set properties multiple times?
  val person: Person = PersonBuilder.build(personBuilder.name("Sherlock Holmes").age(42).age(52))
  person.age == 52

  // TODO: possible to ensure certain order of properties being set?
  // See: https://blog.codecentric.de/en/2016/02/phantom-types-scala/

}
