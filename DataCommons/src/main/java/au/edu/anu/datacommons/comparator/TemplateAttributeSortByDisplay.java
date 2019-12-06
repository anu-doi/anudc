package au.edu.anu.datacommons.comparator;

import java.util.Comparator;

import au.edu.anu.datacommons.data.db.model.TemplateAttribute;

public class TemplateAttributeSortByDisplay implements Comparator<TemplateAttribute> {

	@Override
	public int compare(TemplateAttribute attr1, TemplateAttribute attr2) {
		return attr1.getDisplayOrder().compareTo(attr2.getDisplayOrder());
	}

}