package au.edu.anu.datacommons.report.datasource;

import java.util.Arrays;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;

/**
 * SolrQueryExecuterFactory
 * 
 * Australian National University Data Commons
 * 
 * Factory class for the QueryExecuter.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrQueryExecuterFactory implements QueryExecuterFactory {
	public static final String SOLR_LOCATION = "SOLR_LOCATION";
	
	private static final String[] queryParameterClassNames;
	
	static {
		queryParameterClassNames = new String[] {
				java.lang.String.class.getName()
		};
	}
	
	/**
	 * createQueryExecuter
	 * 
	 * Creates the query executer
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param jrDataset
	 * @param parameters
	 * @return
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuterFactory#createQueryExecuter(net.sf.jasperreports.engine.JRDataset, java.util.Map)
	 */
	public JRQueryExecuter createQueryExecuter(JRDataset jrDataset,
			Map<String, ? extends JRValueParameter> parameters) throws JRException {
		return new SolrQueryExecuter(null, jrDataset, parameters);
	}

	/**
	 * createQueryExecuter
	 * 
	 * Creates the query executer
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param context
	 * @param jrDataset
	 * @param parameters
	 * @return
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.query.QueryExecuterFactory#createQueryExecuter(net.sf.jasperreports.engine.JasperReportsContext, net.sf.jasperreports.engine.JRDataset, java.util.Map)
	 */
	public JRQueryExecuter createQueryExecuter(JasperReportsContext context,
			JRDataset jrDataset, Map<String, ? extends JRValueParameter> parameters)
			throws JRException {
		return new SolrQueryExecuter(context, jrDataset, parameters);
	}

	/**
	 * getBuiltinParameters
	 * 
	 * Return the built in parameters
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see net.sf.jasperreports.engine.query.QueryExecuterFactory#getBuiltinParameters()
	 */
	public Object[] getBuiltinParameters() {
		return null;
	}
	
	/**
	 * supportsQueryParameterType
	 * 
	 * Checks if a parameter type is valid in being in the query.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param className
	 * @return
	 * @see net.sf.jasperreports.engine.query.QueryExecuterFactory#supportsQueryParameterType(java.lang.String)
	 */
	public boolean supportsQueryParameterType(String className) {
		return Arrays.binarySearch(queryParameterClassNames, className) >= 0;
	}
}
