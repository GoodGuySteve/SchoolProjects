// NOTE FOR STUDENTS: search for the string "FILL ME IN" to find the
// places where you need to insert your solutions.

import Math._
import scala.swing._
import scala.swing.event._
import java.awt.Color
import java.awt.geom._
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

// defined in the code below
import Utility._
import Images._
import SpatialTransforms._
import Masking._
import ExampleMask._
import ExampleGray._
import ExampleClr._

//——————————————————————————————————————————————————————————————————————————————
// main entry point

object Main {
  // this program takes command-line options to determine what image
  // to display on the screen out of a predefined list of example
  // images. the command-line usage for this program is explained in
  // the comments below. see the comments in the GUI code at the
  // bottom of the file for explanations of Coord, ImgMaskGUI,
  // ImgGrayGUI, and ImgClrGUI.
  def main( args:Array[String] ) {
    // example coordinates to use for displaying example images
    val coord = Coord(500, 500, 250, 250, 5, 5)

    // the mask example images, from ExampleMask
    val masks = List(
      ImgMaskGUI(coord, vstrip),
      ImgMaskGUI(coord, hstrip),
      ImgMaskGUI(coord, checker),
      ImgMaskGUI(coord, altRings),
      ImgMaskGUI(coord, polarChecker),
      ImgMaskGUI(coord, udisk),
      ImgMaskGUI(coord, scaledisk),
      ImgMaskGUI(coord, transdisk),
      ImgMaskGUI(coord, filtscaledisk),
      ImgMaskGUI(coord, filttransdisk),
      ImgMaskGUI(coord, swirled),
      ImgMaskGUI(coord, annulus(0.25)),
      ImgMaskGUI(coord, radReg(7)),
      ImgMaskGUI(coord, wedgeAnnulus),
      ImgMaskGUI(coord, shifted),
      ImgMaskGUI(coord, rotatedLine(0.33)),
      ImgMaskGUI(coord, bigX),
      ImgMaskGUI(coord, asterisk),
      ImgMaskGUI(coord, inverseAsterisk),
      ImgMaskGUI(coord, recordShape),
      ImgMaskGUI(coord, asteriskMinusX)
    )

    // the grayscale example images, from ExampleGray
    val grays = List(
      ImgGrayGUI(coord, wavDist),
      ImgGrayGUI(coord, grayGradient(coord.imgWidth))
    )

    // the color example images, from ExampleClr
    val clrs = List(
      ImgClrGUI(coord, ybRings),
      ImgClrGUI(coord, colorChecker),
      ImgClrGUI(coord, lerped),
      ImgClrGUI(coord, colorVStrip),
      ImgClrGUI(coord, colorCross),
      ImgClrGUI(coord, checkerRingStrip),
      ImgClrGUI(coord, inverseCheckerRingStrip),
      ImgClrGUI(coord, grayColorGradient(coord.imgWidth)),
      ImgClrGUI(coord, bigXChecker),
      ImgClrGUI(coord, ringsAsteriskOnChecker)
    )

    // parse the command line and display the appropriate image

    // the first command-line argument <mask|gray|clr> says whether
    // we're displaying an image from masks, grays, or clrs. the
    // second argument <index> specifies an index into the appropriate
    // list for the image to display. the optional third argument
    // [save], if present, says to save the resulting image into a PNG
    // file named <mask|gray|clr><index>.png.
    val usage = "usage: scala Main <mask|gray|clr> <index> [save]\nexample: scala Main mask 11 save"

    // check number of command-line arguments
    if ( args.length < 2 || args.length > 3 ) {
      println(usage)
      sys.exit(1)
    }

    // try to get the image index; it should be a number
    val idx =
      try { args(1).toInt }
      catch { case _:Throwable => println(usage); sys.exit(1) }

    // should the image be saved to a file?
    val save =
      if ( args.length == 3 && args(2) == "save" ) true
      else false

    // determine whether we're displaying one of the mask, grayscale,
    // or color images and display the appropriate one based on the
    // given index
    args(0) match {
      case "mask" =>
        if ( idx >= masks.length ) {
          println("there are only " + masks.length + " mask images")
          sys.exit(1)
        }
        masks(idx).show()
        if (save) saveMaskImage(masks(idx), idx)

      case "gray" =>
        if ( idx >= grays.length ) {
          println("there are only " + grays.length + " grayscale images")
          sys.exit(1)
        }
        grays(idx).show()
        if (save) saveGrayImage(grays(idx), idx)

      case "clr" =>
        if ( idx >= clrs.length ) {
          println("there are only " + clrs.length + " color images")
          sys.exit(1)
        }
        clrs(idx).show()
        if (save) saveClrImage(clrs(idx), idx)

      case _ =>
        println(usage)
        sys.exit(1)
    }
  }

