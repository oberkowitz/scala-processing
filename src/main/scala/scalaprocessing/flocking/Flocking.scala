package scalaprocessing.flocking

import processing.core.{PApplet, PConstants}

// Ported from Dan Shiffman's example built into Processing
class Flocking extends PApplet {
  val flock = new Flock

  override def settings(): Unit = {
    size(640, 360)
  }
  override def setup() {
    // Add an initial set of boids into the system
    (0 to 150).foreach { i =>
      flock.addBoid(new Boid(width / 2, height / 2))
    }
  }

  override def draw() {
    background(50)
    flock.run()
    flock.boids.foreach { boid =>
      bordersBoid(boid)
      renderBoid(boid)
    }
  }

  def renderBoid(boid: Boid): Unit = { // Draw a triangle rotated in the direction of velocity
    val theta = boid.velocity.heading2D + PApplet.radians(90)
    // heading2D() above is now heading() but leaving old syntax until Processing.js catches up
    fill(200, 100)
    stroke(255)
    pushMatrix()
    translate(boid.position.x, boid.position.y)
    rotate(theta)
    beginShape(PConstants.TRIANGLES)
    vertex(0, -boid.r * 2)
    vertex(-boid.r, boid.r * 2)
    vertex(boid.r, boid.r * 2)
    endShape()
    popMatrix()
  }

  // Wraparound
  def bordersBoid(boid: Boid): Unit = {
    if (boid.position.x < -boid.r) boid.position.x = width + boid.r
    if (boid.position.y < -boid.r) boid.position.y = height + boid.r
    if (boid.position.x > width + boid.r) boid.position.x = -boid.r
    if (boid.position.y > height + boid.r) boid.position.y = -boid.r
  }

}

object Flocking {
  def main(args: Array[String]) {
    PApplet.main(Array("scalaprocessing.flocking.Flocking"))
  }
}
