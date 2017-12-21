package com.fbsum.plugin.extractarray

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.WindowConstants

class ExtractArrayAction : AnAction() {

    private val log = Logger.getInstance(ExtractArrayAction::class.java)

    companion object {
        private val VALID_FILE_NAMES = mutableListOf("strings.xml", "colors.xml")
    }

    /**
     * 更新：判断插件是否显示
     */
    override fun update(event: AnActionEvent) {
        super.update(event)
        event.presentation.isVisible = false
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return
        val virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE) ?: return
        event.presentation.isVisible = isValidFile(virtualFile) && editor.selectionModel.hasSelection()
    }

    /**
     * 判断当前显示的文件是否为 VALID_FILE_NAMES 所包含的文件
     */
    private fun isValidFile(file: VirtualFile): Boolean {
        return VALID_FILE_NAMES.contains(file.name)
    }

    /**
     * 执行插件
     */
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return
        object : WriteCommandAction.Simple<Any>(project) {
            @Throws(Throwable::class)
            override fun run() {
                val results = Utils.apply(editor)
                if (!results.isEmpty()) {
                    showFrame(results)
                }
            }
        }.execute()
    }

    private fun showFrame(results: List<String>) {
        val textArea = JTextArea()
        results.forEach {
            textArea.append(it)
            textArea.append("\n")
        }

        val panel = JBScrollPane(textArea)
        panel.preferredSize = Dimension(640, 360)
        panel.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED

        // frame
        val frame = JFrame()
        frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        frame.contentPane.add(panel)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

}
