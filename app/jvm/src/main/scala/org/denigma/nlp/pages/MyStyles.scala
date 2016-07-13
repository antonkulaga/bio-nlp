package org.denigma.nlp.pages

import org.denigma.controls.papers.{MediaQueries, TextLayerStyles}

import scalacss.Defaults._
import scalacss.LengthUnit.vh

object MyStyles extends TextLayerStyles with MediaQueries{
  import dsl._

  ".attached.tab.segment" -(
    overflowY.auto
    )

  ".ui.column" -(
    overflowY.auto  important,
    padding(0 px)  important
    )
  "#annotations" -(
    overflowY.visible,
    minHeight(80 vh)
    )

  "#debug" - (
    maxHeight(30 vh)
    )

  "#text" -(
    minHeight(15 em)
    )


  /*
  val totalWidth = 3200.0 px

  "#main" -(
    overflowX.auto,
    overflowY.hidden,
    maxHeight(98 vh)
    )
  ".graph" -(
    borderColor(blue),
    borderWidth(3 px)
    )


  ".CodeMirror" -(
    height.auto important,
    minHeight(15.0 vh),
    maxHeight(100 %%),
    width(100 %%)
    //height(100.0 %%) important
    // width.auto important
    )

  ".CodeMirror-scroll" -(
    overflow.visible,
    height.auto
    )//-(overflowX.auto,overflowY.hidden)

  ".breakpoints" - (
    width( 3 em)
    )

  ".focused" - (
    backgroundColor.ghostwhite
    )

  "#Papers" -(
    padding(0 px)
    )

  ".ui.segment.paper" -(
    padding(0 px),
    minHeight(98.0 vh)
    )

  ".tab.page" -(
      //overflowY.scroll,
      //overflowX.scroll,
      minHeight(98.0 vh)
    )

  ".tab.flexible.page" -(
    overflowY.auto,
    overflowX.auto,
    height(100.0 %%)
    )

  "#LeftGraph" -(
    padding(0 px)
    )

  "#RightGraph" -(
    padding(0 px)
    )

  ".project.content" -{
    cursor.pointer
  }
  */

  "html"-(
    onTiny -fontSize(8 pt),
    onLittle -fontSize(9 pt),
    onSmall -fontSize(10 pt),
    onMedium -fontSize(11 pt),
    onLarge -fontSize(12 pt)
    )

}