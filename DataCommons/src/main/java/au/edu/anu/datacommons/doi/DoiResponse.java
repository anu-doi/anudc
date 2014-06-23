/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.doi;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class contains the details of a response received from the Digital Object Identifier (DOI) web service. The
 * response is recieved either in XML or JSON format as specified in the request's query parameters. The values can then
 * be directly mapped to an instance of this object by unmarshalling the response.
 * 
 * @see <a href="http://ands.org.au/resource/r9-cite-my-data-v1.1-tech-doco.pdf">Cite My Data M2M Service</a>
 * 
 * @author Rahul Khanna
 * 
 */
@XmlRootElement(name = "response")
public class DoiResponse {
	private String type;
	private String code;
	private String message;
	private String doi;
	private String url;
	private String appId;
	private String verboseMsg;

	/**
	 * Gets the type.
	 * 
	 * @return Type as String
	 */
	@XmlAttribute(name = "type")
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            Type as String
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the response code.
	 * 
	 * @return Response code as String
	 */
	@XmlElement(name = "responsecode")
	public String getCode() {
		return code;
	}

	/**
	 * Sets the response code.
	 * 
	 * @param code
	 *            Response code as String
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the response message.
	 * 
	 * @return Response message as String
	 */
	@XmlElement(name = "message")
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the response message.
	 * 
	 * @param message
	 *            Response message as String
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the Digital Object Identifier (DOI).
	 * 
	 * @return DOI as String
	 */
	@XmlElement(name = "doi")
	public String getDoi() {
		return doi;
	}

	/**
	 * Sets the DOI.
	 * 
	 * @param doi
	 *            DOI as String
	 */
	public void setDoi(String doi) {
		this.doi = doi;
	}

	/**
	 * Gets the URL the DOI will resolve to.
	 * 
	 * @return URL as String
	 */
	@XmlElement(name = "doi")
	public String getUrl() {
		return url;
	}

	/**
	 * Sets URL the DOI will resolve to.
	 * 
	 * @param url
	 *            URL as String
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the App ID.
	 * 
	 * @return App ID as String
	 */
	@XmlElement(name = "app_id")
	public String getAppId() {
		return appId;
	}

	/**
	 * Sets the App ID.
	 * 
	 * @param appId
	 *            App ID as String.
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * Gets the verbose message.
	 * 
	 * @return Verbose Message as String.
	 */
	@XmlElement(name = "verbosemessage")
	public String getVerboseMsg() {
		return verboseMsg;
	}

	/**
	 * Sets the verbose message.
	 * 
	 * @param verboseMsg
	 *            Verbose Message as String.
	 */
	public void setVerboseMsg(String verboseMsg) {
		this.verboseMsg = verboseMsg;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("DOI Service Response: type={0}, code={1}, message={2}, doi={3}, url={4}, app_id={5}, verbosemessage={6}.",
						this.getType(), this.getCode(), this.getMessage(), this.getDoi(), this.getUrl(),
						this.getAppId(), this.getVerboseMsg());
	}
}