  // save a mask image to a file as a PNG
  def saveMaskImage( img:ImgMaskGUI, idx:Int ) {
    val image = new BufferedImage(img.coord.canvasWidth,
      img.coord.canvasHeight, BufferedImage.TYPE_INT_ARGB)
    img.canvas.paint(image.getGraphics().asInstanceOf[Graphics2D])
    ImageIO.write(image, "png", new java.io.File("mask" + idx + ".png"))
  }

  // save a grayscale image to a file as a PNG
  def saveGrayImage( img:ImgGrayGUI, idx:Int ) {
    val image = new BufferedImage(img.coord.canvasWidth,
      img.coord.canvasHeight, BufferedImage.TYPE_INT_ARGB)
    img.canvas.paint(image.getGraphics().asInstanceOf[Graphics2D])
    ImageIO.write(image, "png", new java.io.File("gray" + idx + ".png"))
  }

  // save a color image to a file as a PNG
  def saveClrImage( img:ImgClrGUI, idx:Int ) {
    val image = new BufferedImage(img.coord.canvasWidth,
      img.coord.canvasHeight, BufferedImage.TYPE_INT_ARGB)
    img.canvas.paint(image.getGraphics().asInstanceOf[Graphics2D])
    ImageIO.write(image, "png", new java.io.File("clr" + idx + ".png"))
  }
}


//——————————————————————————————————————————————————————————————————————————————
// Example images that exercise the image DSL. see the 'Functional
// Images' section of the code below to see the definitions of
// the types used here.

// example mask image definitions
object ExampleMask {
  val vstrip: ImgMask =
    pt => abs(pt.x) <= 0.5

  val hstrip: ImgMask =
    pt => abs(pt.y) <= 0.5

  val checker: ImgMask =
    pt => (floor(pt.x) + floor(pt.y)).toInt % 2 == 0

  val altRings: ImgMask =
    pt => floor(dist(pt)).toInt % 2 == 0

  val polarChecker: ImgMask = {
    val sc = (plr:Polar) => Point(plr.rho, plr.theta * (10/PI))
    checker compose sc compose pt2plr
  }

  val udisk: ImgMask =
    pt => dist(pt) < 1

  val scaledisk: ImgMask =
    udisk compose scalePt(Vector(2,2))

  val transdisk: ImgMask =
    udisk compose translatePt(Vector(1,0))

  val filtscaledisk: ImgMask =
    scale(Vector(2,2))(udisk)

  val filttransdisk: ImgMask =
    translate(Vector(1,0))(udisk)

  val swirled: ImgMask =
    swirl(1)(vstrip)

  val annulus: Value => ImgMask =
    sc => diffM(udisk)(scale(Vector(sc,sc))(udisk))

  val radReg: Int => ImgMask =
    n => {
      val test = (plr:Polar) => floor(plr.theta * (n/PI)).toInt % 2 == 0
      test compose pt2plr
    }

  val wedgeAnnulus: ImgMask =
    intersectM(annulus(0.25))(radReg(7))

  val shiftXor: Value => FiltMask =
    dx => img => xorM(
      translate(Vector(dx,0))(img))(
      translate(Vector(-dx,0))(img)
    )

  val shifted: ImgMask =
    shiftXor(2.6)(altRings)

  val rotatedLine: Double => ImgMask =
    amount => rotate(amount)(vstrip)

  val bigX: ImgMask =
    unionM(rotatedLine(0.45))(rotatedLine(-0.45))

  val asterisk: ImgMask =
    unionM(unionM(vstrip)(rotatedLine(-0.90)))(rotatedLine(0.90))

  val inverseAsterisk: ImgMask =
    xorM(asterisk)(fullM)

