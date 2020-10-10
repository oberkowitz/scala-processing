package scalaprocessing.flowfield

import processing.core.{PApplet, PVector}
import scalaprocessing.agents.TriangularVehicle
import scalaprocessing.centroidfield.FlowField
import scalaprocessing.util.util._

class FlowFollowingVehicle(val location: PVector, val velocity: PVector) extends TriangularVehicle[FlowField](5) {
  override val maximumSpeed = (Math.random().toFloat * 4) + 2
  override val maximumForce = (Math.random().toFloat / 2f) + .1f

  override def desiredVelocity(context: FlowField) = {
    context.lookup(location)
  }
}

object FlowFollowingVehicle {
  def nextStep(vehicle: FlowFollowingVehicle, f: FlowField, width: Int, height: Int): FlowFollowingVehicle = {
    def borders(location: PVector, radius: Float, width: Int, height: Int): Unit = {
      if (location.x < -radius) location.x = width + radius
      if (location.y < -radius) location.y = height + radius
      if (location.x > width + radius) location.x = -radius
      if (location.y > height + radius) location.y = -radius
    }

    val newVelocity = (vehicle.velocity + vehicle.steeringForce(f)).lim(vehicle.maximumSpeed)
    val newLocation = newVelocity + vehicle.location
    borders(newLocation, vehicle.r, width, height) // Modifies location in place

    new FlowFollowingVehicle(newLocation, newVelocity)
  }

}
