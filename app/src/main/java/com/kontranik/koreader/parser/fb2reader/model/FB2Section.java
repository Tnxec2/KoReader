package com.kontranik.koreader.parser.fb2reader.model;

public class FB2Section {

    public Integer orderid;
    public FB2Elements typ;
    public String id;
    public String title;
    public Integer deep;
    public StringBuffer text;
    public Integer parentId;

    public FB2Section() {}

    public FB2Section(Integer orderid, String id, FB2Elements typ, Integer deep, Integer parentId) {
		this.orderid = orderid;
		this.id = id;
        this.typ = typ;
        this.title = null;
        this.deep = deep;
        this.text = new StringBuffer();
        this.parentId = parentId;
    }

    public String toString() {
        return "Section. Orderid: " + orderid + ", id: " + id + ", ParentId: " + parentId + ", Deep: " + deep + ", Typ: " + typ + ", Title: " + title;
    }

	/**
	 * @return the orderid
	 */
	public Integer getOrderid() {
		return orderid;
	}

	/**
	 * @param orderid the orderid to set
	 */
	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}

	/**
	 * @return the typ
	 */
	public FB2Elements getTyp() {
		return typ;
	}

	/**
	 * @param typ the typ to set
	 */
	public void setTyp(FB2Elements typ) {
		this.typ = typ;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the deep
	 */
	public Integer getDeep() {
		return deep;
	}

	/**
	 * @param deep the deep to set
	 */
	public void setDeep(Integer deep) {
		this.deep = deep;
	}

	/**
	 * @return the text
	 */
	public StringBuffer getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(StringBuffer text) {
		this.text = text;
	}

	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	

}
