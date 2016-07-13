package org.denigma.nlp.annotator

import org.denigma.binding.binders.{Binder, TemplateBinder, BinderForViews}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{BasicView, BindableView}
import org.scalajs.dom
import org.scalajs.dom.raw.Element

/*
class BetterCollectionView extends BindableView {
  type Item
  type ItemView <: BasicView

  override protected lazy val defaultBinders: List[ViewBinder] = List(new BinderForViews[this.type](this), new TemplateBinder[this.type](this))

  /**
    * extracts element after which it inserts children
    *
    * @return
    */
  def extractStart(): Element = {
    val id = "items_of_"+viewElement.id
    sq.byId(id) match {
      case Some(el)=>el
      case None=>
        val sp = dom.document.createElement("span")
        sp.id = id
        if(template==viewElement) {
          viewElement.appendChild(sp)
          dom.console.error(s"items are the same as $id")
        }
        else {
          //dom.console.log(template.outerHTML)
          this.replace(sp,template)
        }
        template.dyn.style.display = "none"
        sp
    }
  }

}

class BetterBinder[View <: BetterCollectionView](view:View) extends Binder{
  override def bindAttributes(el: Element, attributes: Predef.Map[String, String]): Boolean = {
    if(attributes.contains("data-template"))
    {
      el.removeAttribute("data-template")
      view.template = el
      false
    } else true
  }
}
*/