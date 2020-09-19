package scalaprocessing.flocking

import processing.core.PVector
// The Boid class

class Boid(val x: Float, val y: Float) {

  val velocity = PVector.random2D() // Initialize to a random 2D vector
  val position = new PVector(x, y)
  val r = 2.0f
  val maxSpeed = 2f
  val maxForce = 0.03f


  def run(boids: List[Boid]): Unit = {
    flock(boids)
  }

  // We accumulate a new acceleration each time based on three rules
  def flock(boids: List[Boid]): Unit = {
    val sep = separate(boids)
    // Separation
    val ali = align(boids)
    // Alignment
    val coh = cohesion(boids) // Cohesion
    // Arbitrarily weight these forces
    sep.mult(1.5f)
    ali.mult(1.0f)
    coh.mult(1.0f)
    // Add the force vectors to acceleration
    val acceleration = List(sep, ali, coh).foldLeft(new PVector(0, 0))(PVector.add)
    velocity.add(acceleration) // mutate the velocity with the acceleration
    velocity.limit(maxSpeed) // limit the velocity
    position.add(velocity) // update the Boid's position according to the calculated velocity
  }

  // Separation
  // Method checks for nearby boids and steers away
  def separate(boids: List[Boid]): PVector = {
    val desiredseparation = 25.0f
    val steer = new PVector(0, 0, 0)
    var count = 0
    // For every boid in the system, check if it's too close
    for (other <- boids) {
      val d = PVector.dist(position, other.position)
      // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
      if ((d > 0) && (d < desiredseparation)) { // Calculate vector pointing away from neighbor
        val diff = PVector.sub(position, other.position)
        diff.normalize()
        diff.div(d) // Weight by distance

        steer.add(diff)
        count += 1 // Keep track of how many

      }
    }
    // Average -- divide by how many
    if (count > 0) steer.div(count.toFloat)
    // As long as the vector is greater than 0
    if (steer.mag > 0) { // First two lines of code below could be condensed with new PVector setMag() method
      // steer.setMag(maxSpeed);
      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize()
      steer.mult(maxSpeed)
      steer.sub(velocity)
      steer.limit(maxForce)
    }
    steer
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  def align(boids: List[Boid]): PVector = {
    val neighborDistance = 50

    val neighborVelocites = boids
        .collect {
          case other if 0 < PVector.dist(position, other.position) && PVector.dist(position, other.position) < neighborDistance =>
            other.velocity
        }

    val sum = new PVector(0, 0)
    var count = 0
    for (other <- boids) {
      val d = PVector.dist(position, other.position)
      if ((d > 0) && (d < neighborDistance)) {
        sum.add(other.velocity)
        count += 1
      }
    }
    if (count > 0) {
      sum.div(count.toFloat)
      sum.normalize()
      sum.mult(maxSpeed)
      val steer = PVector.sub(sum, velocity)
      steer.limit(maxForce)
      steer
    }
    else new PVector(0, 0)
  }

  // Cohesion
  // For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
  def cohesion(boids: List[Boid]): PVector = {
    val neighbordist = 50
    val sum = new PVector(0, 0)
    // Start with empty vector to accumulate all positions
    var count = 0
    for (other <- boids) {
      val d = PVector.dist(position, other.position)
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.position) // Add position

        count += 1
      }
    }
    if (count > 0) {
      sum.div(count)
      seek(sum) // Steer towards the position

    }
    else new PVector(0, 0)
  }

  // A method that calculates and applies a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  def seek(target: PVector) = {
    val desired = PVector.sub(target, position) // A vector pointing from the position to the target
    // Scale to maximum speed
    desired.normalize()
    desired.mult(maxSpeed)
    // Above two lines of code below could be condensed with new PVector setMag() method
    // Not using this method until Processing.js catches up
    // desired.setMag(maxSpeed);
    // Steering = Desired minus Velocity
    val steer = PVector.sub(desired, velocity)
    steer.limit(maxForce) // Limit to maximum steering force

    steer
  }
}
