package com.kontranik.koreader.parser.fb2reader.model;

public class Attribut {
    public  String name;
    public  String value;
	/**
	 * @param name
	 * @param value
	 */
	public Attribut(String name, String value) {
		this.name = name;
		this.value = value;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

    
}
