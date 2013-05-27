package au.edu.anu.dcclient.tasks;

public class FileTaskInfo {
	private long sizeInBytes;
	private String computedMd;
	private Status status;
	private int respStatusCode;
	private String respStatusStr;

	public enum Status {
		PENDING, INPROGRESS, SUCCESS, FAILED;
	}

	public FileTaskInfo(long sizeInBytes) {
		this.status = Status.PENDING;
		this.sizeInBytes = sizeInBytes;
	}
	
	public long getSizeInBytes() {
		return sizeInBytes;
	}

	void setFileSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public String getComputedMd() {
		return computedMd;
	}

	void setComputedMd(String computedMd) {
		this.computedMd = computedMd;
	}

	public Status getStatus() {
		return status;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	public int getRespStatusCode() {
		return respStatusCode;
	}

	void setRespStatusCode(int respStatusCode) {
		this.respStatusCode = respStatusCode;
	}

	public String getRespStatusStr() {
		return respStatusStr;
	}

	void setRespStatusStr(String respStatusStr) {
		this.respStatusStr = respStatusStr;
	}
}
