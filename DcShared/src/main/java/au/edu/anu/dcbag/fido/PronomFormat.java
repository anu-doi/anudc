package au.edu.anu.dcbag.fido;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.PronomFormatsTxt;

/**
 * This class contains properties that correspond to individual elements of a String returned for a file by the Fido Script. The String as per Fido 1.0 will be
 * in the format:
 * 
 * <pre>
 * printmatch: 
 *     "OK,%(info.time)s,%(info.puid)s,%(info.formatname)s,%(info.signaturename)s,%(info.filesize)s,\"%(info.filename)s\",\"%(info.mimetype)s\",\"%(info.matchtype)s\"\n"
 * 
 *   printnomatch:
 *     "KO,%(info.time)s,,,,%(info.filesize)s,\"%(info.filename)s\",,\"%(info.matchtype)s\"\n"
 * </pre>
 * 
 * @see <a href="https://github.com/openplanets/fido">https://github.com/openplanets/fido</a>
 */
public class PronomFormat
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	public enum MatchStatus
	{
		OK, KO;
	}

	private String sourceStr;
	private MatchStatus matchStatus;
	private long timeToParse;
	private String puid;
	private String formatName;
	private String sigName;
	private long fileSize;
	private String fileName;
	private String mimeType;
	private String matchType;

	/**
	 * Creates a PronomFormat object by parsing a specified String.
	 * 
	 * @param fidoStr
	 *            the fido string to parse
	 */
	public PronomFormat(String fidoStr)
	{
		parseFidoStr(fidoStr);
	}
	
	/**
	 * Creates a PronomFormat object by parsing a fido string already stored in the PronomFormatsTxt file of a bag.
	 * 
	 * @param bag
	 *            Bag containing the file whose Fido String is to be read
	 * @param bf
	 *            File within the bag whose Fido String is to be read from PronomFormatsTxt tag file
	 */
	public PronomFormat(Bag bag, BagFile bf)
	{
		BagFile pronomFormatsTxt = bag.getBagFile(PronomFormatsTxt.FILEPATH);
		if (pronomFormatsTxt != null)
		{
			PronomFormatsTxt tagFile = new PronomFormatsTxt(pronomFormatsTxt.getFilepath(), pronomFormatsTxt, bag.getBagItTxt().getCharacterEncoding());
			String fidoStr = tagFile.get(bf.getFilepath());
			parseFidoStr(fidoStr);
		}
		else
			parseFidoStr("");
	}
	
	/**
	 * Parses a Fido String to extract its individual elements.
	 * 
	 * @param fidoStr
	 *            Fido String to parse
	 */
	private void parseFidoStr(String fidoStr)
	{
		if (fidoStr == null || fidoStr.length() == 0)
		{
			sourceStr = "";
			matchStatus = MatchStatus.KO;
			timeToParse = 0;
			puid = "";
			formatName = "";
			sigName = "";
			fileSize = 0;
			fileName = "";
			mimeType = "";
			matchType = "";
			
			LOGGER.warn("Fido String is null. Assigning default properties.");
		}
		else
		{
			this.sourceStr = fidoStr;
			// Split using commas as delimiters except for commas surrounded by double quotes.
			String[] tokens = sourceStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

			matchStatus = MatchStatus.valueOf(tokens[0].trim());
			timeToParse = Long.parseLong(tokens[1]);
			puid = tokens[2];
			formatName = stripQuotes(tokens[3]);
			sigName = stripQuotes(tokens[4]);
			fileSize = Long.parseLong(tokens[5]);
			fileName = stripQuotes(tokens[6]);
			mimeType = stripQuotes(tokens[7]);
			matchType = stripQuotes(tokens[8]);
		}
	}

	/**
	 * Gets the match status.
	 * 
	 * @return the match status
	 */
	public MatchStatus getMatchStatus()
	{
		return matchStatus;
	}

	/**
	 * Gets the time to parse.
	 * 
	 * @return the time to parse
	 */
	public long getTimeToParse()
	{
		return timeToParse;
	}

	/**
	 * Gets the puid.
	 * 
	 * @return the puid
	 */
	public String getPuid()
	{
		return puid;
	}

	/**
	 * Gets the format name.
	 * 
	 * @return the format name
	 */
	public String getFormatName()
	{
		return formatName;
	}

	/**
	 * Gets the sig name.
	 * 
	 * @return the sig name
	 */
	public String getSigName()
	{
		return sigName;
	}

	/**
	 * Gets the file size.
	 * 
	 * @return the file size
	 */
	public long getFileSize()
	{
		return fileSize;
	}

	/**
	 * Gets the file name.
	 * 
	 * @return the file name
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Gets the mime type.
	 * 
	 * @return the mime type
	 */
	public String getMimeType()
	{
		return mimeType;
	}

	/**
	 * Gets the match type.
	 * 
	 * @return the match type
	 */
	public String getMatchType()
	{
		return matchType;
	}

	/**
	 * Strips leading and trailing double quotes in a string.
	 * 
	 * @param str
	 *            the string from which to strip double quotes
	 * @return the string String without quotes
	 */
	private String stripQuotes(String str)
	{
		StringBuilder sb = new StringBuilder(str);

		if (str == null || str.equals(""))
			return str;

		if (str.charAt(str.length() - 1) == '"')
			sb.deleteCharAt(str.length() - 1);

		if (str.charAt(0) == '"')
			sb.deleteCharAt(0);

		return sb.toString();
	}

	@Override
	public String toString()
	{
		return this.sourceStr;
	}
}
