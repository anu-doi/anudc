package au.edu.anu.datacommons.data.db.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="template_attribute")
public class TemplateAttribute {
	private Long id;
	private Template template;
	private String name;
	private FieldType fieldType;
	private String label;
	private String tooltip;
	private Boolean multiValued;
	private Boolean required;
	private String selectCode;
	private Long maxLength;
	private TemplateTab tab;
	private Integer formOrder;
	private Integer displayOrder;
	private Boolean hidden;
	private String extra;
	private List<TemplateAttributeColumn> columns;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="template_id")
	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name="field_type_id")
	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	@Column(name="label")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name="tooltip")
	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Column(name="multivalued")
	public Boolean getMultiValued() {
		return multiValued;
	}

	public void setMultiValued(Boolean multiValued) {
		this.multiValued = multiValued;
	}

	@Column(name="required")
	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	@Column(name="select_code")
	public String getSelectCode() {
		return selectCode;
	}

	public void setSelectCode(String selectCode) {
		this.selectCode = selectCode;
	}

	@Column(name="max_length")
	public Long getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;
	}
	
	@OneToOne
	@JoinColumn(name ="template_tab_id", referencedColumnName = "id")
	public TemplateTab getTab() {
		return tab;
	}

	public void setTab(TemplateTab tab) {
		this.tab = tab;
	}
	
	@Column(name="form_order")
	public Integer getFormOrder() {
		return formOrder;
	}

	public void setFormOrder(Integer formOrder) {
		this.formOrder = formOrder;
	}

	@Column(name="display_order")
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Column(name="hidden")
	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	@Column(name="extra")
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	@OneToMany(mappedBy="attribute", cascade=CascadeType.ALL, orphanRemoval=true)
	public List<TemplateAttributeColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<TemplateAttributeColumn> columns) {
		this.columns = columns;
	}
	
}
