package scalaprocessing.centroidfield

import gab.opencv._
import org.openkinect.processing.Kinect2
import processing.core._
import scalaprocessing.flowfield.FlowFollowingVehicle
import scalaprocessing.util.util._

import scala.jdk.CollectionConverters._

class CentroidField extends PApplet {

  lazy val sWidth = 512
  lazy val sHeight = 424
  def pg(): PGraphics = getGraphics
  val kinect2 = new Kinect2(this)
  val opencv = new OpenCV(this, sWidth, sHeight)
  var frontWall = 100
  var backWall = 2000

  val flowField = new FlowField(16, sWidth, sHeight, this)
  var cachedField = new FlowField(16, sWidth, sHeight, this)
  var drawField = false
  var vehicles: Seq[FlowFollowingVehicle] = (1 to 100).map(
    _ =>
      new FlowFollowingVehicle(new PVector(random(sWidth), random(sHeight)),
        new PVector(random(width), random(height))))

  override def settings(): Unit = {

    fullScreen()
//    size(sWidth, sHeight)
//    pg = createGraphics(sWidth, sHeight)

  }

  override def setup(): Unit = {
    kinect2.initDepth()
    kinect2.initDevice()
    opencv.gray()
    opencv.threshold(70)
    val blank = createImage(sWidth, sHeight, PConstants.RGB)
    image(blank, 0, 0)

    flowField.init()

//    frameRate(40)
  }
  override def mouseClicked(): Unit = {
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
    if (key == ' ') {
      drawField = !drawField
    }
    println("FrontWall: " + frontWall)
    println("BackWall: " + backWall)
  }

  override def draw(): Unit = {
    if (frameCount % 1000 == 999) {
      cachedField.init()
      flowField.init()
    }
//    pg.scale(2)
    pg.background(0)
    val depthData = kinect2.getRawDepth()
    val processedDepthData = depthData.map {
      case i if i <= backWall && i >= frontWall => color(random(255), random(255), random(255))
      case _ => 0
    }
    val depthImg = new PImage(kinect2.depthWidth, kinect2.depthHeight)

    depthImg.loadPixels()
    depthImg.pixels = processedDepthData
    depthImg.updatePixels()

    opencv.loadImage(depthImg)
//    val displayOut = opencv.getOutput()

    val contours = opencv.findContours().asScala.toSeq.filter(_.numPoints() > 200).map(new ProjectionContour(_, sWidth, sHeight))
    pg.image(depthImg, 0, 0)
//    image(displayOut, depthImg.width, 0)

    pg.noFill()
    pg.strokeWeight(3)

//    contours.foreach(drawContour)

    // Flow field drawing
    pg.pushMatrix()
//    translate(sDepth * 2, 0)
    pg.strokeWeight(1)
    val modifiedField = new FlowField(16, sWidth, sHeight, this)
    if (frameCount % 5 == 1) {
      flowField.mutate(.01f)
      modifiedField.copyField(flowField.field)
      modify2(modifiedField, contours, processedDepthData, sWidth)
      cachedField = modifiedField
    } else {
      cachedField.mutate(.008f)
      modifiedField.copyField(cachedField.field)
    }
    if (drawField) modifiedField.draw()
    val vs = vehicles.map { v =>
      val vehicle = FlowFollowingVehicle.nextStep(v, modifiedField, sWidth, sHeight)
      v.render(pg)
      vehicle
    }
    vehicles = vs
    pg.popMatrix()

  }

  private def drawContour(pc: ProjectionContour): Unit = {
    stroke(0, 255, 0)
    pc.contour.draw()
    stroke(255, 0, 0)
    beginShape()
    for (point <- pc.contour.getPolygonApproximation.getPoints.asScala) {
      vertex(point.x, point.y)
    }
    endShape()
    beginShape()
    stroke(0, 0, 255)
    fill(color(0, 0, 255))
    val projectionContour = new ProjectionContour(pc.contour, sWidth, sHeight)
    ellipse(projectionContour.center.x, projectionContour.center.y, 5, 5)
    noFill()
    endShape()
  }


  def modify2(flowField: FlowField, projectionContours: Seq[ProjectionContour], processedDepthData: Array[Int], width: Int): Unit = {
    flowField.field.zipWithIndex.foreach {
      case (column, i) =>
        column.zipWithIndex.foreach {
          case (_, j) =>
            // find vector directly away from the centroid
            val cellLocation = new PVector(i * flowField.resolution, j * flowField.resolution)
            val fl = cellLocation.x.toInt + width * cellLocation.y.toInt
            if (processedDepthData(fl) !=  0) {
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
