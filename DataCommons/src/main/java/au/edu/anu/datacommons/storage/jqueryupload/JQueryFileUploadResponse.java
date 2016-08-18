package au.edu.anu.datacommons.storage.jqueryupload;

public class JQueryFileUploadResponse {

	private String name;
	private long size;
	private String type;
	private String url;
	private String error;

	public JQueryFileUploadResponse(String name, long size) {
		this.name = name;
		this.size = size;
	}

	public JQueryFileUploadResponse(String name, long size, String error) {
		this(name, size);
		this.error = error;
	}
	
	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public String getUrl() {
		return url;
	}

	public String getType() {
		return type;
	}

	public String getError() {
		return error;
	}

}