package au.edu.anu.dcclient.progress;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressInputStream extends FilterInputStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgressInputStream.class);

	private final PropertyChangeSupport propertyChangeSupport;
	private final long totalBytes;
	private volatile long totalBytesRead;

	public ProgressInputStream(InputStream in, long totalBytes) {
		super(in);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.totalBytes = totalBytes;
	}

	public long getTotalBytes() {
		return totalBytes;
	}

	public long getTotalBytesRead() {
		return totalBytesRead;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	@Override
	public int read() throws IOException {
		int b = super.read();
		updateProgress(1);
		return b;
	}

	// Not overriding read(byte[]) as it calls read(byte[], int, int)

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return (int) updateProgress(super.read(b, off, len));
	}

	@Override
	public long skip(long n) throws IOException {
		return updateProgress(super.skip(n));
	}

	@Override
	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	private long updateProgress(long numBytesRead) {
		if (numBytesRead > 0) {
			long oldTotalNumBytesRead = this.totalBytesRead;
			this.totalBytesRead += numBytesRead;
			int oldPercentComplete = (int) (oldTotalNumBytesRead * 100L / totalBytes);
			int newPercentComplete = (int) (this.totalBytesRead * 100L / totalBytes);
			propertyChangeSupport.firePropertyChange("percentComplete", oldPercentComplete, newPercentComplete);
		}
		return numBytesRead;
	}
}
