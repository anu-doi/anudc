/**
 * 
 */
package au.edu.anu.datacommons.storage.jqueryupload;

import java.util.Objects;

/**
 * @author Rahul Khanna
 *
 */
public class ContentRange {
	
	private String bytesUnit;
	private long firstBytePos;
	private long lastBytePos;
	private long instanceLength;
	
	public ContentRange(String bytesUnit, long firstBytePos, long lastBytePos, long instanceLength) {
		this.bytesUnit = bytesUnit;
		this.firstBytePos = firstBytePos;
		this.lastBytePos = lastBytePos;
		this.instanceLength = instanceLength;
	}

	public ContentRange(String contentRangeStr) {
		parse(Objects.requireNonNull(contentRangeStr));
	}
	
	private void parse(String contentRangeStr) throws IllegalArgumentException {
		String[] split = contentRangeStr.trim().split(" ", 2);
		if (split.length != 2) {
			throw new IllegalArgumentException();
		}
		
		bytesUnit = split[0].trim();
		
		split = split[1].split("/", 2);
		if (split.length != 2) {
			throw new IllegalArgumentException();
		}
		
		instanceLength = Long.parseLong(split[1], 10);
		
		split = split[0].split("-", 2);
		if (split.length != 2) {
			throw new IllegalArgumentException();
		}

		firstBytePos = Long.parseLong(split[0], 10);
		lastBytePos = Long.parseLong(split[1], 10);
	}

	public String getBytesUnit() {
		return bytesUnit;
	}

	public long getFirstBytePos() {
		return firstBytePos;
	}

	public long getLastBytePos() {
		return lastBytePos;
	}

	public long getInstanceLength() {
		return instanceLength;
	}
	
	@Override
	public String toString() {
		return String.format("%s %d-%d/%d", getBytesUnit(), getFirstBytePos(), getLastBytePos(), getInstanceLength());
	}
}
