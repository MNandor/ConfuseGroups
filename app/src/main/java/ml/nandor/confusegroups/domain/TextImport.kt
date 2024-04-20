package ml.nandor.confusegroups.domain

import ml.nandor.confusegroups.domain.model.AtomicNote

object TextImport {
    fun textToQAPair(text: String): Pair<String, String>?{

        // These are considered Group headers. Skip them.
        if (text.startsWith("#")) return null

        val qa = if (text.contains("--")) text.split("--")
        else if (text.contains("-")) text.split("-")
        else return null

        if (qa.size != 2)
            return null

        return Pair(qa[0].trim(), qa[1].trim())
    }

    fun noteToExportableText(atomicNote: AtomicNote): String {
        return "${atomicNote.question}--${atomicNote.answer}"
    }
}