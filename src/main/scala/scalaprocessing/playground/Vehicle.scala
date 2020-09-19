package scalaprocessing.playground

import processing.core.{PApplet, PConstants, PVector}
import scalaprocessing.playground.util.util._

abstract class Vehicle[Context] {
  def location: PVector
  def velocity: PVector
  def acceleration: PVector // Not necessary?

  val maximumSpeed: Float
  val maximumForce: Float

  def render(pa: PApplet): Unit

  def desiredVelocity(context: Context): PVector

  def steeringForce(context: Context): PVector = {
    val desired = desiredVelocity(context)
    val steer = (desired.normalized * maximumSpeed) - velocity
    steer.lim(maximumForce)
  }

}

object SeekingVehicle {
  def nextStep(vehicle: SeekingVehicle, a: PApplet): SeekingVehicle = {
    val newVelocity = (vehicle.velocity + vehicle.steeringForce(a)).lim(vehicle.maximumSpeed)
    val newLocation = newVelocity + vehicle.location
    new SeekingVehicle(newLocation, newVelocity)
  }
}

case class Seeker(location: PVector, velocity: PVector)

class SeekingVehicle(val location: PVector, val velocity: PVector) extends Vehicle[PApplet] {
  val r = 3f

  var acc = new PVector(0, 0)

  override def acceleration = acc

  override val maximumSpeed = 4f
  override val maximumForce = .1f

  //  override def render(pa: PApplet): Unit = ???
  override def desiredVelocity(pApplet: PApplet) = {
    val target = new PVector(pApplet.mouseX, pApplet.mouseY)

    target - location
  }

  override def render(pa: PApplet): Unit = {
    val theta = velocity.heading + PApplet.radians(90)
    // heading2D() above is now heading() but leaving old syntax until Processing.js catches up
    pa.fill(200, 100)
    pa.stroke(255)
    pa.pushMatrix()
    pa.translate(location.x, location.y)
    pa.rotate(theta)
    pa.beginShape(PConstants.TRIANGLES)
    pa.vertex(0, -r * 2)
    pa.vertex(-r, r * 2)
    pa.vertex(r, r * 2)
    pa.endShape()
    pa.popMatrix()
  }

}
