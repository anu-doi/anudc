package au.edu.anu.datacommons.doi;

import java.net.URI;

import org.datacite.schema.kernel_4.Resource.RightsList.Rights;

public class LicenceTypeForDOI {
	private String code;
	private String uri;
	private String description;
	
	public LicenceTypeForDOI() {
		
	}
	
//	public LicenceTypeForDOI(String uri, String description) {
//		this.uri = uri;
//		this.description = description;
//	}
	
	public LicenceTypeForDOI(String code, String uri, String description) {
		this.code = code;
		this.uri = uri;
		this.description = description;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Rights getRight() {
		Rights right = new Rights();
		if (null != code) {
			right.setRightsIdentifierScheme(code);
		}
		if (null != uri) {
			right.setRightsURI(uri);
		}
		if (null != description) {
			right.setValue(description);
		}
		return right;
	}
}
