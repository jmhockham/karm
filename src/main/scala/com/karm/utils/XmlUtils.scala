package com.karm.utils

import scala.xml._

object XmlUtils {

  implicit class NodeWrapper(node: Node) {

    def singleChildWithLabel(element: String): Node = {
      (node \ element).seq match {
        case Seq(single) => single
        case Nil         => throw new IllegalArgumentException(s"No child with label '$element'")
        case _           => throw new IllegalArgumentException(s"Multiple children with label '$element'")
      }
    }

    def firstChildWithLabel(element: String): Option[Node] = (node \ element).headOption

    def singleElemChildWithLabel(element: String): Elem = singleChildWithLabel(element).asElem

    def asElem: Elem = node match {
      case elem: Elem => elem
      case _          => throw new IllegalArgumentException(s"Expected '${node.label}' to be an Elem; found: ${node.getClass.getSimpleName}")
    }
  }

  implicit class NodeSeqWrapper(nodes: Seq[Node]) extends Logger {

    def interpolate(element: Node): Seq[Node] = nodes match {
      case head +: tail if tail.nonEmpty => head ++ element ++ tail.interpolate(element)
      case other                         => other
    }

    def children: Seq[Node] = nodes.flatMap(_.child)

    // Use this when you are making the assumption that all children are of the same type.
    def \!(that: String): Seq[Node] = {
      if (!nodes.children.forall(_.label == that)) {
        log.warn(s"Expected all children to have label '$that' for ${nodes.mkString}")
      }
      nodes \ that
    }
  }

  implicit class ElemWrapper(elem: Elem) {
    def addAttributeIfPresentOnOther(name: String, otherNode: Node): Node = {
      otherNode.attribute(name) match {
        case Some(valueNodes) =>
          elem % Attribute(None, name, valueNodes, Null)
        case None =>
          elem
      }
    }
  }

  def recurseFindAndReplaceText(e: Node, toReplace: String, replacement: => String): Node = e match {
    case Text(text)    => new Text(text.replaceAll(toReplace, replacement))
    case nonText: Elem => nonText.copy(child = nonText.child.map(x => recurseFindAndReplaceText(x, toReplace, replacement)))
    case other         => other
  }

  def removeChildWithLabel(node: Node, labelToRemove: String): NodeSeq = node match {
    case _ if node.label == labelToRemove => Nil
    case elem: Elem                       => elem.copy(child = elem.child.flatMap(removeChildWithLabel(_, labelToRemove)))
    case atom                             => atom
  }

  def trimEnds(node: Node): Node = leftTrim(rightTrim(node))

  private def leftTrim(node: Node): Node = node match {
    case Text(string) => Text(string.replaceAll("^\\s+", ""))
    case elem: Elem => elem.child match {
      case head +: tail => elem.copy(child = leftTrim(head) +: tail)
      case _            => elem
    }
    case _ => node
  }

  private def rightTrim(node: Node): Node = node match {
    case Text(string) => Text(string.replaceAll("\\s+$", ""))
    case elem: Elem => elem.child match {
      case init :+ last => elem.copy(child = init :+ rightTrim(last))
      case _            => elem
    }
    case _ => node
  }

  implicit class ElemExtras(elem: Elem) {
    /**
      * This method takes multiple tuples of (String, String), and adds them as attributes to an Elem
      * @param tups tuple of strings, where the LHS is the attribute key, and the RHS is the value
      * @return Elem
      */
    def addAttributes(tups: (String, String)*): Elem = tups.foldLeft(elem) {
      (acc, tup) => acc % Attribute(None, tup._1, Text(tup._2), xml.Null)
    }
  }

  implicit class NodeExtras(node: Node) {
    def toElem(): Elem = node.asInstanceOf[Elem]
  }

  def addChild(parent: Node, newChild: Node): Node = parent match {
    case e: Elem => e.copy(child = e.child ++ newChild)
  }

  def addChildren(parent: Node, newChildren: Seq[Node]): Node = parent match {
    case e: Elem => e.copy(child = e.child ++ newChildren)
  }

}

