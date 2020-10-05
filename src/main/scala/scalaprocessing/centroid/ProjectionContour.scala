package scalaprocessing.centroid

import gab.opencv.Contour
import processing.core.PVector

import scala.jdk.CollectionConverters._

class ProjectionContour(contour: Contour) {

  val center: PVector = {
    val points = contour.getPoints.asScala.toSeq
    val sums = points.foldLeft((0f, 0f)) { (a, b) =>
      (a._1 + b.x, a._2 + b.y)
    }
    new PVector(sums._1 / contour.numPoints(), sums._2 / contour.numPoints())
  }



}
