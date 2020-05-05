package client

import com.thoughtworks.binding.dom
import org.scalajs.dom.document

import scala.scalajs.js.JSApp

object MainApp extends JSApp {

  val mainColor = "light-blue"

  def main(): Unit = {
    val game = Game.create()
    dom.render(document.getElementById("mainContainer"), game.render())
  }
}

