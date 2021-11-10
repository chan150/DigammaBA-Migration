name := "DiGammaBA"

version := "1.0"

scalaVersion := "2.12.6"

//javacOptions ++= Seq("-source", "1.8", "-g:none")

//scalacOptions ++= Seq("-optimise", "-optimize","-target:jvm-1.8", "-Yinline-warnings")

libraryDependencies += "it.unimi.dsi" % "fastutil" % "8.5.6"

libraryDependencies += "it.unimi.dsi" % "dsiutils" % "2.6.17"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.0"

libraryDependencies += "org.apache.spark" %% "spark-graphx" % "3.2.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.0"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.2.2"