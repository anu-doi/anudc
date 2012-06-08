package au.edu.anu.datacommons.search;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * SolrQuery
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrQuery {
	private List<String> queryFields;
	private List<String> returnFields;
	private List<String> filterFields;
	private String sortOrder;
	private Integer start;
	private Integer maxRows;
	
	/**
	 * Constructor
	 * 
	 * Initialises information for the query
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public SolrQuery () {
		queryFields = new ArrayList<String>();
		returnFields = new ArrayList<String>();
		filterFields = new ArrayList<String>();
	}
	
	/**
	 * addQueryField
	 *
	 * Add a query field with the given parameters to the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param field The field to add to the query
	 * @param value The value to query on that field
	 */
	public void addQueryField(String field, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(field);
		sb.append(":");
		sb.append(value);
		queryFields.add(sb.toString());
	}
	
	/**
	 * addQueryField
	 *
	 * Add a query field with the given string to the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param queryField The query field to add
	 */
	public void addQueryField(String queryField) {
		queryFields.add(queryField);
	}
	
	/**
	 * removeQueryField
	 *
	 * Remove the query field with the given parameters from the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param field The field to remove from the query
	 * @param value The value to remove on that field
	 */
	public void removeQueryField(String field, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(field);
		sb.append(":");
		sb.append(value);
		queryFields.remove(sb.toString());
	}
	
	/**
	 * removeQueryField
	 *
	 * Remove the query field with the given string from the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param queryField
	 */
	public void removeQueryField(String queryField) {
		queryFields.remove(queryField);
	}

	/**
	 * getQueryFields
	 *
	 * Get the list of query fields for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The list of query fields
	 */
	public List<String> getQueryFields() {
		return queryFields;
	}

	/**
	 * setQueryFields
	 *
	 * Set the list of query fields for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param queryFields
	 */
	public void setQueryFields(List<String> queryFields) {
		this.queryFields = queryFields;
	}
	
	/**
	 * addReturnField
	 *
	 * Add a field to return for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param returnField The return field to add
	 */
	public void addReturnField(String returnField) {
		returnFields.add(returnField);
	}
	
	/**
	 * removeReturnField
	 *
	 * Remove the return field from the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param returnField The return field to remove
	 */
	public void removeReturnField(String returnField) {
		returnFields.remove(returnField);
	}
	
	/**
	 * getReturnFields
	 *
	 * Gets the list of return fields for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of return fields
	 */
	public List<String> getReturnFields() {
		return returnFields;
	}

	/**
	 * setReturnFields
	 *
	 * Sets the list of return fields for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param returnFields A list of return fields
	 */
	public void setReturnFields(List<String> returnFields) {
		this.returnFields = returnFields;
	}
	
	/**
	 * addFilterField
	 *
	 * Add a filter field to the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param field The field to filter
	 * @param value The value to filter with
	 */
	public void addFilterField(String field, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(field);
		sb.append(":");
		sb.append(value);
		filterFields.add(sb.toString());
	}
	
	/**
	 * removeFilterField
	 *
	 * Remove the filter with the given information in the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param field The field to remove the filter from
	 * @param value The value of the filter to remove
	 */
	public void removeFilterField(String field, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(field);
		sb.append(":");
		sb.append(value);
		filterFields.remove(sb.toString());
	}
	
	/**
	 * getFilterFields
	 *
	 * Gets a list of filter fields that have been set for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The list of filter fields
	 */
	public List<String> getFilterFields() {
		return filterFields;
	}

	/**
	 * setFilterFields
	 *
	 * Sets a list of fields to filter for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param filterFields The list of fields to filter
	 */
	public void setFilterFields(List<String> filterFields) {
		this.filterFields = filterFields;
	}

	/**
	 * getSortOrder
	 *
	 * Gets the specified sort order for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The sort order
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * setSortOrder
	 *
	 * Sets the sort order for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param sortOrder The sort order
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	/**
	 * getStart
	 *
	 * Gets the offset for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The offset
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * setStart
	 *
	 * Sets the offset for the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param start The offset
	 */
	public void setStart(Integer start) {
		this.start = start;
	}

	/**
	 * getMaxRows
	 *
	 * Gets the maximum number of rows to return the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The maximum number of rows in the return
	 */
	public Integer getMaxRows() {
		return maxRows;
	}

	/**
	 * setMaxRows
	 *
	 * Sets the maximum number of rows to return the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param maxRows The maximum number of rows in the return
	 */
	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}
	
	/**
	 * generateQuery
	 *
	 * Generates a query with the information that has been specified
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A generated query
	 */
	public String generateQuery() {
		StringBuffer sb = new StringBuffer();
		
		if (queryFields.size() > 0) {
			sb.append("q=");
			sb.append(combineStrings(queryFields, " "));
		}
		else {
			sb.append("q=*:*");
		}
		if (returnFields.size() > 0) {
			sb.append("fl=");
			sb.append(combineStrings(returnFields, ","));
		}
		if (filterFields.size() > 0) {
			for (String field : filterFields) {
				sb.append("fq=");
				sb.append(field);
			}
		}
		if (Util.isNotEmpty(sortOrder)) {
			sb.append("sort=");
			sb.append(sortOrder);
		}
		return sb.toString();
	}
	
	/**
	 * generateMultivaluedMap
	 *
	 * Generates a multivalued map from the information that has been specified
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A generated multivalued map
	 */
	public MultivaluedMapImpl generateMultivaluedMap() {
		MultivaluedMapImpl mv = new MultivaluedMapImpl();
		StringBuffer query = new StringBuffer();
		if (queryFields.size() > 0) {
			mv.add("q", combineStrings(queryFields, " "));
		}
		else {
			query.append("*:*");
		}
		mv.add("q", query);
		if (returnFields.size() > 0) {
			mv.add("fl", combineStrings(returnFields, ","));
		}
		if (filterFields.size() > 0) {
			for (String field : filterFields) {
				mv.add("fq", field);
			}
		}
		if (Util.isNotEmpty(sortOrder)) {
			mv.add("sort", sortOrder);
		}
		return mv;
	}
	
	/**
	 * combineStrings
	 *
	 * Combines a list of given fields with the specified separator as a delimeter
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fields A list of fields to combine in to a string
	 * @param separator The separator
	 * @return The combined list of fields
	 */
	private String combineStrings(List<String> fields, String separator) {
		StringBuffer returnStr = new StringBuffer();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				returnStr.append(separator);
			}
			returnStr.append(fields.get(i));
		}
		
		return returnStr.toString();
	}
}
