package com.kontranik.koreader.parser.fb2reader.model;

import java.util.ArrayList;
import java.util.List;

public class FB2Description {
    public FB2TitleInfo titleInfo = new FB2TitleInfo();
    public FB2DocumentInfo documentInfo = new FB2DocumentInfo();
    public FB2PublishInfo publishInfo = new FB2PublishInfo();
    public StringBuffer customInfo = new StringBuffer();
	/**
	 * @return the titleInfo
	 */
	public FB2TitleInfo getTitleInfo() {
		return titleInfo;
	}
	/**
	 * @param titleInfo the titleInfo to set
	 */
	public void setTitleInfo(FB2TitleInfo titleInfo) {
		this.titleInfo = titleInfo;
	}
	/**
	 * @return the documentInfo
	 */
	public FB2DocumentInfo getDocumentInfo() {
		return documentInfo;
	}
	/**
	 * @param documentInfo the documentInfo to set
	 */
	public void setDocumentInfo(FB2DocumentInfo documentInfo) {
		this.documentInfo = documentInfo;
	}
	/**
	 * @return the publishInfo
	 */
	public FB2PublishInfo getPublishInfo() {
		return publishInfo;
	}
	/**
	 * @param publishInfo the publishInfo to set
	 */
	public void setPublishInfo(FB2PublishInfo publishInfo) {
		this.publishInfo = publishInfo;
	}
	/**
	 * @return the customInfo
	 */
	public StringBuffer getCustomInfo() {
		return customInfo;
	}
	/**
	 * @param customInfo the customInfo to set
	 */
	public void setCustomInfo(String customInfo) {
		this.customInfo = new StringBuffer(customInfo);
	}
}