  val recordShape: ImgMask = {
    val fun = (plr: Polar) => Polar(math.abs(plr.rho - 1.5), plr.theta)
    udisk compose polarXf(fun)
  }

  val asteriskMinusX: ImgMask =
    diffM(diffM(asterisk)(bigX))(emptyM)
}

// example grayscale image definitions
object ExampleGray {
  val wavDist: ImgGray =
    pt => (1 + cos(PI * dist(pt))) / 2

  val grayGradient: Double => ImgGray =
    width => pt => math.abs(pt.x) / width
}

// example color image definitions
object ExampleClr {
  // various colors
  val invisible = Colour(0, 0, 0, 0)
  val black = Colour(0, 0, 0, 1)
  val white = Colour(1, 1, 1, 1)
  val red = Colour(1, 0, 0, 1)
  val green = Colour(0, 1, 0, 1)
  val blue = Colour(0, 0, 1, 1)
  val yellow = Colour(1, 1, 0, 1)

  // translate a mask into a colour image, translating true and false
  // values in the mask into the given respective colors
  val maskAsColors: ImgMask => Colour => Colour => ImgClr =
    mask => ifTrue => ifFalse => selectImgClr(mask)(lift0(ifTrue))(lift0(ifFalse))

  val ybRings: ImgClr =
    lerpImgClr(wavDist)(lift0(blue))(lift0(yellow))

  val colorChecker: ImgClr =
    maskAsColors(checker)(black)(white)

  val lerped: ImgClr =
    lerpImgClr(wavDist)(
      maskAsColors(checker)(black)(white))(
      maskAsColors(polarChecker)(blue)(yellow))

  val colorVStrip: ImgClr =
    msk2clr(vstrip)

  val colorCross: ImgClr =
    overlayImgClr(
      maskAsColors(vstrip)(black)(invisible))(
      maskAsColors(hstrip)(red)(invisible))

  val checkerRingStrip: ImgClr =
    selectImgClr(vstrip)(ybRings)(colorChecker)

  val inverseCheckerRingStrip: ImgClr =
    selectImgClr(notM(vstrip))(ybRings)(colorChecker)

  val grayColorGradient: Double => ImgClr =
    width => gray2clr(grayGradient(width))

  val bigXChecker: ImgClr =
    selectImgClr(bigX)(colorChecker)(lift0(green))

  val ringsAsteriskOnChecker: ImgClr =
    selectImgClr(asterisk)(ybRings)(colorChecker)
}


//——————————————————————————————————————————————————————————————————————————————
// Functional Images
//
// An image is defined as a function from a cartesian coordinate Point
// (defined below) to some value; we specifically use three kinds of
// values: Booleans (yielding a black-and-white image), Doubles
// (yielding a grayscale image), and Colours (defined below, yielding
// a color image).
//
// NOTE 1: the comments specifying what to implement below use
// 'method' to mean defining a function using the 'def' keyword and
// 'function' to mean defining a function using the 'val'
// keyword. recall that, semantically, methods and functions are not
// quite the same thing.
//
// NOTE 2: often the Scala compiler will be able to figure out all of
// the necessary types automatically, but sometimes it will not and
// you will need to give them manually. we suggest that you first
// implement things without explicit type annotations, and then add
// them in later if the compiler complains. of course, this note
// doesn't apply to generic types using type variables which must
// always be explicitly declared.
//
// NOTE 3: in our solution, the bodies of all of the functions you are
// supposed to define are only a single line long. if your solutions
// are more than that, you are probably doing something wrong (or at
// least in a more complicated way than necessary).

// a point in the cartesian coordinate system
case class Point( x:Double, y:Double )

// a point in the polar coordinate system
case class Polar( rho:Double, theta:Double )

// Colours (using the British spelling to distinguish from the
// existing java.awt.Color) are defined as four values: Red, Green,
// Blue, and alpha (the color's transparency).
case class Colour( R:Value, G:Value, B:Value, alpha:Value )

// a vector
case class Vector( x:Double, y:Double ) {
  def neg = Vector(-x, -y)
  def inverse = Vector(1/x, 1/y)
}

// some useful utility functions
object Utility {
  // compute distance from origin
  def dist( pt:Point ): Double =
    sqrt( pow(pt.x, 2) + pow(pt.y, 2) )

