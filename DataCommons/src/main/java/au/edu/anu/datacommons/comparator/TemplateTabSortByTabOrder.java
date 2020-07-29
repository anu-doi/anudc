package au.edu.anu.datacommons.comparator;

import java.util.Comparator;

import au.edu.anu.datacommons.data.db.model.TemplateTab;

public class TemplateTabSortByTabOrder implements Comparator<TemplateTab> {

	@Override
	public int compare(TemplateTab tab1, TemplateTab tab2) {
		return tab1.getTabOrder().compareTo(tab2.getTabOrder());
	}

}
