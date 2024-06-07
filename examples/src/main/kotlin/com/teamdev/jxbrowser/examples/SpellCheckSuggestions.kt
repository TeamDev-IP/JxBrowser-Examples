/*
 *  Copyright 2024, TeamDev. All rights reserved.
 *
 *  Redistribution and use in source and/or binary forms, with or without
 *  modification, must retain the above copyright notice and the following
 *  disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.teamdev.jxbrowser.examples

import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback.Action
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.dsl.spellChecker
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.menu.SpellCheckMenu

/**
 * This example demonstrates how to configure spell checking functionality
 * with the required language and display the dictionary suggestions in
 * the context menu that is displayed under a misspelled word.
 */
fun main() = singleWindowApplication(title = "Spell check suggestions") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }

    // Store dropdown menu state.
    var spellCheckMenu by remember { mutableStateOf<SpellCheckMenu?>(null) }
    var spellCheckTell by remember { mutableStateOf<Action?>(null) }

    BrowserView(browser)

    if (spellCheckMenu != null) {
        CursorDropdownMenu(
            expanded = true,
            onDismissRequest = {
                spellCheckMenu = null
                spellCheckTell!!.close()
            }
        ) {
            val spellMenu = spellCheckMenu!!
            val suggestions = spellMenu.dictionarySuggestions()
            suggestions.forEach {
                DropdownMenuItem(onClick = {
                    browser.replaceMisspelledWord(it)
                    spellCheckTell!!.close()
                    spellCheckMenu = null
                }) {
                    Text(it)
                }
            }
            if (suggestions.isNotEmpty()) {
                Divider()
            }
            DropdownMenuItem(onClick = {
                val misspelledWord = spellMenu.misspelledWord()
                engine.spellChecker.customDictionary().add(misspelledWord)
                spellCheckTell!!.close()
                spellCheckMenu = null
            }) {
                Text(spellMenu.addToDictionaryMenuItemText())
            }
        }
    }

    LaunchedEffect(Unit) {
        browser.register(ShowContextMenuCallback { params, tell ->
            if (params.spellCheckMenu().misspelledWord().isNotEmpty()) {
                spellCheckTell = tell
                spellCheckMenu = params.spellCheckMenu()
            }
        })
        browser.mainFrame?.loadHtml(MISSPELLED_TEXT)
    }
}

private val MISSPELLED_TEXT = """
    <html lang="en">
    <body>
    <textarea autofocus cols='30' rows='20'>Smple text with mitake.</textarea>
    </body>
    </html>
""".trimIndent()
