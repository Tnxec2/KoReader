package com.kontranik.koreader.parser.fb2reader;

import com.kontranik.koreader.parser.fb2reader.model.BinaryData;
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme;
import com.kontranik.koreader.parser.fb2reader.model.FB2Section;

import java.io.InputStream;

public class FB2Reader {
    private String appDir;
    private String contentUri;
    private FB2Scheme fb2Scheme;

    FB2Reader(String appDir, String uri) {
    	this.appDir = appDir;
        this.contentUri = uri;
		java.util.logging.Logger.getLogger("FB2READER").log(java.util.logging.Level.INFO, appDir);
	}

	public FB2Scheme readBook(String contentUri, InputStream fileInputStream) {

		if ( contentUri != null ) {
			try {
				FB2Scheme schemeFile = new FileHelper(appDir).getScheme();
				if ( schemeFile != null && contentUri.equals(schemeFile.path) ) {
					this.fb2Scheme = schemeFile;
					return schemeFile;
				}
			} catch ( Exception e) {
				e.printStackTrace();
				java.util.logging.Logger.getLogger("FB2READER").log(java.util.logging.Level.INFO, e.getMessage());
			}

		}
		try {
			this.fb2Scheme = new FB2Parser(appDir, contentUri, fileInputStream).parseBook();
		} catch (Exception e) {
			e.printStackTrace();
			java.util.logging.Logger.getLogger("FB2READER").log(java.util.logging.Level.INFO, e.getMessage());
			return null;
		}
		return fb2Scheme;
	}

	public FB2Scheme readScheme(InputStream fileInputStream) {
		try {
			this.fb2Scheme = new FB2Parser(appDir, contentUri, fileInputStream).parseScheme();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fb2Scheme;
	}

	public String getCoverPage() {
		if ( fb2Scheme.description.titleInfo.coverpage == null ) return null;
		return fb2Scheme.description.titleInfo.coverpage.toString();
	}

	public byte[] getCover() {
		String coverSrc = fb2Scheme.description.titleInfo.coverImageSrc;
		if ( coverSrc != null ) {
			if ( coverSrc.startsWith("#")) coverSrc = coverSrc.substring(1);
			try {
				BinaryData binaryData = new FileHelper(appDir).getBinary(coverSrc);
				return binaryData.getContentsArray();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

    public String getSectionHtml(int orderId) {
        if ( orderId < 0 || orderId > fb2Scheme.sections.size()) return null;

        try {
			return new FileHelper(appDir).getSectionText(orderId);
		} catch (Exception e) {
			return null;
		}
    }

	public String getSectionHtml(String name) {
		if ( name.startsWith("#")) name = name.substring(1);
		FB2Section s = fb2Scheme.getSection(name);
		if ( s != null) {
			return getSectionHtml(s.getOrderid());
		} else {
			return null;
		}
	}

	public byte[] getBinary(String name) {
		if ( name != null ) {
			if ( name.startsWith("#")) name = name.substring(1);
			try {
				BinaryData binaryData = new FileHelper(appDir).getBinary(name);
				return binaryData.getContentsArray();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * @return the uri
	 */
	public String getContentUri() {
		return contentUri;
	}

	/**
	 * @return the fb2Scheme
	 */
	public FB2Scheme getFb2Scheme() {
		return fb2Scheme;
	}

}
