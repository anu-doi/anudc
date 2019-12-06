package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="template_tab")
public class TemplateTab {
	private Long id;
	private Template template;
	private String name;
	private String label;
	private Integer tabOrder;
	private String tooltip;

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

	@Column(name="label")
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name="tab_order")
	public Integer getTabOrder() {
		return tabOrder;
	}
	
	public void setTabOrder(Integer tabOrder) {
		this.tabOrder = tabOrder;
	}

	@Column(name="tooltip")
	public String getTooltip() {
		return tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}
