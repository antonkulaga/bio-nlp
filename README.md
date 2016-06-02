BIO-NLP
=======

It is an Akka-http based websocket microservice that is used to provide NLP features for kappa-notebook and similar applications
NLP part is based on awesome REACH library, developed by The Computational Language Understanding (CLU) Lab at University of Arizona.

Annotator
---------
This subproject is a lib that contains:
 * scala-js-binding based controls to display annotations
 * case classes for communicating between client and bio-nlp server

Chrome-Bio
----------
A chrome extension that allows to annotate biological texts