  // transform cartesian to polar coordinates
  def pt2plr( pt:Point ): Polar =
    Polar( dist(pt), atan2(pt.y, pt.x) )

  // transform polar to cartesian coordinates
  def plr2pt( plr:Polar ): Point =
    Point( plr.rho * cos(plr.theta), plr.rho * sin(plr.theta) )
}

// the functions dealing with images. we provide a set of useful type
// aliases to make the types more meaningful.
object Images {
  type Value   = Double // intended invariant: 0 ≤ Value ≤ 1
  type Img[A]  = Point => A
  type ImgMask = Img[Boolean]
  type ImgGray = Img[Value]
  type ImgClr  = Img[Colour]

  // interpolate between two colours; 0 ≤ w ≤ 1 is the weight of the
  // first colour and (1-w) is the weight of the second colour
  def lerpClr( w:Value, c1:Colour, c2:Colour ): Colour = {
    def h( v1:Value, v2:Value ) = (w*v1) + ((1-w)*v2)
    Colour( h(c1.R,c2.R), h(c1.G,c2.G), h(c1.B,c2.B), h(c1.alpha,c2.alpha) )
  }

  // overlay one colour with another, blending them together
  def overlayClr( c1:Colour, c2:Colour ): Colour = {
    def h( v1:Value, v2:Value ) = v1 + ((1-c1.alpha)*v2)
    Colour( h(c1.R,c2.R), h(c1.G,c2.G), h(c1.B,c2.B), h(c1.alpha,c2.alpha) )
  }

  // we will define a set of "lifting" methods called lift<N> that
  // take functions on <N> values as arguments and return functions on
  // <N> images, where <N> is some number. these lifting methods will
  // enable us to easily create functions to transform and combine <N>
  // images based on functions to transform and combine <N>
  // values. these methods are generic, meaning that the types of the
  // values and images are not specified explicitly, instead they use
  // type variables.
  //
  // example application: given a function with signature (Boolean,
  // Value) => Colour, we could use lift2 to create a function taking
  // an ImgMask and an ImgGray as arguments and returning an ImgClr.

  // define a generic method named 'lift0' that takes a value of some
  // type and returns an image containing only that value.
  // !!FILL ME IN
	def lift0[A](a: A): Img[A] = {
		(p: Point) => a
	} 
  // define a generic, curried method named 'lift1' that takes (1) a
  // function from a value of type A to a value of type B, and (2) an
  // image of type A; and returns an image of type B.
  // !!FILL ME IN
	def lift1[A,B](f: A => B)(i: Img[A]): Img[B] = {
		(p: Point) => f(i(p))
	}
  // define a generic, curried method named 'lift2' that takes (1) a
  // function from 2 values of types A and B to a value of type C, (2)
  // an image of type A, (3) an image of type B; and returns an image
  // of type C.
  // !!FILL ME IN
	def lift2[A,B,C](f: (A, B) => C)(imgA: Img[A])(imgB: Img[B]): Img[C] = {
		(p: Point) => f(imgA(p), imgB(p))
	} 
  // define a generic, curried method named 'lift3' that takes (1) a
  // function from 3 values of types A, B, and C to a value of type D,
  // (2) an image of type A, (3) an image of type B, (4) an image of
  // type C; and returns an image of type D.
  // !!FILL ME IN
	def lift3[A,B,C,D](f: (A, B, C) => D)(imgA: Img[A])(imgB: Img[B])(imgC: Img[C]): Img[D] = {
		(p: Point) => f(imgA(p), imgB(p), imgC(p))
	} 
  // define a function 'lerpImgClr' that uses lerpClr (defined above)
  // and one of the above lifting methods to interpolate between two
  // colour images.
  // !!FILL ME IN
  val lerpImgClr = {
    (weight: Img[Value]) =>
      (i1: Img[Colour]) =>
        (i2: Img[Colour]) =>
          lift3(lerpClr)(weight)(i1)(i2)
  }
  // define a function overlayImgClr' that uses overlayClr (defined
  // above) and one of the above lifting methods to overlay one colour
  // image on top of another
  // !!FILL ME IN
  val overlayImgClr = {
    (i1: Img[Colour]) =>
      (i2: Img[Colour]) => 
        lift2(overlayClr)(i1)(i2) 
  }
  // define a function 'selectImgClr' that uses one of the above
  // lifting methods on an anonymous function that takes a boolean and
  // two colours as arguments and returns the first colour if the
  // boolean is true, otherwise the second colour; thus yielding a
  // function that takes an ImgMask and two ImgClrs and returns an
  // ImgClr whose values come from either the first or second ImgClr
  // argument based on the boolean value at that position given by the
  // ImgMask argument.
  // !!FILL ME IN
  val selectImgClr = {
    (mask: Img[Boolean]) =>
      (i1: Img[Colour]) =>
        (i2: Img[Colour]) => 
          lift3((q0: Boolean, a: Colour, b: Colour) => if(q0) a else b)(mask)(i1)(i2)
  }

