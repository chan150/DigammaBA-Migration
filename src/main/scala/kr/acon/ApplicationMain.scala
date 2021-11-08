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

import kr.acon.generator.ba.BAGenerator.parser
import kr.acon.generator.ba.{BA, BAGenerator}
import kr.acon.generator.skg.SKGGenerator
import kr.acon.parser.{Parser, TrillionBAParser}

object ApplicationMain {
  def main(args: Array[String]): Unit = {
    val apps = Seq("TrillionG", "EvoGraph", "VUpscaler", "DiGammaBA", "TGPS", "LineageBA")
    require(args.length >= 1, s"argument must be larger than 1, args=${args.deep}")
    require(apps.contains(args(0)), s"Unknown application, " +
      s"please set application type in [${apps.mkString(", ")}]")

    val remainArgs = args.slice(1, args.length)
    println(s"Launching ${args(0)}...")
    args(0) match {
      case "LineageBA" => {
        println(remainArgs.deep)
        val parser = new TrillionBAParser
        parser.argsParser(remainArgs)
        val ba = BA.constructFrom(parser)
        (parser.bam0.toInt to parser.ban.toInt).flatMap{
          vid=>
            ba.determineAll(vid)
        }
//       tembo
//        val edges = ba.determineAll(20)
//        edges.foreach(println)
//        BAGenerator(remainArgs)
      }//SKGGenerator(remainArgs)
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