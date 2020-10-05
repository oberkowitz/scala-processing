package scalaprocessing.centroid

import gab.opencv._
import org.openkinect.processing.Kinect2
import processing.core.PApplet.{constrain, map}
import processing.core.{PApplet, PConstants, PImage, PVector}
import scalaprocessing.util.util._
import scala.jdk.CollectionConverters._

class Centroid extends PApplet {
  val kinect2 = new Kinect2(this)
  val opencv = new OpenCV(this, 512, 424)
  var frontWall = 100
  var backWall = 3000

  override def settings(): Unit = {
    size(1024, 424)
  }

  override def setup(): Unit = {
    kinect2.initDepth()
    kinect2.initDevice()
    opencv.gray()
    opencv.threshold(70)
    val blank = createImage(1024, 424, PConstants.RGB)
    image(blank, 0, 0)

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

  override def draw(): Unit = {

    val depthData = kinect2.getRawDepth()
    val thresholdDepth = depthData.map {
      case i if i <= backWall && i >= frontWall => color(255)
      case _ => 0
    }
    val depthImg = new PImage(kinect2.depthWidth, kinect2.depthHeight)

    depthImg.loadPixels()
    depthImg.pixels = thresholdDepth
    depthImg.updatePixels()

    opencv.loadImage(depthImg)
    val displayOut = opencv.getOutput()

    val contours: Seq[Contour] = opencv.findContours().asScala.toSeq.filter(_.numPoints() > 200)

    image(depthImg, 0, 0)
    image(displayOut, depthImg.width, 0)

    noFill()
    strokeWeight(3)

    for (contour <- contours) {

      stroke(0, 255, 0)
      contour.draw()
      stroke(255, 0, 0)
      beginShape()
      for (point <- contour.getPolygonApproximation.getPoints.asScala) {
        vertex(point.x, point.y)
      }
      endShape()
      beginShape()
      stroke( 0, 0, 255)
      fill(color(0, 0, 255))
      val projectionContour = new ProjectionContour(contour)
      ellipse(projectionContour.center.x, projectionContour.center.y, 5, 5)
      noFill()
      endShape()
    }
  }

}

object Centroid {
  def main(args: Array[String]): Unit = {
    PApplet.main("scalaprocessing.centroid.Centroid")
  }
}
