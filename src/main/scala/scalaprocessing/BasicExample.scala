package scalaprocessing

import processing.core.PApplet._
import processing.core.PConstants._
import processing.core._

class BasicExample extends PApplet {

  override def settings() {
    size(800, 800)
  }

  override def setup() {
    colorMode(HSB, 100)
    frameRate(999)
    background(0)
    noLoop()
  }

  override def draw() {
    def pix(f:(Int, Int) => Unit) {
      for (x <- 0 until width; y <- 0 until height) f(x, y)
    }
    pix { (x, y) =>
      stroke(map(x, 0, width, 0, 100), map(y, 0, height, 0, 100), 100)
      point(x, y)
    }
  }
}

object BasicExample {
  def main(args:Array[String]) {
    PApplet.main(Array("scalaprocessing.BasicExample"))
  }
}
