package au.edu.anu.datacommons.search;

import org.apache.solr.common.SolrDocumentList;

public class SolrSearchResult {
	SolrDocumentList resultDocuments = null;
	
	public SolrSearchResult(SolrDocumentList results) {
		this.resultDocuments = results;
	}
	
	public long getNumFound() {
		return resultDocuments.getNumFound();
	}
	
	public long getStart() {
		return resultDocuments.getStart();
	}
	
	public float getMaxScore() {
		return resultDocuments.getMaxScore();
	}
	
	public SolrDocumentList getDocumentList() {
		return resultDocuments;
	}
	
	public void setDocumentList(SolrDocumentList results) {
		this.resultDocuments = results;
	}
}
