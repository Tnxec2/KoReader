package com.kontranik.koreader.parser.fb2reader.model;

import java.util.ArrayList;
import java.util.List;

public class FB2Scheme {

    public FB2Description description = new FB2Description();
	public List<FB2Section> sections = new ArrayList<>();
	public BinaryData cover;

	public FB2Section getSection(Integer orderId) {
		return sections.get(orderId);
	}

	public FB2Section getSection(String attrId) {
		for (FB2Section fb2Section : sections) {
			if ( fb2Section.id != null && fb2Section.id.equals(attrId) ) {
					return fb2Section;
			}
		}
		return null;
	}

	/**
	 * @return the description
	 */
	public FB2Description getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(FB2Description description) {
		this.description = description;
	}

	/**
	 * @return the sections
	 */
	public List<FB2Section> getSections() {
		return sections;
	}

	/**
	 * @param sections the sections to set
	 */
	public void setSections(List<FB2Section> sections) {
		this.sections = sections;
	}

}

