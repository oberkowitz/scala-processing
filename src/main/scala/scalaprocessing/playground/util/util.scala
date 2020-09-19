package scalaprocessing.playground.util

import processing.core.PVector

package object util {

  implicit class PVectorOps(me: PVector) {
    def -(other: PVector): PVector = PVector.sub(me, other)
    def +(other: PVector): PVector = PVector.add(me, other)
    def *(float: Float): PVector = PVector.mult(me, float)
    def lim(limit: Float): PVector = me.copy().limit(limit)
    def normalized: PVector = me.copy().normalize()
  }

}
