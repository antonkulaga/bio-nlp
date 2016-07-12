package org.denigma.nlp.pages

import akka.http.extensions.pjax.PJax
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import org.denigma.controls.Twirl
import play.twirl.api.Html

class Pages extends Directives with PJax{

  def defaultPage: Option[Html] = {
    Some( html.demo())
  }

  def index: Route =  pathSingleSlash{ ctx=>
    ctx.complete {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`.withCharset(HttpCharsets.`UTF-8`), html.index(defaultPage).body  ))
    }
  }

  def brat: Route = pathPrefix("brat" ~ Slash) { ctx=>
    ctx.complete {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`.withCharset(HttpCharsets.`UTF-8`), html.demo().body  ))
    }
  }

  val loadPage: Html => Html = h => html.index(Some(h))


  def test: Route = pathPrefix("test" ~ Slash) { ctx=>
      pjax[Twirl](Html(s"<h1>${ctx.unmatchedPath}</h1>"),loadPage){h=>c=>
        val resp = HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`.withCharset(HttpCharsets.`UTF-8`), h.body  ))
        c.complete(resp)
      }(ctx)
    }


  def routes: Route = index ~ test ~ brat


}