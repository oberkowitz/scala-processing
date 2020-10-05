package scalaprocessing.kinectcloud

import org.openkinect.processing.Kinect2
import processing.core.PApplet.{constrain, map}
import processing.core.{PApplet, PConstants, PVector}

object KinectCloud {

  def main(args: Array[String]): Unit = {
    PApplet.main(Array("scalaprocessing.kinectcloud.KinectCloud"))
  }
}

class KinectCloud extends PApplet {

  val kinect2 = new Kinect2(this)

  var frontWall = 100
  var backWall = 3000

  override def settings(): Unit = {
    size(kinect2.depthWidth, kinect2.depthHeight)
  }

  override def keyPressed(): Unit = {
    keyCode match {
      case PConstants.UP => frontWall = Math.min(frontWall + 5, 4500)
      case PConstants.DOWN => frontWall = Math.max(frontWall - 5, 0)
      case PConstants.LEFT => backWall = Math.max(backWall - 5, 0)
      case PConstants.RIGHT => backWall = Math.min(backWall + 5, 4500)
      case _ => ()
    }
    println("FrontWall: " + frontWall)
    println("BackWall: " + backWall)
  }

  val noiseScale = 0.02
  var scope = false

  override def setup(): Unit = {
    kinect2.initDepth()
    kinect2.initDevice()
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
    val rawDepth = kinect2.getRawDepth
    val newDepth = rawDepth.map {
      case i if i <= backWall && i >= frontWall => constrain(map(i, 0, 4500, 0, 1.0f), 0, 1.0f)
      case _ => 0
    }

    var dx = 0.0
    var dy = 0.0
    val now = millis
    val speed = 0.008
    val angle = PApplet.sin(now * 0.001f)
    val z = now * 0.00008
    val hue = now * 0.01
    val scale = 0.001f
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
        val alpha = if (scope) newDepth(x + width * y) else 1.0f
        val c = if (alpha != 0) {

          val ddx = dx + x * scale
          val ddy = dy + y * scale
          val n = fractalNoise(ddx, ddy, z) - 0.75
          val m = fractalNoise(ddx, ddy, z + 10.0f) - 0.75
          val dd: Float = ((hue + 80.0 * m) % 100.0).toFloat
          val fl: Float = 100 - 100 * constrain(PApplet.pow((3.0f * n).toFloat, 3.5f), 0, 0.9f)
          val asdf: Float = 100 * constrain(PApplet.pow((3.0 * n).toFloat, 1.5f), 0, 0.9f)
          color(dd, asdf, asdf * alpha )
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
