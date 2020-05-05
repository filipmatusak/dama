import com.thoughtworks.binding.Binding

package object client {
  implicit def makeIntellijHappy(x: scala.xml.Node): Binding[org.scalajs.dom.raw.Node] = ???

}
