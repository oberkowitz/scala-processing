package scalaprocessing.clouds

import processing.core.{PApplet, PConstants, PVector}

object Clouds {

  def main(args: Array[String]): Unit = {
    PApplet.main(Array("scalaprocessing.clouds.Clouds"))
  }
}

class Clouds extends PApplet {

  override def settings(): Unit = {
    size(300, 200)
  }
  val noiseScale = 0.02
  var scope = false

  override def setup(): Unit = {
    // Connect to the local instance of fcserver. You can change this line to connect to another computer's fcserver
    colorMode(PConstants.HSB, 100)
  }

  def fractalNoise(x: Double, y: Double, z: Double) = {

    var xx = x
    var yy = y
    var zz = z
    var r = 0.0
    var amp = 1.0
    (0 to 4).foreach { _ =>
      r = r + noise(x.toFloat, y.toFloat, z.toFloat) * amp
      amp /= 2
      xx *= 2
      yy *= 2
      zz *= 2
    }
    r
  }

  override def mouseClicked(): Unit = {
    scope = !scope
  }

  override def draw(): Unit = {
    val distance = 100
    var dx = 0.0
    var dy = 0.0
    val now = millis
    val speed = 0.008
    val angle = PApplet.sin(now * 0.001f)
    val z = now * 0.00008
    val hue = now * 0.01
    val scale = 0.002f
    dx = dx + PApplet.cos(angle) * speed
    dy = dx + PApplet.sin(angle) * speed
    loadPixels()
    var x = 0
    while ( {
      x < width
    }) {
      var y = 0
      while ( {
        y < height
      }) {
        val mouseVector = new PVector(mouseX, mouseY)
        val currPixel = new PVector(x, y)
        val mag = PVector.sub(mouseVector, currPixel).mag()
        val alpha = PApplet.map(mag, distance, 0, 0, 1)
        val c = if (!scope || (Math.abs(mouseX - x) < distance && Math.abs(mouseY - y) < distance)) {

          val ddx = dx + x * scale
          val ddy = dy + y * scale
          val n = fractalNoise(ddx, ddy, z) - 0.75
          val m = fractalNoise(ddx, ddy, z + 10.0f) - 0.75
          val dd: Float = ((hue + 80.0 * m) % 100.0).toFloat
          val fl: Float = 100 - 100 * PApplet.constrain(PApplet.pow((3.0f * n).toFloat, 3.5f), 0, 0.9f)
          val asdf: Float = 100 * PApplet.constrain(PApplet.pow((3.0 * n).toFloat, 1.5f), 0, 0.9f)
          color(dd, asdf, asdf * (if (scope) alpha else 1.0f) )
        } else {
          color(0)
        }
        pixels(x + width * y) = c

        {
          y += 1; y - 1
        }
      }

      {
        x += 1; x - 1
      }
    }
    updatePixels()
  }

}
