package au.edu.anu.dcbag.metadata;

import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.xmp.XMPMetadata;

public interface MetadataExtractor
{
	public Map<String, String[]> getMetadataMap();
	
	public Metadata getMetadata();
	
	public XMPMetadata getXmpMetadata() throws TikaException;
}
