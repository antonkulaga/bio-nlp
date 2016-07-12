package org.denigma.nlp.pages

import org.denigma.controls.papers.{TextLayerStyles, MediaQueries}

import scalacss.Defaults._

object MyStyles extends TextLayerStyles with MediaQueries{
  import dsl._

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

  ".attached.tab.segment" -(
      overflowY.auto
    )

  ".ui.column" -(
    overflowY.auto  important,
    padding(0 px)  important
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
}