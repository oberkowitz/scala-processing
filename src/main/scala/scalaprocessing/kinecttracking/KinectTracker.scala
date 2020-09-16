package scalaprocessing.kinecttracking

import org.openkinect.processing.Kinect2
import processing.core.{PApplet, PConstants, PVector}

class KinectTracker(val pa: PApplet) { //enable Kinect2
  val kinect2 = new Kinect2(pa)
  kinect2.initDepth()
  kinect2.initDevice()
  // Make a blank image
  val display = pa.createImage(kinect2.depthWidth, kinect2.depthHeight, PConstants.RGB)
  // Set up the vectors
  loc = new PVector(0, 0)
  lerpedLoc = new PVector(0, 0)
  // Depth threshold
  var threshold = 745
  // Raw location
  var loc: PVector = new PVector(0, 0)
  // Interpolated location
  var lerpedLoc: PVector = new PVector(0, 0)
  // Depth data
  var depth: Array[Int] = null

  def track(): Unit = { // Get the raw depth as array of integers
    depth = kinect2.getRawDepth
    // Being overly cautious here
    if (depth == null) return
    var sumX = 0
    var sumY = 0
    var count = 0
    var x = 0
    while ( {
      x < kinect2.depthWidth
    }) {
      var y = 0
      while ( {
        y < kinect2.depthHeight
      }) { // Mirroring the image
        val offset = kinect2.depthWidth - x - 1 + y * kinect2.depthWidth
        // Grabbing the raw depth
        val rawDepth = depth(offset)
        // Testing against threshold
        if (rawDepth > 0 && rawDepth < threshold) {
          sumX += x
          sumY += y
          count += 1
        }

        {
          y += 1;
          y - 1
        }
      }

      {
        x += 1;
        x - 1
      }
    }
    // As long as we found something
    if (count != 0) loc = new PVector(sumX / count, sumY / count)
    // Interpolating the location, doing it arbitrarily for now
    lerpedLoc.x = PApplet.lerp(lerpedLoc.x, loc.x, 0.3f)
    lerpedLoc.y = PApplet.lerp(lerpedLoc.y, loc.y, 0.3f)
  }

  def getLerpedPos = lerpedLoc

  def getPos = loc

  def displayer(): Unit = {
    val img = kinect2.getDepthImage
    if (depth == null || img == null) return
    // Going to rewrite the depth image to show which pixels are in threshold
    // A lot of this is redundant, but this is just for demonstration purposes
    display.loadPixels()
    var x = 0
    while ( {
      x < kinect2.depthWidth
    }) {
      var y = 0
      while ( {
        y < kinect2.depthHeight
      }) { // mirroring image
        val offset = (kinect2.depthWidth - x - 1) + y * kinect2.depthWidth
        // Raw depth
        val rawDepth = depth(offset)
        val pixelIndex = x + y * display.width
        if (rawDepth > 0 && rawDepth < threshold) { // A red color instead
          display.pixels(pixelIndex) = pa.color(150, 50, 50)
        }
        else display.pixels(pixelIndex) = img.pixels(offset)

        {
          y += 1;
          y - 1
        }
      }

      {
        x += 1;
        x - 1
      }
    }
    display.updatePixels()
    // Draw the image
    pa.image(display, 0, 0)
  }

  def getThreshold = threshold

  def setThreshold(t: Int): Unit = {
    threshold = t
  }
}

