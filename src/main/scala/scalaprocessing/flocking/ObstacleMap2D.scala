package scalaprocessing.flocking

import scala.util.Try

sealed abstract class ObstacleMap2D(map: Array[Array[Float]]) {

  def getValue(x: Int, y: Int): Option[Float] = {
    Try(map(x)(y)).toOption
  }

  def setValue(x: Int, y: Int, value: Float): Unit = {
    map(x)(y) = value
  }

  def x: Int = map.length
  def y: Int = map.head.length

}

object ObstacleMap2D {
  def create(x: Int, y: Int) = {
    val matrix = Array.ofDim[Float](x, y)
    new ObstacleMap2D(matrix) {}
  }

  def reslice(obstacleMap2D: ObstacleMap2D, scale: Int): Option[ObstacleMap2D] = {
    val newX = obstacleMap2D.x / scale
    val newY = obstacleMap2D.y / scale

    val newMap = Array.ofDim[Float](newX, newY)
    ???

  }
}
