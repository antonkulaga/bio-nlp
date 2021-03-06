package org.denigma.nlp.extractions

import java.io.{File => JFile}

import edu.arizona.sista.processors.Document
import edu.arizona.sista.reach._
import edu.arizona.sista.reach.context.ContextEngineFactory.Engine
import edu.arizona.sista.reach.mentions._

class BioExtractor(config: com.typesafe.config.Config, filePath: String) {

  val contextEngineType = Engine.withName(config.getString("contextEngine.type"))
  val contextConfig = config.getConfig("contextEngine.params").root
  val contextEngineParams: Map[String, String] = edu.arizona.sista.reach.context.createContextEngineParams(contextConfig)
  val reach = new ReachSystem(contextEngineType=contextEngineType, contextParams=contextEngineParams)
  println("I AM READY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

  def annotate(text: String, id: String = "paper", chunkId: String = ""): (Document, Seq[BioMention]) = {
    val doc = reach.mkDoc(text, id, chunkId)
    //val doc: Document = reach.processor.annotate(text, keepText = true)
    //doc.id
    val mentions = reach.extractFrom(doc)
    (doc, mentions)
  }

  /*
  def extractFrom(entries: Seq[FriesEntry], documents: Seq[Document]): Seq[BioMention] = {
    // initialize the context engine
    val contextEngine = ContextEngineFactory.buildEngine(contextEngineType, contextParams)

    val entitiesPerEntry = for (doc <- documents) yield extractEntitiesFrom(doc)
    contextEngine.infer(entries, documents, entitiesPerEntry)
    val entitiesWithContextPerEntry = for (es <- entitiesPerEntry) yield contextEngine.assign(es)
    val eventsPerEntry = for ((doc, es) <- documents zip entitiesWithContextPerEntry) yield {
      val events = extractEventsFrom(doc, es)
      MentionFilter.keepMostCompleteMentions(events, State(events))
    }
    contextEngine.update(eventsPerEntry.flatten)
    val eventsWithContext = contextEngine.assign(eventsPerEntry.flatten)
    val grounded = grounder(eventsWithContext)
    // Coref expects to get all mentions grouped by document
    val resolved = resolveCoref(groupMentionsByDocument(grounded, documents))
    // Coref introduced incomplete Mentions that now need to be pruned
    val complete = MentionFilter.keepMostCompleteMentions(resolved, State(resolved)).map(_.toCorefMention)
    // val complete = MentionFilter.keepMostCompleteMentions(eventsWithContext, State(eventsWithContext)).map(_.toBioMention)

    resolveDisplay(complete)
  }
  */

/*
  def testLoad(paperName: String = "PMC88976.nxml") = {
    def now = new Date()

    val path = filePath / "papers" / paperName
    if (!path.exists) println(s"?????????????????????????? $path PATH DOES NOT EXIST")
    val file = path.toJava
    val startNS = System.nanoTime
    val paperId = paperName.replace(".nxml", "")

    val nxmlReader = new NxmlReader()
    val entries: Seq[FriesEntry] = Try(nxmlReader.readNxml(file)) match {
      case Success(v) => v
      case Failure(e) =>
        //FileUtils.writeStringToFile(logFile, report, true)
        Nil
    }
    //other
  }
  */

}