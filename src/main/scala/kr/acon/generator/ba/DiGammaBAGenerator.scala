package kr.acon.generator.ba

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import kr.acon.generator.BaseGenerator
import kr.acon.parser.TrillionBAParser
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.util.Random

object DiGammaBAGenerator extends BaseGenerator {
  override val appName = "DiGammaBA generator (experimental)"

  override val parser = new TrillionBAParser

  override def postProcessing() {
  }

  override def run: RDD[(Long, LongOpenHashBigSet)] = {

    val ba = BA.constructFrom(parser)
    val bba = sc.broadcast(ba)

    import kr.acon.util.Utilities._

    val range = sc.rangeHash(parser.bam0, parser.bal - 1, 1, parser.machine)

    // stage 1: get local graph

    val edges = range.mapPartitions {
      val b = bba.value
      partitions =>
        partitions.flatMap {
          vid =>
            b.determineAll(vid)
        }
    }

    val degrees = edges.flatMap {
      case (vid, adjacency) =>
        new Iterator[(Long, Long)] {
          val iter = adjacency.iterator()

          override def hasNext: Boolean = iter.hasNext

          override def next(): (Long, Long) = (vid, iter.nextLong())
        }
      //        adjacency
      //          .toArray(Array[Long]).map((vid, _))
    }.flatMap {
      case (u, v) =>
        Iterator((u, 1L), (v, 1L))
    }.reduceByKey {
      _ + _
    }.map(x => (x._2, 0l))

    import org.apache.commons.math3.special.Gamma
    def f(e: Double, a: Double, b: Double, d: Double) =
      (Gamma.digamma(e / d + b + 1) - Gamma.digamma(e / d + a)) / d

    def getPosition(gp: Double, start: Long, end: Long, numOfExistingEdges: Long, numOfNewVertices: Long, d: Double): Long = {
      //      println(gp, start, end)
      if (start >= end) {
        //        println(s"=========== $start")
        start
      } else {
        val f2 = f(numOfExistingEdges, start, (start + end) / 2, d)
        //        println(f2)
        if (f2 <= gp) {
          getPosition(gp - f2, (start + end) / 2 + 1, end, numOfExistingEdges, numOfNewVertices, d)
        } else {
          getPosition(gp, start, (start + end) / 2, numOfExistingEdges, numOfNewVertices, d)
        }
      }
    }

    def generateEdges(weight: Double, start: Long, numOfExistingEdges: Double, numOfNewVertices: Double, d: Double): mutable.HashSet[Long] = {
      val s = d * f(numOfExistingEdges, start, numOfNewVertices, d) * weight
      val r = new Random
      val p = s / (numOfNewVertices * d)
      val sn = if (s - s.toInt > r.nextDouble()) 1 else 0
      //            val sk = s.toInt + sn
      val sk = math.round((s + math.sqrt(s * (1 - p)) * r.nextGaussian())).toInt

      val list = new mutable.HashSet[Long]()
      while (list.size < sk && list.size < numOfNewVertices) {
        val gp = r.nextDouble() * f(numOfExistingEdges, start, numOfNewVertices - 1, d)
        val e = getPosition(gp, start, (numOfNewVertices).toLong, numOfExistingEdges.toLong, numOfNewVertices.toLong, d: Double)
        list.add(e)
      }
      //      val list = (0 until sk).map {
      //        x =>
      //          val gp = r.nextDouble() * f(numOfExistingEdges, start, numOfNewVertices - 1, d)
      //          getPosition(gp, start, (numOfNewVertices).toLong, numOfExistingEdges.toLong, numOfNewVertices.toLong, d: Double)
      //      }.toSet

      //        .toSet
      //      println(start, list)
      list.++(list.flatMap { x =>
        if (x + 1 < numOfNewVertices)
          generateEdges(1, x + 1, numOfExistingEdges, numOfNewVertices, d)
        else
          Set.empty[Long]
      })
      //      println(list.toSet)
    }

    def generateAllEdgesInSuqarePart(degrees: Seq[Long], numOfNewVertices: Long, numOfExistingEdges: Long, d: Double) = {
      var acc = 0
      degrees.zipWithIndex.foreach {
        case (x, index) =>
          val list = generateEdges(x, 0, numOfExistingEdges, numOfNewVertices, d).map(_ + degrees.length).toSet
          acc += list.size
          println(index, x, list.size, list)
      }
      (0 until numOfNewVertices.toInt - 1).foreach {
        x =>
          val list = generateEdges(1, x + 1, numOfExistingEdges, numOfNewVertices, d).map(_ + degrees.length).toSet
          acc += list.size
          println(x + degrees.length, list.size, list)
      }
      //      println(acc)
    }

    val numOfNewVertices = parser.ban - parser.bal
    val numOfExistingEdges = parser.bal * parser.bam
    val d = parser.bam
    val dl = parser.bal

    val triangles = sc.range(0l, numOfNewVertices, 1, parser.machine).map(x => (1l, x))

    val allVertices = degrees.++(triangles)

    allVertices.zipWithIndex().flatMap {
      case ((weight, start), index) =>
        val list = generateEdges(weight, start, numOfExistingEdges, numOfNewVertices, d).map(_ + dl)

        val len = list.size
        val adjacency = new LongOpenHashBigSet(len)


        list.foreach {
          e =>
            adjacency.add(e)
        }
        Iterator((index.toLong, adjacency))
    }
  }
}
