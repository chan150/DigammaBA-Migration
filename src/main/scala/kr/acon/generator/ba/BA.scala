package kr.acon.generator.ba

import it.unimi.dsi.fastutil.longs.{LongArraySet, LongBigArrays, LongOpenHashBigSet}
import kr.acon.parser.TrillionBAParser
import kr.acon.util.HashFunctions

import scala.util.Random

object BA {
  @inline final def constructFrom(p: TrillionBAParser) = {
    if (p.model != "none") {
      new OptimizedModel(p)
    } else if (p.bap != 0 || p.baStatic) {
      new CopyingModel(p)
    } else {
      new BA(p)
    }
  }

  @inline final type randomClass = Random
}

trait InterfaceBA extends Serializable {
  def runtimeCheck(f: () => Unit, JobName: String) {
    val startTime = System.currentTimeMillis
    f()
    val endTime = System.currentTimeMillis
    println("[%s]\t: %d ms".format(JobName, (endTime - startTime)))
  }

  implicit class ImplArrayArrayLong(self: Array[Array[Long]]) {
    def set(eid: Long, value: Long) = LongBigArrays.set(self, eid, value)

    def get(eid: Long) = LongBigArrays.get(self, eid)
  }

}

class ModelA(n: Long, m: Int, m0: Int) extends OptimizedModel(n, m, m0, m0, false, 1d) {
  private[generator] def this(p: TrillionBAParser) {
    this(p.ban, p.bam, p.bam0)
  }
}

class ModelB(n: Long, m: Int, m0: Int) extends OptimizedModel(n, m, m0, m0, true, 0d) {
  private[generator] def this(p: TrillionBAParser) {
    this(p.ban, p.bam, p.bam0)
  }
}


class CopyingModel(n: Long, m: Int, m0: Int, p: Double) extends OptimizedModel(n, m, m0, m0, false, p) {
  private[generator] def this(p: TrillionBAParser) {
    this(p.ban, p.bam, p.bam0, p.bap)
  }
}

class OptimizedModel(n: Long, m: Int, m0: Int, l: Long, isStatic: Boolean, p: Double) extends BA(n, m, m0, l) {
  require(0 <= p && p <= 1, s"[Optimized model] 0 <= p <= 1, p=${p}")

  val pLong = p * Long.MaxValue
  val vidFunc = if (isStatic) {
    (_: Long) => n
  } else {
    (vid: Long) => vid
  }

  val destFunc = if (p != 0) {
    (vid: Long, eid: Long, x: Long) =>
      val r = HashFunctions.byXORSHIFT(vid * n + x)
      if (r <= pLong) {
        r % vid
      } else {
        if (eid < eidLocalMax) {
          edges.get(eid)
        } else {
          destByReference(vid, eid, x)
        }
      }
  } else {
    (vid: Long, eid: Long, x: Long) =>
      if (eid < eidLocalMax) {
        edges.get(eid)
      } else {
        destByReference(vid, eid, x)
      }
  }

  val srcFunc = if (isStatic) {
    eid: Long =>
      val r = HashFunctions.byXORSHIFT(eid)
      r % n
  } else {
    eid: Long =>
      super.src(eid)
  }

//  val determineAllFunc: Iterator[(Long, LongOpenHashBigSet)] = {
//    if (isStatic) {
//      vid: Long =>
//        val len = if (vid == m0) m0 else m
//        val res = for (i <- 0 until len) yield {
//          val eid = getEid(vid, i)
//          val adjacency = new LongOpenHashBigSet(1)
//          adjacency.add(dest(vid, eid, i))
//          (src(eid), adjacency)
//        }
//        res.toIterator
//    } else {
//      vid: Long =>
//        super.determineAll(vid)
//    }
//  }

//  override def determineAll(vid: Long): Iterator[(Long, LongOpenHashBigSet)] = {
//    determineAllFunc(vid)
//  }

  override def src(eid: Long): Long = {
    srcFunc(eid)
  }

  override def dest(vid: Long, eid: Long, x: Long): Long = {
    destFunc(vid, eid, x)
  }

  private[generator] def this(p: TrillionBAParser) {
    this(p.ban, p.bam, p.bam0, p.bal, p.baStatic, p.bap)
  }
}

class BA(n: Long, m: Int, m0: Int, l: Long) extends LocalBA(l: Long, m: Int, m0: Int) {
  require(m <= m0, s"[Parallel] m0 must be equal or larger than m, m0=${m0}, m=${m}")
  require(m0 <= n, s"[Parallel] n must be equal or larger than m0, n=${n}, m0=${m0}")
  require(1 <= m, s"[Parallel] m must be equal or larger than 1, m=${m}")
  require(m0 <= l && l <= n, s"[Parallel] m0 <= l <= n, m0=${m0}, l=${l}, n=${n}")

