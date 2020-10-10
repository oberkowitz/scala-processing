package scalaprocessing.flowfield

import processing.core.{PApplet, PConstants, PVector}
import scalaprocessing.centroidfield.FlowField
import scalaprocessing.util.util._

object FlowFieldTest {
  def main(args: Array[String]): Unit = {
    PApplet.main(Array(classOf[FlowFieldTest].getName))
  }
}

class FlowFieldTest extends PApplet {
  val flowField = new FlowField(8, 512, 424, this)

  var drawField = true
  var vehicles: Seq[FlowFollowingVehicle] = (1 to 100).map(
    _ =>
      new FlowFollowingVehicle(new PVector(random(512), random(424)),
                               new PVector(random(width), random(height))))

  override def setup(): Unit = {
    flowField.init()
  }

  override def settings(): Unit = {
    size(512, 424)
  }

  override def keyPressed(): Unit = {
    if (key == ' ') {
      drawField = !drawField
    }
  }

  override def mouseClicked(): Unit = {
    flowField.init()
  }

  def createModifiedFlowField(epicenter: PVector, input: FlowField, output: FlowField): Unit = {
    input.field.zipWithIndex.foreach {
      case (column, x) =>
        column.zipWithIndex.foreach {
          case (cell, y) =>
            // find vector directly away from the mouse
            val maxDistance = new PVector(width, height).mag()
            val outwards = new PVector(x * input.resolution, y * input.resolution) - epicenter
            val distance = outwards.mag()
            val fl = 100
            if (distance < fl) {
              val heading = outwards.heading()
              val originalMag = cell.mag()
              val perf = new PVector(Math.cos(heading).toFloat, Math.sin(heading).toFloat)
              val inverted = PApplet.map(distance, 0, fl, 1.0f, 0)
              output.field(x)(y) = PVector.lerp(cell, perf, inverted).normalize()
            } else {
              output.field(x)(y) = cell.copy()
            }
        }
    }
  }

  override def draw(): Unit = {
    background(0)
    flowField.mutate()
    val modifiedField = new FlowField(8, 512, 424, this)
    createModifiedFlowField(new PVector(mouseX, mouseY), flowField, modifiedField)
    if (drawField) modifiedField.draw()
    val vs = vehicles.map { v =>
      val vehicle = FlowFollowingVehicle.nextStep(v, modifiedField, 512, 424)
      v.render(this)
      vehicle
    }
    vehicles = vs

  }
}
