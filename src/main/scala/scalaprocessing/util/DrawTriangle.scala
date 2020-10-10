package scalaprocessing.util

import processing.core.{PApplet, PConstants, PVector}

object DrawTriangle {

  def draw(location: PVector, velocity: PVector, pApplet: PApplet, radius: Float): Unit = {
    val theta = velocity.heading + PApplet.radians(90)
    pApplet.fill(200, 255)
    pApplet.stroke(255)
    pApplet.pushMatrix()
    pApplet.translate(location.x, location.y)
    pApplet.rotate(theta)
    pApplet.beginShape(PConstants.TRIANGLES)
    pApplet.vertex(0, -radius * 2)
    pApplet.vertex(-radius, radius * 2)
    pApplet.vertex(radius, radius * 2)
    pApplet.endShape()
    pApplet.popMatrix()
  }

}
