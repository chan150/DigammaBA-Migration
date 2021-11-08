# DigammaBA-Migration

```scala
val applicationName = "DiGammaBA"

val baParser = TrillionBAParser(args)
val localBA = BA.localBA(baParser)

val broadcastBA = sc.broadcast(localBA)

val digammaBA = BA.digammaBA(broadcastBA)
```

# working with TEMBO cluster
### SSH should be registered by cluster manager 
```shell
// get password 
$ get-token

// booking by web site
$ 
```