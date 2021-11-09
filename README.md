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
```