  // define a function 'msk2clr' that uses one of the lifting methods
  // on an anonymous function; thus yielding a function that takes a
  // ImgMask as argument and returns an ImgClr where a true value in
  // the ImgMask is black in the ImgClr and a false value is white.
  // !!FILL ME IN
  val msk2clr = {
    (i: Img[Boolean]) =>
      lift1((b: Boolean) => if(b) black else white)(i)
  }
  // define a function 'gray2clr' that uses one of the lifting methods
  // on an anonymous function; thus yielding a function that takes a
  // ImgGray as argument and returns an ImgClr where a Value in the
  // ImgGray is turned into a corresponding Colour in the ImgClr.
  // !!FILL ME IN
  val gray2clr = {
    (i: Img[Value]) =>
      lift1((d: Value) => Colour(d, d, d, 1))(i)
  }
}

// the functions dealing with spatial transforms on images. we provide
// a set of useful type aliases to make the types more meaningful.
object SpatialTransforms {
  type TransformPt  = Point => Point
  type TransformPlr = Polar => Polar
  type Filter[A]    = Img[A] => Img[A]
  type FiltMask     = Filter[Boolean]
  type FiltGray     = Filter[Value]
  type FiltColour   = Filter[Colour]

  // define a method named 'translatePt' that takes a Vector as
  // argument and returns a TransformPt that translates a point
  // according to the given Vector. recall that to translate a point
  // by a vector, you simply add the vector's x component to the
  // point's x coordinate and the vector's y component to the point's
  // y coordinate.
  // !!FILL ME IN
  def translatePt(v: Vector): TransformPt = {
    (p: Point) => Point(p.x + v.x, p.y + v.y)
  }
  // define a method named 'scalePt' that takes a Vector as argument
  // and returns a TransformPt that scales a point according to the
  // given Vector. recall that to scale a point by a vector, you
  // simply multiply the vector's x component by the point's x
  // coordinate and the vector's y component by the point's y
  // coordinate.
  // !!FILL ME IN
  def scalePt(v: Vector): TransformPt = {
    (p: Point) => Point(p.x * v.x, p.y * v.y)
  }
  // define a method named 'rotatePt' that takes a Double as argument
  // specifying an angle and returns a TransformPt that rotates a
  // point by the given angle. recall that to rotate a point (x, y) by
  // an angle theta, the new point is (x * cos theta − y * sin theta,
  // y * cos theta + x * sin theta).
  // !!FILL ME IN
  def rotatePt(theta: Double): TransformPt = {
    (p: Point) => Point(p.x * cos(theta) - p.y * sin(theta), p.y * cos(theta)+ p.x * sin(theta))
  }
  // define a method named 'swirlPt' that takes a Double as argument
  // specifying an angle and returns a TransformPt that swirls a point
  // by the given angle. recall that to swirl a point pt by an angle
  // theta, the point is rotated by the angle (|pt| * (2*pi/theta))
  // where |pt| is the distance from pt to the origin.
  // !!FILL ME IN
  def swirlPt(theta: Double): TransformPt = {
    (p: Point) => rotatePt(dist(p) * (2 * PI / theta))(p)
  }
  // define a method named 'polarXf' that takes a TransformPlr as
  // argument and returns a TranformPt.
  // !!FILL ME IN
  def polarXf(transform: TransformPlr): TransformPt = {
    (p: Point) => plr2pt(transform(pt2plr(p)))
  }
  // NOTE: the above transformations work somewhat
  // counter-intuitively, e.g., scalePt(Vector(2,2)) actually scales
  // points so an image is half the size instead of twice the size,
  // and translatePt(Vector(1,0)) actually translates an image to the
  // left instead of to the right. the reason is because of the way we
  // are defining images as functions and transformations as functions
  // on functions; in general, to get the transformation we
  // intuitively expect we must use the inverse transformation on the
  // image. to make this easier, we'll define below versions of the
  // above transformations that automatically use the inverse argument
  // so that we get the results we expect.

