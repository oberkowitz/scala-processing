package scalaprocessing.centroidfield

import gab.opencv.Contour
import processing.core.{PApplet, PVector}

import scala.jdk.CollectionConverters._

class ProjectionContour(val contour: Contour, width: Int, height: Int) {

  val center: PVector = {
    val points = contour.getPoints.asScala.toSeq
    val sums = points.foldLeft((0f, 0f)) { (a, b) =>
      (a._1 + b.x, a._2 + b.y)
    }
    val xAverage = sums._1 / contour.numPoints()
    val yAverage = sums._2 / contour.numPoints()
    val x = PApplet.constrain(xAverage, 0, width)
    val y = PApplet.constrain(yAverage, 0, height)
    new PVector(x, y)
  }



}
