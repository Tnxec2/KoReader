package com.kontranik.koreader.parser.fb2reader.model;

import com.kontranik.koreader.parser.fb2reader.FileHelper;

public class FB2ParserObject {

    public FileHelper fileHelper;

    public static final Integer maxHeader = 5; // 0 bis 5
    public static final String  SectionReachedException = "SectionReachedException";

    public FB2Scheme fb2scheme  = new FB2Scheme();

    public FB2Section mySection;
    public Integer sectionid;
    public Integer sectionDeep;
    
    public BinaryData myBinaryData;

    public boolean myParseText;
    public StringBuffer myText;

    public FB2Elements lastElement;
    
    public boolean isDescription;
    public boolean isDescriptionCustomInfo;
    public boolean isDescriptionPublishInfo;
    public boolean isDescriptionDocumentInfo;
    public boolean isDescriptionTitleInfo;
    
    public boolean isCoverpage;
    public boolean isAnnotation;
    public boolean isAuthor;
    public boolean isTranslator;
    public boolean isHistory;
    
    public boolean isSection;
    public boolean isNotes;
	public boolean onlyscheme;

	public boolean isSupNote;

    public void clearMyText() {
        myText = new StringBuffer();
        myParseText = false;
    }

	public void clear() {

        mySection = null;
       sectionid = 0;
       sectionDeep = 0;

       myParseText = false;
       myText = new StringBuffer();
        
       isDescription = false;
       isDescriptionCustomInfo = false;
       isDescriptionPublishInfo = false;
       isDescriptionDocumentInfo = false;
       isDescriptionTitleInfo = false;
        
       isCoverpage = false;
       isAnnotation = false;
       isAuthor = false;
       isTranslator = false;
       isHistory = false;
        
       isSection = false;
       isNotes = false;
	}
}
