trait Plus[A] {
  def plus(a1: A, a2: A): A
}

def plus[A: Plus](a1: A, a2: A): A = implicitly[Plus[A]].plus(a1, a2)

val a = 2
val b = 3

implicit object PlusInt extends Plus[Int] {
  override def plus(a1: Int, a2: Int): Int = a1 + a2
}

val c = plus(a, b)


//def sum(xs: List[Int]): Int = xs.foldLeft(10) { _ + _ }

trait Monoid[A] {
  def mappend(a1: A, a2: A): A
  def mzero: A
}

implicit object IntMonoid extends Monoid[Int] {
  def mappend(a: Int, b: Int): Int = a + b
  def mzero: Int = 0
}

implicit object StringMonoid extends Monoid[String] {
  def mappend(a: String, b: String): String = a + b
  def mzero = ""
}

trait FoldLeft[F[_]] {
  def foldLeft[A, B](xs: F[A], b: B, f: (B, A) => B): B
}

object FoldLeft {
  implicit val foldLeftList: FoldLeft[List] = new FoldLeft[List] {
    override def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B): B = xs.foldLeft(b)(f)
  }
}


def sum[M[_]: FoldLeft, A: Monoid](xs: M[A]): A = {
  val m = implicitly[Monoid[A]]
  val fl = implicitly[FoldLeft[M]]
  fl.foldLeft(xs, m.mzero, m.mappend)
}
sum(List(1,2,3,4))
sum(List("a", "b", "c"))

trait MonoidOp[A] {
  val F: Monoid[A]
  val value: A
  def |+|(a2: A) = F.mappend(value, a2)
}

implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
  override val value: A = a
  override val F: Monoid[A] = implicitly[Monoid[A]]
}

3 |+| 4

"a" |+| "b" |+| "c"
