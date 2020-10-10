package scalaprocessing.centroidfield

import processing.core.PApplet.map
import processing.core.{PApplet, PVector}

class FlowField(val resolution: Int, val width: Int, val height: Int, pa: PApplet) {

  val cols = width / resolution
  val rows = height / resolution

  val field: Array[Array[PVector]] = Array.ofDim[PVector](cols, rows)

  def forEach(f: (Int, Int, PVector) => Unit) = {
    field.zipWithIndex.foreach {
      case (column, i) =>
        column.zipWithIndex.foreach {
          case (cell, j) =>
            f(i, j, cell)
        }
    }
  }

  def copyField(f: Array[Array[PVector]]): Unit = {
    f.zipWithIndex.foreach {
      case (column, i) =>
        column.zipWithIndex.foreach {
          case (cell, j) =>
            field(i)(j) = cell
        }
    }
  }

  def init(): Unit = {
    pa.noiseSeed(pa.random(10000).toLong)
    (0 until cols).foreach { x =>
      (0 until rows).foreach { y =>
        val theta =
          map(pa.noise(x * .1f, y * .1f), 0, 1, 0, (Math.PI * 2).toFloat)
        field(x)(y) = new PVector(Math.cos(theta).toFloat, Math.sin(theta).toFloat)
      }
    }
  }

  def mutate(factor: Float = .001f): Unit = {
    pa.noiseSeed(pa.random(10000).toLong)
    (0 until cols).foreach { x =>
      (0 until rows).foreach { y =>
        val theta =
          map(pa.noise(x * .1f, y * .1f), 0, 1, 0, (Math.PI * 2).toFloat)
        field(x)(y).rotate(theta * .001f)
      }
    }
  }

  def lookup(vec: PVector): PVector = {
    val column = PApplet.constrain(vec.x / resolution, 0, cols - 1).toInt
    val row = PApplet.constrain(vec.y / resolution, 0, rows - 1).toInt
    field(column)(row)
  }

  def draw(): Unit = {
    field.zipWithIndex.foreach {
      case (vs, x) => vs.zipWithIndex.foreach {
        case (pv, y) => drawVector(pv, x * resolution, y * resolution, resolution - 2)
      }
    }
  }

  private def drawVector(v: PVector, x: Float, y: Float, scayl: Float): Unit = {
    pa.pushMatrix()
    val arrowsize = 4
    // Translate to position to render vector
    pa.translate(x, y)
    pa.stroke(255, 100)
    // Call vector heading function to get direction (note that pointing to the right is a heading of 0) and rotate
    pa.rotate(v.heading)
    // Calculate length of vector & scale it to be bigger or smaller if necessary
    val len = v.mag * scayl
    // Draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
    pa.line(0, 0, len, 0)
    //line(len,0,len-arrowsize,+arrowsize/2);
    //line(len,0,len-arrowsize,-arrowsize/2);
    pa.popMatrix()
  }

}


