package org.denigma.nlp.messages.Annotations


case class Rule(
            name: String,
            labels: Seq[String],
            ruleType: String,
            unit: String,
            priority: String,
            keep: Boolean,
            action: String,
            pattern: String
          )