BIO-NLP
=======

It is an Akka-http based websocket microservice that is used to provide NLP features for kappa-notebook and similar applications
NLP part is based on awesome REACH library, developed by The Computational Language Understanding (CLU) Lab at University of Arizona.

Brat-Facade
-----------
Brat is a popular javascript library for the text annotations, here I provide a simple facade for core of its features with some extension methods. 

```scala
resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "brat-facade" % "latestVersion" // to add facade
```


Annotator
---------
This subproject is a lib that contains:
 * scala-js-binding based controls to display annotations
 * case classes for communicating between client and bio-nlp server
WARNING: if you have issues with REACH dependency resolutions - git clone and publishLocal REACH.


Chrome-Bio
----------
A chrome extension that allows to annotate biological texts. 
It is on a very early stage.