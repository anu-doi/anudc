package au.edu.anu.datacommons.data.db.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="publish_location")
public class PublishLocation {
	private Long id;
	private String code;
	private String name;
	private String execute_class;
	private Long requires;
	private List<FedoraObject> fedoraObjects;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="execute_class")
	public String getExecute_class() {
		return execute_class;
	}

	public void setExecute_class(String execute_class) {
		this.execute_class = execute_class;
	}
	
	@Column(name="requires")
	public Long getRequires() {
		return requires;
	}
	
	public void setRequires(Long requires) {
		this.requires = requires;
	}
	
	@ManyToMany(mappedBy="publishedLocations", fetch=FetchType.LAZY)
	public List<FedoraObject> getFedoraObjects() {
		return fedoraObjects;
	}

	public void setFedoraObjects(List<FedoraObject> fedoraObjects) {
		this.fedoraObjects = fedoraObjects;
	}
}