  // define a generic method named 'translate' that takes a vector as
  // argument and returns a Filter that translates an image with
  // translatePt using the negation of that vector.
  // !!FILL ME IN
  def translate[A](v: Vector): Filter[A] = {
    (i: Img[A]) => ((p: Point) => i(translatePt(v.neg)(p)))
  }
  // define a generic method named 'scale' that takes a vector as
  // argument and returns a Filter that scales an image with scalePt
  // using the inverse of that vector.
  // !!FILL ME IN
  def scale[A](v: Vector): Filter[A] = {
    (i: Img[A]) => ((p: Point) => i(scalePt(v.inverse)(p)))
  }
  // define a generic method named 'rotate' that takes an angle as
  // argument and returns a Filter that rotates an image with
  // rotatePt using the negation of that angle.
  // !!FILL ME IN
  def rotate[A](theta: Double): Filter[A] = {
    (i: Img[A]) => ((p: Point) => i(rotatePt(-theta)(p)))
  }
  // define a generic method named 'swirl' that takes an angle as
  // argument and returns a Filter that swirls an image with swirlPt
  // using the negation of that angle.
  // !!FILL ME IN
  def swirl[A](theta: Double): Filter[A] = {
    (i: Img[A]) => ((p: Point) => i(swirlPt(-theta)(p)))
  }
}

// the functions dealing with mask algebra, i.e., combining boolean
// images in various ways to create masks to use for transforming
// other images.
object Masking {
  // define a function named 'fullM' using one of the lift methods
  // from Images; thus yielding an ImgMask that is always true at
  // every point.
  // !!FILL ME IN
  val fullM = {
      lift0(true)
  }
  // define a function named 'emptyM' using one of the lift methods
  // from Images; thus yielding an ImgMask that is always false at
  // every point.
  // !!FILL ME IN
  val emptyM = {
      lift0(false)
  }
  // define a function named 'notM' using one of the lift methods from
  // Images; thus yielding a function that returns the negation of its
  // input: taking an ImgMask as argument and returning an ImgMask
  // whose value at a position is true iff the argument's value at
  // that position was false.
  // !!FILL ME IN
  val notM = {
    (i9: Img[Boolean]) =>
      lift1((q: Boolean) => if(q) false else true)(i9)
  }
  // define a function named 'intersectM' using one of the lift
  // methods from Images; thus yielding a function that returns the
  // intersection of its two inputs: taking two ImgMasks as arguments
  // and returning an ImgMask whose value at a position is true iff
  // both argument's values at that position were true.
  // !!FILL ME IN
  val intersectM = {
    (i1: Img[Boolean]) => 
      (i2: Img[Boolean]) =>
        lift2((q1: Boolean, q2: Boolean) => if(q1 && q2) true else false)(i1)(i2)
  }
  // define a function named 'unionM' using one of the lift methods
  // from Images; thus yielding a function that returns the union of
  // its two inputs: taking two ImgMasks as arguments and returning an
  // ImgMask whose value at a position is true if either argument's
  // value at that position was true.
  // !!FILL ME IN
  val unionM = {
    (i1: Img[Boolean]) =>
      (i2: Img[Boolean]) =>
        lift2((q1: Boolean, q2: Boolean) => if(q1 || q2) true else false)(i1)(i2)
  }
  // define a function named 'xorM' using one of the lift methods from
  // Images; thus yielding a function that returns the XOR of its two
  // inputs: taking two ImgMasks as arguments and returning an ImgMask
  // whose value at a position is true if exactly one argument's value
  // at that position was true.
  // !!FILL ME IN
  val xorM = {
    (i1: Img[Boolean]) =>
      (i2: Img[Boolean]) =>
        lift2((q1: Boolean, q2: Boolean) => if(q1 ^ q2) true else false)(i1)(i2)
  }
  // define a curried function named 'diffM' in terms of a subset of
  // the above defined functions (i.e., diffM is defined as a
  // combination of other masking operations) that returns the set
  // difference of its two inputs: taking two ImgMasks X and Y as
  // arguments and returning an ImgMask whose value at a position is
  // true iff that position was true in X but not in Y.
  // !!FILL ME IN
  val diffM = {
    (i1: Img[Boolean]) =>
      (i2: Img[Boolean]) =>
        intersectM(i1)(notM(i2))
  }
}


