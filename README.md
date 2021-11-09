# DigammaBA-Migration

```scala
val applicationName = "DiGammaBA"

val baParser = TrillionBAParser(args)
val localBA = BA.localBA(baParser)

val broadcastBA = sc.broadcast(localBA)

val digammaBA = BA.digammaBA(broadcastBA)
```

# working with TEMBO cluster
### a SSH public key should be registered by the cluster manager 

#### issue #1 jdk 11 feasibility
#### issue #2 scala 2.13 - deprecated syntax (2.11 => 2.13)
#### issue #3 DigammaBA => DigammaBA-Migration 

```shell
// get password 
$ get-token

// booking by web site
$ 

// ssh compution machine
$ ssh tem02

// execution on localhost
$ ../spark-3.2.0-bin-hadoop3.2/bin/spark-submit --master local[*] --jars `ls lib/* | xargs echo | tr ' ' ,` --class kr.acon.ApplicationMain target/scala-2.12/digammaba_2.12-1.0.jar DiGammaBA graph4 -format tsv -ba.n 1000 -ba.m 10 -ba.m0 10 -ba.l 1000

// execution on cluster
$ 
```