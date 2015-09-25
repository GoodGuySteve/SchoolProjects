object Tester {
  case class Test[T](name: String, f: () => T, compare: T) {
    lazy val saw = try {
      f()
    } catch {
      case _: NotImplementedError => "NOT IMPLEMENTED"
      case e: Throwable => e.getMessage
    }
    lazy val passed = saw == compare

    def asString(): String = {
      val passedString = 
        if (passed) "passed" else "FAILED"
      val afterHeader =
        if (passed) "" else "\n\tSaw: " + saw + "\n\tExpected: " + compare
      name + ": " + passedString + afterHeader
    }
  }

  case class TestSuite(tests: Seq[Test[_]]) {
    def runSuite() {
      tests.foreach(test => println(test.asString))
    }
  }

  import Images._

  val p = Point(1, 1)
  val i = (pt: Point) => 1

  def liftTests(): Seq[Test[_]] = {
    Seq(Test("lift0", () => lift0(1)(p), 1),
        Test("lift1", () => lift1((i: Int) => i.toString)(pt => 1)(p), "1"),
        Test("lift2", () => lift2((x: Int, y: Int) => (x + y).toString)(i)(i)(p), "2"),
        Test("lift3", () => lift3((x: Int, y: Int, z: Int) => (x + y + z).toString)(i)(i)(i)(p), "3"))
  }

  def otherTests(): Seq[Test[_]] = {
    import ExampleClr._
    Seq(Test("lerpClr", () => lerpClr(0.5, black, red), Colour(0.5, 0.0, 0.0, 1.0)),
        Test("overlayClr", () => overlayClr(green, blue), Colour(0.0, 1.0, 0.0, 1.0)),
        Test("selectImgClr", () => selectImgClr(ExampleMask.swirled)(_ => yellow)(_ => red)(p), Colour(1.0,1.0,0.0,1.0)),
        Test("gray2clr", () => gray2clr(_ => 0.5)(p), Colour(0.5, 0.5, 0.5, 1)))
  }

  def spatialTransformTests(): Seq[Test[_]] = {
    import SpatialTransforms._
    Seq(Test("translatePt", () => translatePt(Vector(3, 3))(Point(2, 2)), Point(5.0, 5.0)),
        Test("scalePt", () => scalePt(Vector(2, 3))(Point(1, 2)), Point(2.0, 6.0)),
        Test("rotatePt", () => rotatePt(85)(Point(2, 2)), Point(-1.6166020468909095,-2.3209045266852577)),
        Test("swirlPt", () => swirlPt(65)(Point(3, 4)), Point(0.7974753887845558,4.9359936187441456)),
        Test("polarXf", () => polarXf(p => p)(Point(3, 4)), Point(3.0000000000000004,3.9999999999999996)),
        Test("translate", () => translate(Vector(5, 3))(s => s)(Point(2, 3)), Point(-3.0, 0.0)),
        Test("scale", () => scale(Vector(2, 3))(s => s)(Point(100, 40)), Point(50.0,13.333333333333332)),
        Test("rotate", () => rotate(250)(s => s)(Point(4, 3)), Point(-1.9476308374843816,4.605076994022998)),
        Test("swirl", () => swirl(190)(s => s)(Point(5, 10)), Point(8.275739775544535,7.517455099132291)))
  }
  
  def maskingTests(): Seq[Test[_]] = {
    import Masking._

    def ins(i1: ImgMask, i2: ImgMask, p: Point): Boolean =
      intersectM(i1)(i2)(p)

    def un(i1: ImgMask, i2: ImgMask, p: Point): Boolean =
      unionM(i1)(i2)(p)

    def x(i1: ImgMask, i2: ImgMask, p: Point): Boolean =
      xorM(i1)(i2)(p)

    val i = Point(4, 5)
    val u = Point(1, 1)

    Seq(Test("fullM", () => fullM(p), true),
        Test("emptyM", () => emptyM(p), false),
        Test("intersectM_full_full", () => ins(fullM, fullM, i), true),
        Test("intersectM_empty_full", () => ins(emptyM, fullM, i), false),
        Test("intersectM_full_empty", () => ins(fullM, emptyM, i), false),
        Test("intersectM_empty_empty", () => ins(emptyM, emptyM, i), false),
        Test("unionM_full_full", () => un(fullM, fullM, u), true),
        Test("unionM_empty_full", () => un(emptyM, fullM, u), true),
        Test("unionM_full_empty", () => un(fullM, emptyM, u), true),
        Test("unionM_empty_empty", () => un(emptyM, emptyM, u), false),
        Test("xorM_full_full", () => x(fullM, fullM, i), false),
        Test("xorM_empty_full", () => x(fullM, emptyM, i), true),
        Test("xorM_full_empty", () => x(emptyM, fullM, i), true),
        Test("xorM_empty_empty", () => x(emptyM, emptyM, i), false),
        Test("diffM", () => diffM(p => p.x > 10)(p => p.x < 10)(Point(10, 100)), false))
  }

  def allTests(): Seq[Test[_]] =
    liftTests ++ otherTests ++ spatialTransformTests ++ maskingTests

  def main(args: Array[String]) {
    TestSuite(allTests).runSuite()
  }
}

        
