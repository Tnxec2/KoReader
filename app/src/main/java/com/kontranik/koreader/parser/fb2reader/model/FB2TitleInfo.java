package com.kontranik.koreader.parser.fb2reader.model;

import java.util.ArrayList;
import java.util.List;

public class FB2TitleInfo {
    public List<String> genre = new ArrayList<>();
    public List<Author> authors = new ArrayList<>();
    public String booktitle;
    public StringBuffer annotation = new StringBuffer();
    public List<String> keywords = new ArrayList<>();
    public String date;
    public StringBuffer coverpage = new StringBuffer();
    public String coverImageSrc;
    public String lang;
    public String srclang;
    public List<Author> translators = new ArrayList<>();
    public String sequenceName;
    public String sequenceNumber;
	/**
	 * @return the genre
	 */
	public List<String> getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(List<String> genre) {
		this.genre = genre;
	}
	/**
	 * @return the authors
	 */
	public List<Author> getAuthors() {
		return authors;
	}
	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	/**
	 * @return the booktitle
	 */
	public String getBooktitle() {
		return booktitle;
	}
	/**
	 * @param booktitle the booktitle to set
	 */
	public void setBooktitle(String booktitle) {
		this.booktitle = booktitle;
	}
	/**
	 * @return the annotation
	 */
	public StringBuffer getAnnotation() {
		return annotation;
	}
	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(StringBuffer annotation) {
		this.annotation = annotation;
	}
	/**
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return keywords;
	}
	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the coverpage
	 */
	public StringBuffer getCoverpage() {
		return coverpage;
	}
	/**
	 * @param coverpage the coverpage to set
	 */
	public void setCoverpage(StringBuffer coverpage) {
		this.coverpage = coverpage;
	}
	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}
	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	/**
	 * @return the srclang
	 */
	public String getSrclang() {
		return srclang;
	}
	/**
	 * @param srclang the srclang to set
	 */
	public void setSrclang(String srclang) {
		this.srclang = srclang;
	}
	/**
	 * @return the translators
	 */
	public List<Author> getTranslators() {
		return translators;
	}
	/**
	 * @param translators the translators to set
	 */
	public void setTranslators(List<Author> translators) {
		this.translators = translators;
	}
	/**
	 * @return the sequenceName
	 */
	public String getSequenceName() {
		return sequenceName;
	}
	/**
	 * @param sequenceName the sequenceName to set
	 */
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	/**
	 * @return the sequenceNumber
	 */
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	/**
	 * @return the coverImageSrc
	 */
	public String getCoverImageSrc() {
		return coverImageSrc;
	}
	/**
	 * @param coverImageSrc the coverImageSrc to set
	 */
	public void setCoverImageSrc(String coverImageSrc) {
		this.coverImageSrc = coverImageSrc;
	}
}
