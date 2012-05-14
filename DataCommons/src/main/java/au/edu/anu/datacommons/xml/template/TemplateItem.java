package au.edu.anu.datacommons.xml.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * TemplateItem
 * 
 * Australian National University Data Commons
 * 
 * The TemplateItem class is utilised for marshalling and unmarshalling JAXB objects with the
 * template root element.
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 
 */
public class TemplateItem {
	private String name;
	private String label;
	private String fieldType;
	private String tooltip;
	private String saveType;
	private String defaultValue;
	private String maxLength;
	private String disabled;
	private String readOnly;
	private String editPerm;
	private String classValue;
	private List<TemplateOption> templateOptions;
	private List<TemplateColumn> templateColumns;
	
	public TemplateItem() {
		templateOptions = new ArrayList<TemplateOption>();
		templateColumns = new ArrayList<TemplateColumn>();
	}
	
	/**
	 * getName
	 * 
	 * Returns the name attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The name of the item
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	/**
	 * setName
	 * 
	 * Sets the name attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param name The name of the item
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
	 * @return The label for the item
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/**
	 * setLabel
	 * 
	 * Sets the label attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param label The label for the item
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * getFieldType
	 * 
	 * Returns the fieldType attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The fieldType for the item
	 */
	@XmlAttribute
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * setFieldType
	 * 
	 * Sets the fieldType attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param fieldType The fieldType for the item
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * getTooltip
	 * 
	 * Returns the tooltip attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The tooltip for the item
	 */
	@XmlAttribute
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * setTooltip
	 * 
	 * Sets the tooltip attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param tooltip The tooltip of the item
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * getSaveType
	 * 
	 * Returns the saveType attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The save type of the item (e.g. &quot;single&quot;,&quot;multiple&quot;,&quot;table&quot;)
	 */
	@XmlAttribute
	public String getSaveType() {
		return saveType;
	}

	/**
	 * setSaveType
	 * 
	 * Sets the saveType attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param saveType The name of the item (e.g. &quot;single&quot;,&quot;multiple&quot;,&quot;table&quot;)
	 */
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	/**
	 * getDefaultValue
	 * 
	 * Returns the defaultValue attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The default value of the item
	 */
	@XmlAttribute
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * setDefaultValue
	 * 
	 * Sets the defaultValue attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param name The default value of the item
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * getMaxLength
	 * 
	 * Returns the maximum length attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The maximum length for the item
	 */
	@XmlAttribute
	public String getMaxLength() {
		return maxLength;
	}

	/**
	 * setMaxLength
	 * 
	 * Sets the maximum length attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param maxLength The maximum length for the item
	 */
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * getDisabled
	 * 
	 * Returns the disabled attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return Indicates whether the field should be disabled
	 */
	@XmlAttribute
	public String getDisabled() {
		return disabled;
	}

	/**
	 * setDisabled
	 * 
	 * Sets the disabled attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param name Indicates whether the field should be disabled
	 */
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	/**
	 * getReadonly
	 * 
	 * Returns the readonly attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return Indicates whether the field should be read only
	 */
	@XmlAttribute(name="readonly")
	public String getReadOnly() {
		return readOnly;
	}

	/**
	 * setReadonly
	 * 
	 * Sets the readonly attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param readonly Indicates whether the field should be read only
	 */
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * getEditPerm
	 * 
	 * Returns the editPerm attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The edit permissions of the item
	 */
	@XmlAttribute
	public String getEditPerm() {
		return editPerm;
	}

	/**
	 * setEditPerm
	 * 
	 * Sets the name attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param editPerm The edit permissions of the item
	 */
	public void setEditPerm(String editPerm) {
		this.editPerm = editPerm;
	}

	/**
	 * getClassValue
	 * 
	 * Returns the class attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The value of the class attribute for the item
	 */
	@XmlAttribute(name="class")
	public String getClassValue() {
		return classValue;
	}

	/**
	 * setClassValue
	 * 
	 * Sets the class attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param classValue The value of the class attribute for the item
	 */
	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	/**
	 * getTemplateOptions
	 * 
	 * Returns the option elements of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The option elements for the item
	 */
	@XmlElement(name="option")
	public List<TemplateOption> getTemplateOptions() {
		return templateOptions;
	}

	/**
	 * setTemplateOptions
	 * 
	 * Sets the options elements of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param templateOptions The option elements for the item
	 */
	public void setTemplateOptions(List<TemplateOption> templateOptions) {
		this.templateOptions = templateOptions;
	}

	/**
	 * getTemplateColumns
	 * 
	 * Returns the column elements of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The column elements of the item
	 */
	@XmlElement(name="column")
	public List<TemplateColumn> getTemplateColumns() {
		return templateColumns;
	}

	/**
	 * setTemplateColumns
	 * 
	 * Sets the column elements of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param name The column elements of the item
	 */
	public void setTemplateColumns(List<TemplateColumn> templateColumns) {
		this.templateColumns = templateColumns;
	}
}
