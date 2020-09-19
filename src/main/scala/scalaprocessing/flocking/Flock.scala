package scalaprocessing.flocking

import scala.collection.mutable.ListBuffer

class Flock() {
  val boids = ListBuffer[Boid]() // Initialize the ArrayList

  def run(): Unit = {
    boids.foreach(_.run(boids.toList)) // Passing the entire list of boids to each boid individually
  }

  def addBoid(b: Boid): Unit = {
    boids += b
  }
}
