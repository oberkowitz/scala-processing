package scalaprocessing.sound
import processing.core.PApplet
import processing.core.PApplet.map
import processing.sound._

class SoundDemo extends PApplet{

  val saw = new SawOsc(this);
  override def settings() {
    size(640, 360);
  }

  override def setup() {
    background(255);

    saw.play();
  }

  override def draw() {
    // Map mouseY from 1.0 to 0.0 for amplitude (mouseY is 0 at the
    // top of the sketch, so the higher the mouse position, the louder)
    val amplitude = map(mouseY, 0, height, 1.0f, 0.0f)
    saw.amp(amplitude)

    // Map mouseX from 20Hz to 1000Hz for frequency
    val frequency = map(mouseX, 0, width, 20.0f, 1000.0f)
    saw.freq(frequency)

    // Map mouseX from -1.0 to 1.0 for panning the audio to the left or right
    val panning = map(mouseX, 0, width, -1.0f, 1.0f)
    saw.pan(panning)
  }
}
object Main {
  def main(args: Array[String]) {
    PApplet.main(Array("scalaprocessing.sound.SoundDemo"))
  }
}
