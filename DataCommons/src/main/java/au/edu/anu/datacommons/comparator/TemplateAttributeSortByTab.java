package au.edu.anu.datacommons.comparator;

import java.util.Comparator;

import au.edu.anu.datacommons.data.db.model.TemplateAttribute;

public class TemplateAttributeSortByTab implements Comparator<TemplateAttribute> {

	@Override
	public int compare(TemplateAttribute attr1, TemplateAttribute attr2) {
		int compareValue = attr1.getTab().getTabOrder().compareTo(attr2.getTab().getTabOrder());
		
		if (compareValue != 0) {
			return compareValue;
		}
		compareValue = attr1.getFormOrder().compareTo(attr2.getFormOrder());
		return compareValue;
	}

}
