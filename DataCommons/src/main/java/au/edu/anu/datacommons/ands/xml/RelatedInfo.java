package au.edu.anu.datacommons.ands.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * RelatedInfo
 * 
 * Australian National University Data Commons
 * 
 * Class for the relatedInfo element in the ANDS RIF-CS schema
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
public class RelatedInfo {
	private String type;
	private Identifier identifier;
	private String title;
	private String notes;
	
	/**
	 * getType
	 *
	 * Get the related information type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the related information type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getIdentifier
	 *
	 * Get the related object identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifier
	 */
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * setIdentifier
	 *
	 * Set the related object identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * getTitle
	 *
	 * Get the related object title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * setTitle
	 *
	 * Set the related object title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * getNotes
	 *
	 * Get the related object notes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * setNotes
	 *
	 * Set the related object notes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
