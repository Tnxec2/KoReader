package com.kontranik.koreader.parser.fb2reader

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.kontranik.koreader.parser.fb2reader.model.BinaryData
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme
import com.kontranik.koreader.parser.fb2reader.model.FB2Section
import java.io.*

class FileHelper(private val appDir: String) {

    @Throws(Exception::class)
    fun clearworkdir() {
        val workdir = getworkdir()
        val files = workdir.listFiles()
        for (f in files) f.delete()
    }

    @Throws(Exception::class)
    private fun getworkdir(): File {
        val workdir: File
        workdir = File(appDir, Constant.OUT_DIR)
        workdir.mkdir()
        if (!workdir.exists()) {
            throw Exception("workdir not exist")
        }
        return workdir
    }

    @Throws(Exception::class)
    fun getSectionText(sectionId: Int): String {
        val workdir = getworkdir()
        val text = StringBuilder()
        var line: String?
        val fileSection = File(workdir, Constant.PREFIX_SECTION + sectionId + ".html")
        val reader = BufferedReader(FileReader(fileSection))
        while (reader.readLine().also { line = it } != null) {
            text.append(line)
        }
        reader.close()
        return text.toString()
    }

    @get:Throws(Exception::class)
    val scheme: FB2Scheme
        get() {
            val workdir = getworkdir()
            val fileScheme = File(workdir, Constant.SCHEME_FILENAME)
            var mapper = ObjectMapper()
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            return mapper.readValue(fileScheme, FB2Scheme::class.java)
        }

    @Throws(Exception::class)
    fun writeSchema(scheme: FB2Scheme?) {
        if (scheme == null) return
        val workdir = getworkdir()
        val fileScheme = File(workdir, Constant.SCHEME_FILENAME)
        try {
            var mapper = ObjectMapper()
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            mapper.writeValue(fileScheme, scheme)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun getBinary(sourceName: String): BinaryData {
        var sourceName = sourceName
        if (sourceName.startsWith("#")) sourceName = sourceName.substring(1)
        val workdir = getworkdir()
        val fileBin = File(workdir, Constant.PREFIX_BINARY + sourceName + ".json")
        var mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        return mapper.readValue(fileBin, BinaryData::class.java)
    }

    @Throws(Exception::class)
    fun writeBinary(data: BinaryData?) {
        if (data == null) return
        val workdir = getworkdir()
        val fileBin = File(workdir, Constant.PREFIX_BINARY + data.name + ".json")
        var mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        mapper.writeValue(fileBin, data)
        // System.out.println(fileBin.getAbsolutePath());
    }

    @Throws(Exception::class)
    fun writeSection(mySection: FB2Section?, fB2Scheme: FB2Scheme) {
        if (mySection == null) return
        val workdir = getworkdir()
        val fileSection = File(workdir, Constant.PREFIX_SECTION + mySection.orderid + ".html")
        val writer = BufferedWriter(FileWriter(fileSection))
        writer.write(Constant.HTML_VORSPAN)
        writer.append(mySection.text.toString())
        writer.append(Constant.HTML_NACHSPAN)
        writer.close()
        fB2Scheme.sections[mySection.orderid].textsize = FB2Helper.getSizeOfHtmlText(mySection.text.toString())
        mySection.text = StringBuffer()
    }
}