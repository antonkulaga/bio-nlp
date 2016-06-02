package org.denigma.nlp.messages.Annotations


object Document {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Document] = PicklerGenerator.generatePickler[Document]

  lazy val empty = Document("", Vector.empty, "")
}
case class Document( id: String = "",
                     sentences: Vector[Sentence],
                     //coreferenceChains:Option[CorefChains],
                     //discourseTree: Option[DiscourseTree],
                     text: String) {

  def hasId = id != ""

  /*
  lazy val fullText: String = text.getOrElse{ sentences.foldLeft(""){
    case (acc, s)=> acc+s.text
  }.trim }
*/
  def absolutePosition(mention: Mention): Interval = {
    require(mention.sentenceNum < sentences.length, "mention should refer to a sentence that exists")
    val offsets = sentencesOffsets
    offsets.get(mention.sentenceNum).map {
      case (offset, sentence) =>
        mention.span + offset
    }.getOrElse{
      mention.span
    }
  }

  lazy val sentencesOffsets: Map[Int, (Int, Sentence)] = sentences.zipWithIndex.foldLeft(List.empty[(Int, (Int, Sentence))]){
    case (Nil, (sentence, index) ) => (index -> (0 -> sentence))::Nil
    case ( (prev , (prevOffset , previous))::before, (sentence, index)) =>
      val offset = prevOffset + previous.text.length + 1
      (index -> (offset -> sentence))::(prev , (prevOffset , previous))::before
  }.reverse.toMap
}

object KBEntry {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[KBEntry] = PicklerGenerator.generatePickler[KBEntry]
}

case class KBEntry(id: String, text: String, key: String, namespace: String, species: String)
{
  lazy val miriam = s"http://identifiers.org/${namespace}/${id}"
}

object KBResolution {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[KBResolution] = PicklerGenerator.generatePickler[KBResolution]
}

case class KBResolution(
                         entry: KBEntry,
                         metaInfo: Map[String, String] = Map.empty
                       )
object Interval {
  lazy val empty: Interval = Interval(0, 0)

  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Interval] = PicklerGenerator.generatePickler[Interval]
}

case class Interval (start: Int, end: Int) {
  def isEmpty: Boolean = this == Interval.empty

  def +(offset: Int) = Interval(start + offset, end + offset)
}


object Sentence {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Sentence] = PicklerGenerator.generatePickler[Sentence]
}

case class Sentence(
                     /** Actual tokens in this sentence */
                     words: List[String],
                     /** Start character offsets for the words; start at 0 */
                     startOffsets: List[Int],
                     /** End character offsets for the words; start at 0 */
                     endOffsets: List[Int],
                     /** POS tags for words */
                     tags: List[String],
                     /** Lemmas */
                     lemmas: List[String],
                     /** NE labels */
                     entities: List[String],
                     /** Normalized values of named/numeric entities, such as dates */
                     norms: List[String],
                     /** Shallow parsing labels */
                     chunks:List[String]
                     /*
                       /** Constituent tree of this sentence; includes head words */
                       var syntacticTree:Option[Tree],
                       /** DAG of syntactic and semantic dependencies; word offsets start at 0 */
                     //  var dependenciesByType:DependencyMap
                     */
                   ) {
  lazy val text:String =  getSentenceFragmentText(0, words.length)

  def getSentenceFragmentText(start:Int, end:Int):String = {
    // optimize the single token case
    if(end - start == 1) words(start)

    val text = new scala.collection.mutable.StringBuilder()
    for(i <- start until end) {
      if(i > start) {
        // add as many white spaces as recorded between tokens
        // sometimes this space is negative: in BioNLPProcessor we replace "/" with "and"
        //   in these cases, let's make sure we print 1 space, otherwise the text is hard to read
        val numberOfSpaces = math.max(1, startOffsets(i) - endOffsets(i - 1))
        for (j <- 0 until numberOfSpaces) {
          text.append(" ")
        }
      }
      text.append(words(i))
    }
    text.toString()
  }

}

case class DiscourseTree (
                           /** Label of this tree, if non-terminal */
                           relationLabel: String,
                           /** Direction of the relation, if non-terminal */
                           relationDirection: RelationDirection.Value,

                           /** Children of this non-terminal node */
                           children: Array[DiscourseTree],

                           /** Nucleus or Satellite; used only during reading */
                           kind:TreeKind.Value,

                           /** Raw text attached to this node */
                           rawText: String,

                           /** Character offsets for the rawText; used only during reading */
                           charOffsets: (Int, Int),

                           /** Position of the first token in the annotation for this discourse tree */
                           firstToken: TokenOffset = null,

                           /** Position of the last token in the annotation for this discourse tree; this is inclusive! */
                           lastToken: TokenOffset = null,

                           /** Index of the first EDU in this tree in the array of EDUs for the first sentence */
                           firstEDU: Int = -1,

                           /** Index of the last EDU in this tree in the array of EDUs for the last sentence (inclusive) */
                           lastEDU: Int = -1)


case class TokenOffset (sentence:Int, token:Int) {
  override def toString:String = s"($sentence, $token)"
}

case object TreeKind extends Enumeration {
  type TreeKind = Value
  val Nucleus, Satellite, Root = Value
}

case object RelationDirection extends Enumeration {
  type RelationDirection = Value
  val LeftToRight, RightToLeft, None = Value
}

