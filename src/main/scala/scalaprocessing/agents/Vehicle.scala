package scalaprocessing.agents

import processing.core.{PApplet, PConstants, PVector}
import scalaprocessing.util.DrawTriangle
import scalaprocessing.util.util._

abstract class Vehicle[Context] {

  type Action = Context => PVector

  val location: PVector
  val velocity: PVector

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

abstract class TriangularVehicle[C](val r: Int) extends Vehicle[C] {
  override def render(pa: PApplet): Unit = {
    DrawTriangle.draw(location, velocity, pa, r)
  }
}
