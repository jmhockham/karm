package com.karm.model

import scala.xml.Node
import scala.xml.transform.{ BasicTransformer, RewriteRule, RuleTransformer }

/**
  * Trait which provides a more efficient transform method for Scala XML
  * transforms.
  *
  * This version is already available in scala-xml 1.0.6; however, due to issue
  * https://github.com/scala/scala-module-dependency-sample/pull/14, it is impossible to run
  * scala-xml 1.0.6 with Scala version 2.11.8.
  */
trait EfficientTransform {
  this: BasicTransformer =>

  /**
    * This is a copy of the version of this method in scala-xml 1.0.6.
    */
  override def transform(ns: Seq[Node]): Seq[Node] = {
    val changed = ns flatMap transform
    if (changed.length != ns.length || (changed, ns).zipped.exists(_ != _)) changed
    else ns
  }
}

abstract class EfficientRewriteRule extends RewriteRule with EfficientTransform
class EfficientRuleTransformer(rules: RewriteRule*) extends RuleTransformer(rules: _*) with EfficientTransform

