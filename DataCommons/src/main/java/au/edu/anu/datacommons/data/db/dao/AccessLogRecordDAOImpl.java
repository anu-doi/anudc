package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.security.AccessLogRecord;

public class AccessLogRecordDAOImpl extends GenericDAOImpl<AccessLogRecord, Long> implements AccessLogRecordDAO
{

	public AccessLogRecordDAOImpl(Class<AccessLogRecord> type)
	{
		super(type);
	}
}
