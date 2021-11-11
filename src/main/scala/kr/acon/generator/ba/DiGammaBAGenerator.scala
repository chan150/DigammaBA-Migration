package kr.acon.generator.ba

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import kr.acon.generator.BaseGenerator
import kr.acon.parser.TrillionBAParser
import org.apache.spark.rdd.RDD

object DiGammaBAGenerator extends BaseGenerator {
  override val appName = "DiGammaBA generator (experimental)"

  override val parser = new TrillionBAParser

  override def postProcessing() {
  }

  override def run: RDD[(Long, LongOpenHashBigSet)] = {

    val ba = BA.constructFrom(parser)
    val bba = sc.broadcast(ba)

    import kr.acon.util.Utilities._

    val range = sc.rangeHash(parser.bam0, parser.ban - 1, 1, parser.machine)

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
    }

    import org.apache.commons.math3.special.Gamma

    def f(e: Double, a: Double, b: Double, d: Double) = {
      Gamma.digamma(e / d + b + 1) - Gamma.digamma(e / d + a)
    } / d

    //    println( f(100, 0,100, 1) )
    //    println( f(100, 100,100, 1) )

//    val e = 1000000 // = local edge (seed)
    //    val d = 20
    //
    //    println(f(e, 0, e, d) / (e to (1 + d) * e).map(1d / _.toDouble).reduce(_ + _))
    //    println(f(e, e, e, d) / (1d / (1 + d) * e))

    val edges2 = degrees.map {
      case (vid, degree) =>

//        numedges = f(parser.bal)
        (vid,  degree)
    }

    // stage 2: get final graph
    edges
  }


}
