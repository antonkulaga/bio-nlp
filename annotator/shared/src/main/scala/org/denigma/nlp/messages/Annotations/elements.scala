package org.denigma.nlp.messages.Annotations


object Document {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[Document] = PicklerGenerator.generatePickler[Document]

  lazy val empty = Document("", Nil, None)
}
case class Document( id: String = "",
                     sentences: List[Sentence],
                     //coreferenceChains:Option[CorefChains],
                     //discourseTree: Option[DiscourseTree],
                     text: Option[String]) {

  def hasId = id != ""
}

object KBEntry {
  import boopickle.DefaultBasic._

  implicit val pickler: Pickler[KBEntry] = PicklerGenerator.generatePickler[KBEntry]
}

case class KBEntry(id: String, text: String, key: String, namespace: String, species: String)

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
                   )

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

