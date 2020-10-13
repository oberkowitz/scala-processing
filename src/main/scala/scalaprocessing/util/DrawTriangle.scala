package scalaprocessing.util

import processing.core.{PApplet, PConstants, PGraphics, PVector}

object DrawTriangle {

  def draw(location: PVector, velocity: PVector, pg: PGraphics, radius: Float): Unit = {
    val theta = velocity.heading + PApplet.radians(90)
    pg.fill(200, 255)
    pg.stroke(255)
    pg.pushMatrix()
    pg.translate(location.x, location.y)
    pg.rotate(theta)
    pg.beginShape(PConstants.TRIANGLES)
    pg.vertex(0, -radius * 2)
    pg.vertex(-radius, radius * 2)
    pg.vertex(radius, radius * 2)
    pg.endShape()
    pg.popMatrix()
  }

}
