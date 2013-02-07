/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.xml.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * TemplateColumn
 * 
 * Australian National University Data Commons
 * 
 * The TemplateColumn class is utilised for marshalling and unmarshalling JAXB objects with the
 * template root element.
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 
 */
public class TemplateColumn {
	private String name;
	private String label;
	private String fieldType;
	private String maxLength;
	private String readonly;
	private String classValue;
	private List<TemplateOption> templateOptions;
	
	public TemplateColumn() {
		templateOptions = new ArrayList<TemplateOption>();
	}
	
	/**
	 * getName
	 * 
	 * Returns the name attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The name for the column
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	/**
	 * setName
	 * 
	 * Sets the name attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param name The name for the column
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getLabel
	 * 
	 * Returns the label attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The label for the column
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}
	
	/**
	 * setLabel
	 * 
	 * Sets the label attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param label The label for the column
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * getFieldType
	 * 
	 * Returns the fieldType attribute of the field in the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The fieldType for the column
	 */
	@XmlAttribute
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * setFieldType
	 * 
	 * Sets the fieldType attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param fieldType The fieldType for the field in the column
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * getMaxLength
	 * 
	 * Returns the maximum length attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The maximum length for the field in the column
	 */
	@XmlAttribute
	public String getMaxLength() {
		return maxLength;
	}

	/**
	 * setMaxLength
	 * 
	 * Sets the maximum length attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param maxLength The maximum length for field in the column
	 */
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * getReadonly
	 * 
	 * Returns the readonly attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The value of read only for the field in the column
	 */
	@XmlAttribute
	public String getReadonly() {
		return readonly;
	}

	/**
	 * setReadonly
	 * 
	 * Sets the readonly attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param readonly The value of read only for the field in the column
	 */
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	/**
	 * getClassValue
	 * 
	 * Returns the class attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The value of the class attribute for the column
	 */
	@XmlAttribute(name="class")
	public String getClassValue() {
		return classValue;
	}

	/**
	 * setClassValue
	 * 
	 * Sets the class attribute of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param classValue The value of the class attribute for the column
	 */
	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	/**
	 * getTemplateOptions
	 * 
	 * Returns the options elements of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The option elements for the column
	 */
	@XmlElement(name="option")
	public List<TemplateOption> getTemplateOptions() {
		return templateOptions;
	}

	/**
	 * setTemplateOptions
	 * 
	 * Sets the options elements of the column
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param templateOptions The option elements for the column
	 */
	public void setTemplateOptions(List<TemplateOption> templateOptions) {
		this.templateOptions = templateOptions;
	}
	
}
