package ml.nandor.exportDatabaseFile

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File

object ExportDatabaseFile {

    fun test(): String{
        return "Hello"
    }

    fun getExportableDatabases(context: Context, searchString:String = ""): List<File> {
        val baseDir = context.filesDir
        val dbDir = baseDir.parentFile?.listFiles { it -> it.name == "databases" }?.firstOrNull()

        val options = dbDir?.listFiles()?.filter {
            // write-ahead log - not an actual database
            if (it.name.endsWith("-wal")) return@filter false

            // memory-mapped file - not an actual database
            if (it.name.endsWith("-shm")) return@filter false

            // if a search term is set, filter by it
            if (!it.name.lowercase().contains(searchString.lowercase())) return@filter false

            return@filter true
        } ?: listOf()

        return options
    }

    fun exportDBToStorage(context: Context, searchString: String = "", fileName:String?=null): String?{
        val options = getExportableDatabases(context, searchString)

        if (options.size != 1) return null

        val dbFile = options[0]

        val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val targetFileName = "${context.applicationContext.applicationInfo.name}-${System.currentTimeMillis()/1000}.db"

        val exportedFileName = File(targetDir.absolutePath + "/exported-$targetFileName")

        dbFile.copyTo(exportedFileName)

        return targetFileName

    }
}