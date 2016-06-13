package org.denigma.nlp.pubmed
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.Document

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import fastparse.all._

trait BasicParser {
  protected val optSpaces = P(" ".rep)
  protected val spaces = P(" ".rep(min = 1))
  protected val digit = P(CharIn('0'to'9'))
  protected val letter = P(CharIn('A' to 'Z') | CharIn('a' to 'z'))

}

/**
  * Created by antonkulaga on 24/03/16.
  */
class PubmedParser extends BasicParser {



  //val pmid = P(("PMC" | ") ~ digit.rep(min =1).! ~ P("/")~AnyChar.?.rep(min =0))

}

object Pubmed {

  def pmid(url: String) = {
    url.toLowerCase.indexOf("PMC") match {
      case -1=> None
      case ind => url.substring(ind)
    }
  }

  val base = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/"

  /*

  <eSummaryResult>
    <DocSum>
      <Id>4803388</Id>
      <Item Name="PubDate" Type="Date">1973 May</Item>
      <Item Name="EPubDate" Type="Date"/>
      <Item Name="Source" Type="String">Semin Psychiatry</Item>
      <Item Name="AuthorList" Type="List">
        <Item Name="Author" Type="String">Framo JL</Item>
      </Item>
      <Item Name="LastAuthor" Type="String">Framo JL</Item>
      <Item Name="Title" Type="String">Marriage therapy in a couples group.</Item>
      <Item Name="Volume" Type="String">5</Item>
      <Item Name="Issue" Type="String">2</Item>
      <Item Name="Pages" Type="String">207-17</Item>
      <Item Name="LangList" Type="List">
        <Item Name="Lang" Type="String">English</Item>
      </Item>
      <Item Name="NlmUniqueID" Type="String">0235734</Item>
      <Item Name="ISSN" Type="String">0037-1971</Item>
      <Item Name="ESSN" Type="String"/>
      <Item Name="PubTypeList" Type="List">
        <Item Name="PubType" Type="String">Journal Article</Item>
      </Item>
      <Item Name="RecordStatus" Type="String">PubMed - indexed for MEDLINE</Item>
      <Item Name="PubStatus" Type="String">ppublish</Item>
      <Item Name="ArticleIds" Type="List">
        <Item Name="pubmed" Type="String">4803388</Item>
        <Item Name="eid" Type="String">4803388</Item>
        <Item Name="rid" Type="String">4803388</Item>
      </Item>
      <Item Name="History" Type="List">
        <Item Name="pubmed" Type="Date">1973/05/01 00:00</Item>
        <Item Name="medline" Type="Date">1973/05/01 00:01</Item>
        <Item Name="entrez" Type="Date">1973/05/01 00:00</Item>
      </Item>
      <Item Name="References" Type="List"/>
      <Item Name="HasAbstract" Type="Integer">0</Item>
      <Item Name="PmcRefCount" Type="Integer">0</Item>
      <Item Name="FullJournalName" Type="String">Seminars in psychiatry</Item>
      <Item Name="ELocationID" Type="String"/>
      <Item Name="SO" Type="String">1973 May;5(2):207-17</Item>
    </DocSum>
    <DocSum>
    */
  def request(url: String): Future[Document] = {
    Ajax.get(url).map(r=>r.responseXML)
  }
}

