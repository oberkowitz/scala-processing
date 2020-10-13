package scalaprocessing.playground

import processing.core.{PApplet, PVector}

class SeekingSketch extends PApplet {
  var vehicle = new SeekingVehicle(new PVector(200, 200), new PVector(0, 0))

  override def settings(): Unit = {
    size(640, 360)
  }

  override def draw(): Unit = {
    background(0)
    vehicle = SeekingVehicle.nextStep(vehicle, this)
    vehicle.render(this.getGraphics)
  }
}

object SeekingSketch {
  def main(args: Array[String]): Unit = {
    PApplet.main(Array("scalaprocessing.playground.SeekingSketch"))
  }
}
