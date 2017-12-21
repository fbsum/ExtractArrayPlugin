package com.fbsum.plugin.extractarray

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor


class Utils {

    private val log = Logger.getInstance(Utils::class.java)

    companion object {
        fun apply(editor: Editor): List<String> {

            val selectionModel = editor.selectionModel
            val selectedTexts = selectionModel.selectedText?.split("\n")

            val names = ArrayList<String>()
            var isString = true
            selectedTexts?.forEach {
                val text = it.trim()
                if (text.startsWith("<color name=\"") && text.endsWith("</color>")) {
                    val end = text.indexOf("\">")
                    if (end > 12) {
                        val name = text.substring(13, end)
                        names.add(name)
                    }
                    isString = false
                } else if (text.startsWith("<string name=\"") && text.endsWith("</string>")) {
                    val end = text.indexOf("\">")
                    if (end > 13) {
                        val name = text.substring(14, end)
                        names.add(name)
                    }
                    isString = true
                }
            }

            val results = ArrayList<String>()
            names.forEach {
                if (isString) {
                    results.add("<item>@string/$it</item>")
                } else {
                    results.add("<item>@color/$it</item>")
                }
            }

            return results
        }
    }
}