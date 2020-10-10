package scalaprocessing.playground

import processing.core.{PApplet, PConstants, PVector}
import scalaprocessing.agents.Vehicle
import scalaprocessing.util.DrawTriangle
import scalaprocessing.util.util._


object SeekingVehicle {
  def nextStep(vehicle: SeekingVehicle, a: PApplet): SeekingVehicle = {
    val newVelocity = (vehicle.velocity + vehicle.steeringForce(a)).lim(vehicle.maximumSpeed)
    val newLocation = newVelocity + vehicle.location
    new SeekingVehicle(newLocation, newVelocity)
  }
}

class SeekingVehicle(val location: PVector, val velocity: PVector) extends Vehicle[PApplet] {
  val r = 5f

  var acc = new PVector(0, 0)

  override val maximumSpeed = 4f
  override val maximumForce = .1f

  //  override def render(pa: PApplet): Unit = ???
  override def desiredVelocity(pApplet: PApplet): PVector = {
    val target = new PVector(pApplet.mouseX, pApplet.mouseY)

    target - location
  }

  override def render(pa: PApplet): Unit = {
    DrawTriangle.draw(location, velocity, pa, r)
  }

}
