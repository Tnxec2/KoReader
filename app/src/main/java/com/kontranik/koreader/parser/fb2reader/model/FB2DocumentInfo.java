package com.kontranik.koreader.parser.fb2reader.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FB2DocumentInfo {
    public List<Author> authors = new ArrayList<>();
    public String version;
    public StringBuffer history = new StringBuffer();
    public Date date;
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the history
	 */
	public StringBuffer getHistory() {
		return history;
	}
	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		this.history = new StringBuffer(history);
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
}
