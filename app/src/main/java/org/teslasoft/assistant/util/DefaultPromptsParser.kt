package org.teslasoft.assistant.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import java.util.Locale

class DefaultPromptsParser {
    private var explanationPrompt = HashMap<String, String>()
    private var languagesSupported = arrayListOf(
        "en",
        "es",
        "pl",
        "ru",
        "sk",
        "tr",
        "uk"
    )

    private var languageIdentifier: LanguageIdentifier? = null

    fun init() {
        explanationPrompt["en"] = "What does \"%s\" means?"
        explanationPrompt["es"] = "¿Qué significa \"%s\"?"
        explanationPrompt["pl"] = "Co oznacza \"%s\"?"
        explanationPrompt["ru"] = "Что означает \"%s\"?"
        explanationPrompt["sk"] = "Čo znamená \"%s\"?"
        explanationPrompt["tr"] = "\"%s\" ne anlama geliyor?"
        explanationPrompt["uk"] = "Що означає \"%s\"?"
    }

    private var listener: OnCompletedListener? = null

    fun parse(type: String, text: String, context: Context) {
        if (type == "explanationPrompt") {
            languageIdentifier = LanguageIdentification.getClient()
            languageIdentifier?.identifyLanguage(text)
                ?.addOnSuccessListener { languageCode ->
                    val l = if (languageCode == "und") {
                        "en"
                    } else {
                        languageCode
                    }

                    var lng = "en"

                    /* R8 obfuscator fix */
                    if (languagesSupported == null) languagesSupported = arrayListOf(
                        "en",
                        "es",
                        "pl",
                        "ru",
                        "sk",
                        "tr",
                        "uk"
                    )

                    for (lang: String in languagesSupported) {
                        /* R8 obfuscator fix */
                        if (lang != null && lang == l) {
                            lng = lang
                            break
                        }
                    }

                    val d = String.format(explanationPrompt.getValue(lng).toString(), text)

                    listener!!.onCompleted(d)
                }?.addOnFailureListener {
                    listener!!.onCompleted(String.format(explanationPrompt.getValue("en"), text))
                }
        } else {
            throw IllegalArgumentException("Unsupported prompt type at org.teslasoft.assistant.util.DefaultPromptsParser.kt")
        }
    }

    fun addOnCompletedListener(listener: OnCompletedListener) {
        this.listener = listener
    }

    fun interface OnCompletedListener {
        fun onCompleted(text: String)
    }
}