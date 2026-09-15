package com.mms.minzmahallu.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/** System share-sheet helpers (no SDK, plain ACTION_VIEW intents). */
object ShareUtil {

    fun shareText(ctx: Context, text: String, title: String = "Share") {
        try {
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            ctx.startActivity(Intent.createChooser(i, title))
        } catch (e: Exception) {
            Toast.makeText(ctx, e.message ?: "Share failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareFile(ctx: Context, file: File, mime: String, title: String = "Share") {
        try {
            val uri: Uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)
            val i = Intent(Intent.ACTION_SEND).apply {
                type = mime
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            ctx.startActivity(Intent.createChooser(i, title))
        } catch (e: Exception) {
            Toast.makeText(ctx, e.message ?: "Share failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun viewPdf(ctx: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)
            val i = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            ctx.startActivity(Intent.createChooser(i, "Open PDF"))
        } catch (e: Exception) {
            Toast.makeText(ctx, e.message ?: "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    fun prettyPhone(raw: String): String {
        val d = raw.filter { it.isDigit() }
        if (d.isEmpty()) return ""
        return if (d.length == 10) "${d.take(5)} ${d.drop(5)}" else raw.trim()
    }

    /** Private cache dir for generated PDFs (served through FileProvider). */
    fun cacheFile(ctx: Context, name: String): File {
        val dir = File(ctx.cacheDir, "shared").apply { mkdirs() }
        return File(dir, name.filter { it.isLetterOrDigit() || it in "._-()" })
    }

    fun dial(ctx: Context, phoneRaw: String) {
        val d = phoneRaw.filter { it.isDigit() || it == '+' }
        if (d.isBlank()) {
            Toast.makeText(ctx, "No phone number", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$d")))
        } catch (e: Exception) {
            Toast.makeText(ctx, e.message ?: "Cannot dial", Toast.LENGTH_SHORT).show()
        }
    }
}
