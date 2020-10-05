package scalaprocessing.contours

import gab.opencv._
import org.openkinect.processing.Kinect2
import processing.core.{PApplet, PConstants}

import scala.jdk.CollectionConverters._

class Contours extends PApplet {

  val kinect2 = new Kinect2(this)
  val opencv = new OpenCV(this, 512, 424)

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

  override def draw(): Unit = {

    val depthImg = kinect2.getDepthImage()
    opencv.loadImage(depthImg)
    val displayOut = opencv.getOutput()

    val contours: Seq[Contour] = opencv.findContours().asScala.toSeq
    println("found " + contours.size + " contours")

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
    }
  }

}

object Contours {
  def main(args: Array[String]): Unit = {
    PApplet.main("scalaprocessing.contours.Contours")
  }
}
