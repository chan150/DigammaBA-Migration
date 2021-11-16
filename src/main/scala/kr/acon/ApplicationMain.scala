/*
 *
 *       __________  ______    __    ________  _   __   ______
 *      /_  __/ __ \/  _/ /   / /   /  _/ __ \/ | / /  / ____/
 *       / / / /_/ // // /   / /    / // / / /  |/ /  / / __
 *      / / / _, _// // /___/ /____/ // /_/ / /|  /  / /_/ /
 *     /_/ /_/ |_/___/_____/_____/___/\____/_/ |_/   \____/
 *
 *     Copyright (C) 2017 Himchan Park (chan150@dgist.ac.kr)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package kr.acon

import kr.acon.generator.ba.{BAGenerator, DiGammaBAGenerator}

import scala.collection.mutable
import scala.util.Random

object ApplicationMain {
  def main(args: Array[String]): Unit = {
    //    import org.apache.commons.math3.special.Gamma
    //    def f(e: Double, a: Double, b: Double, d: Double) =
    //      (Gamma.digamma(e / d + b + 1) - Gamma.digamma(e / d + a)) / d
    //
    //    def getPosition(gp: Double, start: Long, end: Long, numOfExistingEdges: Long, numOfNewVertices: Long, d: Double): Long = {
    //      //      println(gp, start, end)
    //      if (start >= end) {
    //        //        println(s"=========== $start")
    //        start
    //      } else {
    //        val f2 = f(numOfExistingEdges, start, (start + end) / 2, d)
    //        //        println(f2)
    //        if (f2 <= gp) {
    //          getPosition(gp - f2, (start + end) / 2 + 1, end, numOfExistingEdges, numOfNewVertices, d)
    //        } else {
    //          getPosition(gp, start, (start + end) / 2, numOfExistingEdges, numOfNewVertices, d)
    //        }
    //      }
    //    }
    //
    //    def generateEdges(weight: Double, start: Long, numOfExistingEdges: Double, numOfNewVertices: Double, d: Double, check: Boolean = false): mutable.HashSet[Long] = {
    //      val s = d * f(numOfExistingEdges, start, numOfNewVertices, d) * weight
    //      val r = new Random
    //      val p = s / (numOfNewVertices * d)
    //      val sn = if (s - s.toInt > r.nextDouble()) 1 else 0
    //      //            val sk = s.toInt + sn
    //      val sk = math.round((s + math.sqrt(s * (1 - p)) * r.nextGaussian())).toInt
    //      //      if (check)
    //      //        println(s, sk)
    //
    //      var list = new mutable.HashSet[Long]()
    //      while (list.size < sk && list.size < numOfNewVertices) {
    //        val gp = r.nextDouble() * f(numOfExistingEdges, start, numOfNewVertices - 1, d)
    //        val e = getPosition(gp, start, (numOfNewVertices).toLong, numOfExistingEdges.toLong, numOfNewVertices.toLong, d: Double)
    //        list.add(e)
    //      }
    //      //      val list = (0 until sk).map {
    //      //        x =>
    //      //          val gp = r.nextDouble() * f(numOfExistingEdges, start, numOfNewVertices - 1, d)
    //      //          getPosition(gp, start, (numOfNewVertices).toLong, numOfExistingEdges.toLong, numOfNewVertices.toLong, d: Double)
    //      //      }.toSet
    //
    //      //        .toSet
    //      //      println(start, list)
    //      list.++(list.flatMap { x =>
    //        if (x + 1 < numOfNewVertices)
    //          generateEdges(1, x + 1, numOfExistingEdges, numOfNewVertices, d)
    //        else
    //          Set.empty[Long]
    //      })
    //      //      println(list.toSet)
    //    }
    //
    //    def generateAllEdgesInSuqarePart(degrees: Seq[Long], numOfNewVertices: Long, numOfExistingEdges: Long, d: Double) = {
    //      var acc = 0
    //      degrees.zipWithIndex.foreach {
    //        case (x, index) =>
    //          val list = generateEdges(x, 0, numOfExistingEdges, numOfNewVertices, d, true).map(_ + degrees.length).toSet
    //          acc += list.size
    //          println(index, x, list.size, list)
    //      }
    //      (0 until numOfNewVertices.toInt - 1).foreach {
    //        x =>
    //          val list = generateEdges(1, x + 1, numOfExistingEdges, numOfNewVertices, d, true).map(_ + degrees.length).toSet
    //          acc += list.size
    //          println(x + degrees.length, list.size, list)
    //      }
    //      println(acc)
    //    }
    //
    //    def generateAllEdgesInSuqarePart2(degrees: Seq[Long], numOfNewVertices: Long, numOfExistingEdges: Long, d: Double) = {
    //      var a, b, c, d = 0
    //      (0 until 100).zipWithIndex.foreach {
    //        case _ =>
    //          val list = generateEdges(1, 0, numOfExistingEdges, numOfNewVertices, d, true)
    //          println(list)
    //          list.foreach(x => if (x == 0) a += 1 else if (x == 1) b += 1 else if (x == 2) c += 1 else d += 1)
    //      }
    //      println(a, b, c, d)
    //    }
    //
    //        generateAllEdgesInSuqarePart(Seq(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10), 100, 20, 10)
    //    //    generateAllEdgesInSuqarePart2(Seq(1, 1, 2), 3, 4, 4)
    //    //    println(generateEdges(1, 0, 4, 3, 2, true))
    //
    ////    val a = (0 until 10).map(_ => 0).toArray
    ////    (0 until 10000000).foreach {
    ////      _ =>
    ////        val numOfExistingEdges = 4
    ////        val start = 0
    ////        val numOfNewVertices = 10
    ////        val d = 2
    ////
    ////        val r = new Random()
    ////        val gp = r.nextDouble() * f(numOfExistingEdges, start, numOfNewVertices - 1, d)
    ////        val e = getPosition(gp, start, (numOfNewVertices).toLong, numOfExistingEdges.toLong, numOfNewVertices.toLong, d: Double)
    ////        (0 until numOfNewVertices).foreach {
    ////          z =>
    ////            if (e == z)
    ////              a(z) = a(z) + 1
    ////        }
    ////    }
    ////    println(a.mkString("\t"))

    //    println( f(100, 0,100, 1) )
    //    println( f(100, 100,100, 1) )

    //    val e = 1000000 // = local edge (seed)
    //    val d = 20
    //
    //    println(f(e, 0, e, d) / (e to (1 + d) * e).map(1d / _.toDouble).reduce(_ + _))
    //    println(f(e, e, e, d) / (1d / (1 + d) * e))


    //    println(f(4, 0, 2, 2))

    val apps = Seq("TrillionG", "EvoGraph", "VUpscaler", "DiGammaBA", "TGPS", "LineageBA")
    require(args.length >= 1, s"argument must be larger than 1, args=${args.mkString("\t")}")
    require(apps.contains(args(0)), s"Unknown application, " +
      s"please set application type in [${apps.mkString(", ")}]")

    val remainArgs = args.slice(1, args.length)
    println(s"Launching ${args(0)}...")
    args(0) match {
      case "LineageBA" => {
        BAGenerator(remainArgs)
      }
      case "DiGammaBA" => {
        DiGammaBAGenerator(remainArgs)
        //        println(remainArgs.mkString("\t"))
        //        val parser = new TrillionBAParser
        //        parser.argsParser(remainArgs)
        //        val ba = BA.constructFrom(parser)
        //        (parser.bam0.toInt to parser.ban.toInt).flatMap{
        //          vid=>
        //            ba.determineAll(vid)
        //        }
        //       tembo
        //        val edges = ba.determineAll(20)
        //        edges.foreach(println)
        //        BAGenerator(remainArgs)
      } //SKGGenerator(remainArgs)
      case _ =>
    }

    //    val src = 100
    //    val dst = 101
    //    val edge = (src, dst)
    //
    //    val vnew = 102
    //
    //
    //
    //    import org.apache.commons.math3.special.Gamma
    //    def f(e:Double, a:Double, b:Double, d:Double) =
    //      {Gamma.digamma(e/d + b + 1) - Gamma.digamma(e/d + a)} / d
    //
    ////    println( f(100, 0,100, 1) )
    ////    println( f(100, 100,100, 1) )
    //
    //    val e = 1000000 // = local edge (seed)
    //    val d = 20
    //
    //    println(f(e, 0,e, d) / (e to (1+d)*e).map(1d/_.toDouble).reduce(_+_))
    //    println(f(e, e,e, d) / (1d/(1+d)*e))
  }
}