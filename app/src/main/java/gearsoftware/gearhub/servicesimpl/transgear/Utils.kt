package gearsoftware.gearhub.servicesimpl.transgear

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import java.io.File

/**
 * Some utilities for transgear filesystem
 */

internal object Utils {

    fun parseFileIntent(context: Context, intent: Intent): List<File> =
            when (intent.action) {
                Intent.ACTION_SEND -> parseUriToFilename(context, intent.getParcelableExtra(Intent.EXTRA_STREAM)!!, null)
                        ?.let { listOf(File(it)) }
                        ?: emptyList()

                Intent.ACTION_SEND_MULTIPLE ->
                    intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
                            ?.map { parseUriToFilename(context, it as Uri, null) }
                            ?.mapNotNull { File(it!!) }
                            ?: emptyList()
                else -> emptyList()
            }

    private fun parseUriToFilename(context: Context, uri: Uri, type: String?): String? {
        val proj: Array<String>
        var cursor: Cursor? = null
        val columnIndex: Int
        val f = File(uri.path!!)
        var res: String? = null

        if (f.exists()) {
            return uri.path
        }

        try {
            proj = type?.let {
                when {
                    type.startsWith("image") -> arrayOf(MediaStore.Images.Media.DATA)
                    type.startsWith("audio") -> arrayOf(MediaStore.Audio.Media.DATA)
                    type.startsWith("video") -> arrayOf(MediaStore.Video.Media.DATA)
                    else -> arrayOf(MediaStore.Files.FileColumns.DATA)
                }
            } ?: arrayOf(MediaStore.Files.FileColumns.DATA)


            cursor = context.contentResolver.query(uri, proj, null, null, null)

            if (cursor != null) {
                columnIndex = if (type != null && type.startsWith("image")) {
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                } else if (type != null && type.startsWith("audio")) {
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                } else if (type != null && type.startsWith("video")) {
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                } else {
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                }

                cursor.moveToFirst()
                res = cursor.getString(columnIndex)
            }


            if (res == null) {
                cursor = context.contentResolver.query(uri, null, null, null, null)
                return if (cursor != null && cursor.moveToFirst()) {
                    ""
                    //cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                } else null
            }
            return res
        } finally {
            cursor?.close()
        }
    }
}
