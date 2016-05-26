package org.denigma.nlp.annotator

import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.denigma.controls.code.CodeBinder
import org.denigma.nlp.messages.Annotations
import org.denigma.nlp.messages.MessagesNLP.DocumentAnnotations
import org.scalajs.dom.Element
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

class AnnotationsView(val elem: Element, annotations: Var[DocumentAnnotations]) extends ItemsSeqView {
  type Item = Annotations.Mention
  type ItemView = MentionView
  val items: Rx[List[Annotations.Mention]] = annotations.map(an=>an.mentions)

  override def newItemView(item: Annotations.Mention): MentionView = constructItemView(item){
    case (el, _)=> new MentionView(el, item).withBinder(b=>new CodeBinder(b))
  }
}

class MentionView(val elem: Element, mention: Annotations.Mention) extends BindableView
{
  val code = Var(str(mention))


  def str(men: Annotations.Mention) = mention match {
    case mention: Annotations.CorefEventMention =>
      "labels: "+mention.labels+"<br>"+
      "foundby: "+mention.foundBy+"<br>"+
      "ground:"+mention.bestPick.getOrElse("")+"<br>"+
      "context:"+mention.context+"<br>"+
      "arguments: "+mention.arguments+"<br>"+
      "modifications: "+mention.modifications+"<br>"+
      "triggered by: "+mention.trigger+"<br>"+
      "intervals: "+mention.tokenInterval+"<br>"


    case mention :Annotations.BioMention =>
      "labels: "+mention.labels+"<br>"+
      "foundby: "+mention.foundBy+"<br>"+
      "ground:"+mention.bestPick.getOrElse("")+"<br>"+
      "context:"+mention.context+"<br>"+
      "arguments: "+mention.arguments+"<br>"+
      "modifications: "+mention.modifications+"<br>"+
      "intervals: "+mention.tokenInterval+"<br>"



    case mention =>
      "labels: "+mention.labels+"<br>"+
      "foundby: "+mention.foundBy+"<br>"+
      "arguments: "+mention.arguments+"<br>"+
      "intervals: "+mention.tokenInterval+"<br>"



  }
}