//——————————————————————————————————————————————————————————————————————————————
// GUI implementation

// relation between the GUI canvas coordinate system and the image
// coordinate system. in the canvas coordinate system, (0, 0) is the
// top-left and (canvasWidth-1, canvasHeight-1) is the bottom-right of
// the GUI window. note that the image origin coordinates can be
// negative and/or greater than the canvas width/height.
case class Coord(
  canvasWidth:Int,  // width of the GUI canvas
  canvasHeight:Int, // height of the GUI canvas
  originX:Int,      // canvas X coordinate of the image's origin point
  originY:Int,      // canvas Y coordinate of the image's origin point
  imgWidth:Double,  // the width of the image to fit inside the canvas
  imgHeight:Double  // the height of the image to fit inside the canvas
) {
  // sanity check
  assert( canvasWidth > 0 && canvasHeight > 0 )
  assert( imgWidth > 0 && imgHeight > 0 )

  // scaling factor from canvas to image coordinates
  val scaleX = canvasWidth.toDouble / imgWidth.toDouble
  val scaleY = canvasHeight.toDouble / imgHeight.toDouble

  // given coordinates (x, y) in the canvas coordinate system, return
  // the corresponding point in the image coordinate system
  def c2img( x:Int, y:Int ): Point =
    Point( (x - originX)/scaleX, (y - originY)/scaleY )
}

// a GUI window to display mask images
case class ImgMaskGUI( coord:Coord, img:ImgMask ) {
  val canvas = new Panel {
    override def paint( g:Graphics2D ) {
      g.setPaint(Color.black)
      for ( x <- 0 until coord.canvasWidth; y <- 0 until coord.canvasHeight ) {
        if ( img(coord.c2img(x,y)) ) g.drawLine(x, y, x, y)
      }
    }

    preferredSize = new Dimension(coord.canvasWidth, coord.canvasHeight)
  }

  val frame = new MainFrame {
    title = "Mask Image"
    contents = canvas
  }

  // call this to actually display the image on the screen
  def show() { frame.open }
}

// a GUI window to display grayscale images
case class ImgGrayGUI( coord:Coord, img:ImgGray ) {
  val canvas = new Panel {
    override def paint( g:Graphics2D ) {
      for ( x <- 0 until coord.canvasWidth; y <- 0 until coord.canvasHeight ) {
        val v = img( coord.c2img(x,y) ).toFloat
        g.setPaint( new Color(v, v, v, 1) )
        g.drawLine(x, y, x, y)
      }
    }

    preferredSize = new Dimension(coord.canvasWidth, coord.canvasHeight)
  }

  val frame = new MainFrame {
    title = "Grayscale Image"
    contents = canvas
  }

  // call this to actually display the image on the screen
  def show() { frame.open }
}

// a GUI window to display colour images
case class ImgClrGUI( coord:Coord, img:ImgClr ) {
  val canvas = new Panel {
    override def paint( g:Graphics2D ) {
      for ( x <- 0 until coord.canvasWidth; y <- 0 until coord.canvasHeight ) {
        val Colour(r, gg, b, alpha) = img( coord.c2img(x, y) )
        g.setPaint( new Color(r.toFloat, gg.toFloat, b.toFloat, alpha.toFloat) )
        g.drawLine(x, y, x, y)
      }
    }

    preferredSize = new Dimension(coord.canvasWidth, coord.canvasHeight)
  }

  val frame = new MainFrame {
    title = "Colour Image"
    contents = canvas
  }

  // call this to actually display the image on the screen
  def show() { frame.open }
}