  @inline val vidStart = m0.toLong
  @inline val vidEnd = n

  //TODO: debug
  def checkAll(vid: Long): Long = {
    val len = if (vid == m0) m0 else m
    val adjacency = new LongOpenHashBigSet(len)

    var collision = 0
    var i = 0
    while (adjacency.size64() < len) {
      val eid = getEid(vid, adjacency.size64())
      val e = dest(vid, eid, i)
      if(adjacency.contains(e)){
        collision +=1
      }
      adjacency.add(e)
      i += 1
    }
    collision
  }

  def determineAll(vid: Long): Iterator[(Long, LongOpenHashBigSet)] = {
    val len = if (vid == m0) m0 else m
    val adjacency = new LongOpenHashBigSet(len)

    var i = 0
    while (adjacency.size64() < len) {
      val eid = getEid(vid, adjacency.size64())
      val e = dest(vid, eid, i)
      adjacency.add(e)
      i += 1
    }
    Iterator((vid, adjacency))
  }

  def getVid(eid: Long): (Long, Long) = {
    if (eid < m0) {
      (m0.toLong, eid)
    } else {
      ((eid - m0) / m + m0, (eid - m0) % m)
    }
  }

  override def edge(eid: Long, direction: Boolean): Long = {
    direction match {
      case false => src(eid)
      case true => {
        val (vid, x) = getVid(eid)
        dest(vid, eid, x)
      }
    }
  }

  def dest(vid: Long, eid: Long, x: Long): Long = {
    //    println(vid, eid, x)
    if (eid < eidLocalMax) {
      edges.get(eid)
    } else {
      destByReference(vid, eid, x)
    }
  }

  def destByReference(vid: Long, eid: Long, x: Long): Long = {
    val base = eid - ((eid - m0) % m)
    val r = HashFunctions
      //            .byPrimitiveRandom(eid)
      .byXORSHIFT(vid * n + x)
    //        .byMD5(eid)
    edge(r % base, r % 2 != 0) // r % 2 != 0
  }

  private[generator] def this(p: TrillionBAParser) {
    this(p.ban, p.bam, p.bam0, p.bal)
  }
}

class LocalBA(n: Long, m: Int, m0: Int) extends InterfaceBA {
  require(m <= m0, s"[Local] m0 must be equal or larger than m, m0=${m0}, m=${m}")
  require(m0 <= n, s"[Local] n must be equal or larger than m0, n=${n}, m0=${m0}")
  require(1 <= m, s"[Local] m must be equal or larger than 1, m=${m}")

  @inline val edges = LongBigArrays.newBigArray((n - m0) * m + m0)
  @inline val vidNewStart = m0.toLong
  @inline val eidLocalMax = if (edges != null) LongBigArrays.length(edges) else 0 //= (n - m0) * m + m0

  @inline lazy val random = new Random
  @inline val set = new LongArraySet(m)

  if (edges != null) {
    @inline def processLocal() {
      for (eid <- 0 until m0) {
        edges.set(eid, eid)
      }
      for (vid <- m0.toLong + 1 to n) {
        updateVertex(vid)
      }
    }

    runtimeCheck(processLocal, "Runtime for Local BA")
  }

  def determine(eid: Long) = {
    val (eidNext, direction) = rng(eid)
    edge(eidNext, direction)
  }

  def rng(eid: Long) = {
    val base = eid - ((eid - m0) % m)
    (random.nextLong.abs % base, random.nextBoolean())
  }

  //  def edge(eid: Long, direction: Boolean):Long = {
  //    direction match {
  //      case false => determine(eid)
  //      case true => eid / m
  //    }
  //  }

  def edge(eid: Long, direction: Boolean) = {
    direction match {
      case false => src(eid)
      case true => dest(eid)
    }
  }

  def src(eid: Long) = {
    if (eid < m0)
      m0.toLong
    else
      (eid - m0) / m  + vidNewStart + 1
  }

  def dest(eid: Long) = {
    edges.get(eid)
  }

  def getEid(vid: Long, x: Long) = {
    if (m0 == vid) {
      x
    } else {
      (vid - m0 - 1) * m + m0 + x
    }
  }

  def updateVertex(vid: Long) {
    set.clear()
    while (set.size() < m) {
      val eid = getEid(vid, set.size())
      val newLink = determine(eid)
      set.add(newLink)
    }
    var index = 0l
    val it = set.iterator()
    while (it.hasNext) {
      val eid = getEid(vid, index)
      edges.set(eid, it.nextLong())
      index += 1
    }
  }
}
