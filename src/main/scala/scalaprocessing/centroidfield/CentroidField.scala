package scalaprocessing.centroidfield

import gab.opencv._
import org.openkinect.processing.Kinect2
import processing.core.PApplet.{constrain, map}
import processing.core.{PApplet, PConstants, PImage, PVector}
import scalaprocessing.flowfield.FlowFollowingVehicle
import scalaprocessing.util.util._

import scala.jdk.CollectionConverters._

class CentroidField extends PApplet {
  val kinect2 = new Kinect2(this)
  val opencv = new OpenCV(this, 512, 424)
  var frontWall = 100
  var backWall = 3000

  val flowField = new FlowField(16, 512, 424, this)
  var drawField = true
  var vehicles: Seq[FlowFollowingVehicle] = (1 to 20).map(
    _ =>
      new FlowFollowingVehicle(new PVector(random(512), random(424)),
        new PVector(random(width), random(height))))

  override def settings(): Unit = {
    size(512 * 3, 424)
  }

  override def setup(): Unit = {
    kinect2.initDepth()
    kinect2.initDevice()
    opencv.gray()
    opencv.threshold(70)
    val blank = createImage(512 * 3, 424, PConstants.RGB)
    image(blank, 0, 0)

    flowField.init()
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
    background(0)
    val depthData = kinect2.getRawDepth()
    val processedDepthData = depthData.map {
      case i if i <= backWall && i >= frontWall => color(255)
      case _ => 0
    }
    val depthImg = new PImage(kinect2.depthWidth, kinect2.depthHeight)

    depthImg.loadPixels()
    depthImg.pixels = processedDepthData
    depthImg.updatePixels()

    opencv.loadImage(depthImg)
    val displayOut = opencv.getOutput()

    val contours: Seq[Contour] = opencv.findContours().asScala.toSeq.filter(_.numPoints() > 100)
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
      stroke(0, 0, 255)
      fill(color(0, 0, 255))
      val projectionContour = new ProjectionContour(contour, 512, 424)
      ellipse(projectionContour.center.x, projectionContour.center.y, 5, 5)
      noFill()
      endShape()
    }

    // Flow field drawing
    pushMatrix()
    translate(512 * 2, 0)
    strokeWeight(1)
    val modifiedField = new FlowField(16, 512, 424, this)
    modifiedField.copyField(flowField.field)

//    modify2(modifiedField, contours.map(new ProjectionContour(_, 512, 424)))
    for (contour <- contours) {
      val pc = new ProjectionContour(contour, 512, 424)
      modifyFlowField(pc, modifiedField)

    }
    if (drawField) modifiedField.draw()
    val vs = vehicles.map { v =>
      val vehicle = FlowFollowingVehicle.nextStep(v, modifiedField, 512, 424)
      v.render(this)
      vehicle
    }
    vehicles = vs
    popMatrix()

  }

  def modify2(flowField: FlowField, projectionContours: Seq[ProjectionContour]) = {
    flowField.field.zipWithIndex.foreach {
      case (column, i) =>
        column.zipWithIndex.foreach {
          case (_, j) =>
            // find vector directly away from the centroid
            val cellLocation = new PVector(i * flowField.resolution, j * flowField.resolution)
            val pcOpt = projectionContours.find(_.contour.containsPoint(cellLocation.x.toInt, cellLocation.y.toInt))
            pcOpt.foreach { pc =>
              val outwards = cellLocation - pc.center
              val heading = outwards.heading()
              val perf = new PVector(Math.cos(heading).toFloat, Math.sin(heading).toFloat)
              flowField.field(i)(j) = perf.normalize()
            }
        }
    }

  }

  def modifyFlowField(projectionContour: ProjectionContour, flowField: FlowField): Unit = {
    flowField.field.zipWithIndex.foreach {
      case (column, i) =>
        column.zipWithIndex.foreach {
          case (_, j) =>
            // find vector directly away from the centroid
            val cellLocation = new PVector(i * flowField.resolution, j * flowField.resolution)
            if (projectionContour.contour.containsPoint(cellLocation.x.toInt, cellLocation.y.toInt)) {
              val outwards = cellLocation - projectionContour.center
              val heading = outwards.heading()
              val perf = new PVector(Math.cos(heading).toFloat, Math.sin(heading).toFloat)
              flowField.field(i)(j) = perf.normalize()
            }
        }
    }
  }

}

object CentroidField {
  def main(args: Array[String]): Unit = {
    PApplet.main("scalaprocessing.centroidfield.CentroidField")
  }
}
