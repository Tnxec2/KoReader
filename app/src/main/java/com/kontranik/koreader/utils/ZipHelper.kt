package com.kontranik.koreader.utils

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipHelper {

    @Throws(IOException::class)
    fun unzip(zipFile: File?, targetDirectory: File?) {
        val zis = ZipInputStream(
                BufferedInputStream(FileInputStream(zipFile)))
        try {
            var ze: ZipEntry
            var count: Int
            val buffer = ByteArray(8192)
            while (zis.nextEntry.also { ze = it } != null) {
                val file = File(targetDirectory, ze.name)
                val dir = if (ze.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException("Failed to ensure directory: " +
                        dir.absolutePath)
                if (ze.isDirectory) continue
                val fout = FileOutputStream(file)
                try {
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                } finally {
                    fout.close()
                }
                /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close()
        }
    }
}