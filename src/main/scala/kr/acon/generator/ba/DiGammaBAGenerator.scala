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
        adjacency.toArray(Array[Long]).map((vid, _))
    }.flatMap {
      case (u, v) =>
        Iterator((u, 1L), (v, 1L))
    }.reduceByKey {
      _ + _
    }

    val edges2 = degrees.map {
      case (vid, degree) =>

    }

    // stage 2: get final graph
    edges
  }


}
