package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

/**
 * RelatedObject
 * 
 * Australian National University Data Commons
 * 
 * Class for the relatedObject element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class RelatedObject {
	private String key;
	private List<Relation> relations;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the RelatedObject class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public RelatedObject() {
		relations = new ArrayList<Relation>();
	}
	
	/**
	 * getKey
	 *
	 * Get the related object key
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the key
	 */
	@NotNull(message="The key of a related object may not be null")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getKey() {
		return key;
	}
	
	/**
	 * setKey
	 *
	 * Set the related object key
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * getRelations
	 *
	 * Get the related object relations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the relations
	 */
	@Valid
	@Size(min=1, message="Each related object must have a relationship type")
	@XmlElement(name="relation", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Relation> getRelations() {
		return relations;
	}
	
	/**
	 * setRelations
	 *
	 * Set the related object relations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param relations the relations to set
	 */
	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}
}
