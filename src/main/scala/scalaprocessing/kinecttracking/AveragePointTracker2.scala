package scalaprocessing.kinecttracking

import processing.core.{PApplet, PConstants}

class AveragePointTracker2 extends PApplet {
  // Daniel Shiffman and Thomas Sanchez Lengeling
  // Tracking the average location beyond a given depth threshold
  // Thanks to Dan O'Sullivan

  // https://github.com/shiffman/OpenKinect-for-Processing
  // http://shiffman.net/p5/kinect/


  // The kinect stuff is happening in another class
  val tracker = new KinectTracker(this)

  override def settings() {
    size(640, 520)

  }

  override def draw() {
    background(255)

    // Run the tracking analysis
    tracker.track()
    // Show the image
    tracker.displayer()

    // Let's draw the raw location
    val v1 = tracker.getPos
    fill(50, 100, 250, 200)
    noStroke()
    ellipse(v1.x, v1.y, 20, 20)

    // Let's draw the "lerped" location
    val v2 = tracker.getLerpedPos
    fill(100, 250, 50, 200)
    noStroke()
    ellipse(v2.x, v2.y, 20, 20)

    // Display some info
    val t = tracker.getThreshold
    fill(0)
    text("threshold: " + t + "    " + "framerate: " + frameRate.toInt + "    " +
      "UP increase threshold, DOWN decrease threshold", 10, 500)
  }

  // Adjust the threshold with key presses
  override def keyPressed() {
    var t = tracker.getThreshold
    if (key == PConstants.CODED) {
      if (keyCode == PConstants.UP) {
        t += 5
        tracker.setThreshold(t)
      } else if (keyCode == PConstants.DOWN) {
        t -= 5
        tracker.setThreshold(t)
      }
    }
  }
}

object AveragePointTracker2 {
  def main(args: Array[String]) {
    PApplet.main(Array("scalaprocessing.kinecttracking.AveragePointTracker2"))
  }
}

