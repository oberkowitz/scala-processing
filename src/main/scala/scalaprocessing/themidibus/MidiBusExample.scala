package scalaprocessing.themidibus

import processing.core.PApplet
import themidibus._

class MidiBusExample extends PApplet {

  val myBus = new MidiBus(this, -1, "Java Sound Synthesizer") // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.

  override def settings() {
    size(400, 400)
  }

  override def setup(): Unit = {
    background(0)
    MidiBus.list() // List all available Midi devices on STDOUT. This will show each device's index and name.

    // Either you can
    //                   Parent In Out
    //                     |    |  |
    //myBus = new MidiBus(this, 0, 1); // Create a new MidiBus using the device index to select the Midi input and output devices respectively.
    // or you can ...
    //                   Parent         In                   Out
    //                     |            |                     |
    //myBus = new MidiBus(this, "IncomingDeviceName", "OutgoingDeviceName"); // Create a new MidiBus using the device names to select the Midi input and output devices respectively.
    // or for testing you could ...
    //                 Parent  In        Out
    //                   |     |          |

  }

  override def draw(): Unit = {
    val channel = 0
    val pitch = 64
    val velocity = 127
    val note = new Note(channel, pitch, velocity)
    myBus.sendNoteOn(note) // Send a Midi noteOn

    delay(200)
    myBus.sendNoteOff(note) // Send a Midi nodeOff

    val number = 0
    val value = 90
    val change = new ControlChange(channel, number, velocity)
    myBus.sendControllerChange(change) // Send a controllerChange

    delay(2000)
  }

  override def delay(time: Int): Unit = {
    val current = millis
    while ( {
      millis < current + time
    }) Thread.`yield`()
  }

  def noteOn(note: Note): Unit = { // Receive a noteOn
    println
    println("Note On:")
    println("--------")
    println("Channel:" + note.channel)
    println("Pitch:" + note.pitch)
    println("Velocity:" + note.velocity)
  }

  def noteOff(note: Note): Unit = { // Receive a noteOff
    println
    println("Note Off:")
    println("--------")
    println("Channel:" + note.channel)
    println("Pitch:" + note.pitch)
    println("Velocity:" + note.velocity)
  }

  def controllerChange(change: ControlChange): Unit = { // Receive a controllerChange
    println
    println("Controller Change:")
    println("--------")
    println("Channel:" + change.channel)
    println("Number:" + change.number)
    println("Value:" + change.value)
  }
}

object MidiBusExample {
  def main(args: Array[String]) {
    PApplet.main(Array("scalaprocessing.themidibus.MidiBusExample"))
  }
}

