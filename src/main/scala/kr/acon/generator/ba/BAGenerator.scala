package kr.acon.generator.ba

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet
import kr.acon.generator.BaseGenerator
import kr.acon.parser.TrillionBAParser
import org.apache.spark.rdd.RDD

object BAGenerator extends BaseGenerator {
  override val appName = "TrillionBA: A Trillion-scale Barabasi-Albert Synthetic Graph Generator"

  override val parser = new TrillionBAParser

  override def postProcessing() {
  }

  override def run: RDD[(Long, LongOpenHashBigSet)] = {

    val ba = BA.constructFrom(parser)
    val bba = sc.broadcast(ba)

    import kr.acon.util.Utilities._

    val range = sc.rangeHash(parser.bam0, parser.ban - 1, 1, parser.machine)

    val edges = range.mapPartitions {
      val b = bba.value
      partitions =>
        partitions.flatMap {
          vid =>
            b.determineAll(vid)
        }
    }
    edges
  }


}
