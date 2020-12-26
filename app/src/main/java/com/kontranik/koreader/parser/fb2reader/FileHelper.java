package com.kontranik.koreader.parser.fb2reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kontranik.koreader.parser.fb2reader.model.BinaryData;
import com.kontranik.koreader.parser.fb2reader.model.FB2Description;
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme;
import com.kontranik.koreader.parser.fb2reader.model.FB2Section;

public class FileHelper {
    private String appDir;
    ObjectMapper mapper = new ObjectMapper();

    public FileHelper(String appDir) {
        this.appDir = appDir;
    }

    public void clearworkdir() throws Exception {
        File workdir = getworkdir();
        final File[] files = workdir.listFiles();
        for (File f: files) f.delete();
    }

    private File getworkdir() throws Exception {
        File workdir;
        workdir = new File(this.appDir, Constant.OUT_DIR);
        workdir.mkdir();
        if ( ! workdir.exists() ) {
            throw new Exception("workdir not exist");
        }
        return workdir;
    }

    public String getSectionText(Integer sectionId) throws Exception {
        File workdir = getworkdir();
        StringBuilder text = new StringBuilder();
        String line;
        File fileSection = new File(workdir, Constant.PREFIX_SECTION + sectionId + ".html");
        BufferedReader reader = new BufferedReader(new FileReader(fileSection));
        while ((line = reader.readLine()) != null) {
            text.append(line);
          }
        reader.close();
        return text.toString();
    }

    public FB2Scheme getScheme() throws Exception {
        File workdir = getworkdir();
        File fileScheme = new File(workdir, Constant.SCHEME_FILENAME);
        return mapper.readValue(fileScheme, FB2Scheme.class);
    }

    public void writeSchema(FB2Scheme scheme) throws Exception {
        if ( scheme == null) return;
        File workdir = getworkdir();
        File fileScheme = new File(workdir, Constant.SCHEME_FILENAME);
        try {
            mapper.writeValue(fileScheme, scheme);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BinaryData getBinary(String sourceName) throws Exception {
        if ( sourceName.startsWith("#")) sourceName = sourceName.substring(1);
        File workdir = getworkdir();
        File fileBin = new File(workdir, Constant.PREFIX_BINARY + sourceName + ".json");
        return mapper.readValue(fileBin, BinaryData.class);
    }

    public void writeBinary(BinaryData data) throws Exception {
        if ( data == null) return;
        File workdir = getworkdir();
        File fileBin = new File(workdir, Constant.PREFIX_BINARY + data.getName() + ".json");
        mapper.writeValue(fileBin, data);
        // System.out.println(fileBin.getAbsolutePath());
    }

	public void writeSection(FB2Section mySection) throws Exception {
        if ( mySection == null) return;

        File workdir = getworkdir();
        File fileSection = new File(workdir, Constant.PREFIX_SECTION + mySection.orderid + ".html");
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileSection));
        writer.write(Constant.HTML_VORSPAN);
        writer.append(mySection.text.toString());
        writer.append(Constant.HTML_NACHSPAN);
        writer.close();
        mySection.text = new StringBuffer();
	}
}
