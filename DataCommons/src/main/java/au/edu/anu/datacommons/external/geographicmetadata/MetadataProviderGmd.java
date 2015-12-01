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

package au.edu.anu.datacommons.external.geographicmetadata;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBElement;

import org.isotc211._2005.gco.CharacterStringPropertyType;
import org.isotc211._2005.gmd.CICitationType;
import org.isotc211._2005.gmd.CIContactType;
import org.isotc211._2005.gmd.CIResponsiblePartyPropertyType;
import org.isotc211._2005.gmd.EXExtentPropertyType;
import org.isotc211._2005.gmd.EXExtentType;
import org.isotc211._2005.gmd.EXSpatialTemporalExtentType;
import org.isotc211._2005.gmd.EXTemporalExtentPropertyType;
import org.isotc211._2005.gmd.MDDataIdentificationType;
import org.isotc211._2005.gmd.MDKeywordsPropertyType;
import org.isotc211._2005.gmd.MDMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import au.edu.anu.datacommons.external.ExternalMetadataException;
import au.edu.anu.datacommons.external.ExternalMetadataProvider;
import au.edu.anu.datacommons.external.ParamInfo;
import au.edu.anu.datacommons.webservice.bindings.Collection;
import au.edu.anu.datacommons.webservice.bindings.DateCoverage;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

/**
 * @author Rahul Khanna
 *
 */
public class MetadataProviderGmd implements ExternalMetadataProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataProviderGmd.class);

	private static final ClientResponse.Status[] SUCCESS_STATUSES = { ClientResponse.Status.OK };
	private static final String FRIENDLY_NAME = "Geographic MetaData extensible markup language (GMD)";
	private static final List<ParamInfo> requiredParams = new ArrayList<>(1);

	static {
		requiredParams.add(new ParamInfo("gmdXmlUrl", "Geographic MetaData URL"));
	}		

	@Autowired
	Client client;

	@Override
	public String getFriendlyName() {
		return FRIENDLY_NAME;
	}

	@Override
	public String getFqClassName() {
		return this.getClass().getName();
	}

	@Override
	public List<ParamInfo> getRequiredParams() {
		return requiredParams;
	}

	@Override
	public FedoraItem retrieveMetadata(MultivaluedMap<String, String> params) throws ExternalMetadataException {
		String gmdXmlUrl = getGmdXmlUrl(params);
		WebResource webRes = client.resource(gmdXmlUrl);
		ClientResponse clientResp = webRes.get(ClientResponse.class);

		MDMetadataType resp = null;
		if (isSuccess(clientResp.getClientResponseStatus())) {
			resp = webRes.get(MDMetadataType.class);
		} else {
			String entity = clientResp.getEntity(String.class);
			throw new ExternalMetadataException(MessageFormat.format("{0} returned unsuccessful HTTP Status: {1}, {2}",
					FRIENDLY_NAME, clientResp.getStatus(), entity));
		}
		return createFedoraItem(resp, gmdXmlUrl);
	}

	private String getGmdXmlUrl(MultivaluedMap<String, String> params) {
		return params.getFirst("gmdXmlUrl");
	}

	private boolean isSuccess(Status clientResponseStatus) {
		for (ClientResponse.Status iStatus : SUCCESS_STATUSES) {
			if (iStatus.equals(clientResponseStatus)) {
				return true;
			}
		}
		return false;
	}

	private FedoraItem createFedoraItem(MDMetadataType mdMetadata, String xmlUrl) {
		Collection coll = new Collection();

		// harcoded value - subtype
		coll.setSubType("dataset");
		LOGGER.trace("sub type: {}", coll.getSubType());

		// url of from where the source XML was downloaded
		coll.setWebsiteAddress(xmlUrl);
		LOGGER.trace("website url: {}", coll.getWebsiteAddress());

		// title -> name
		coll.setTitle(getTitle(mdMetadata));
		LOGGER.trace("title: {}", coll.getTitle());

		// language -> metaLang
		coll.setMetadataLanguage(getMetadataLanguage(mdMetadata));
		LOGGER.trace("metadata language: {}", coll.getMetadataLanguage());

		// fileIdentifier -> externalId
		coll.setExtIds(getFileIdentifiers(mdMetadata));
		LOGGER.trace("externalId: {}", coll.getExtIds());

		// abstract -> briefDesc
		coll.setBriefDesc(getAbstract(mdMetadata));
		LOGGER.trace("brief description: {}", coll.getBriefDesc());

		// timePeriod.beginPosition -> dateFrom
		coll.setDateCoverage(getDateCoverage(mdMetadata));
		LOGGER.trace("date coverage: {}", coll.getDateCoverage());

		// statement -> coverageDateText
		coll.setCoverageDateTextList(getCoverageDateTextList(mdMetadata));
		LOGGER.trace("coverage date text: {}", coll.getCoverageDateTextList());

		// electronicMailAddress -> email
		coll.setEmails(getEmails(mdMetadata));
		LOGGER.trace("emails: {}", coll.getEmails());

		// postalAddress -> postalAddress
		coll.setContactAddress(getContactAddress(mdMetadata));
		LOGGER.trace("contact address: {}", coll.getContactAddress());

		// responsibleParty -> supervisor
		coll.setPrincipalInvestigators(getPrincipalInvestigators(mdMetadata));
		LOGGER.trace("principal investigators: {}", coll.getPrincipalInvestigators());

		// keyword -> locSubject
		coll.setKeywords(getKeywords(mdMetadata));
		LOGGER.trace("keywords: {}", coll.getKeywords());

		return coll;
	}

	private String getTitle(MDMetadataType mdMetadata) {
		try {
			CICitationType ciCitation = getDataIdentification(mdMetadata).getCitation().getCICitation();
			@SuppressWarnings("unchecked")
			JAXBElement<String> characterString = (JAXBElement<String>) ciCitation.getTitle().getCharacterString();
			return characterString.getValue();
		} catch (Exception e) {
			return "";
		}
	}

	private String getMetadataLanguage(MDMetadataType mdMetadata) {
		String metadataLang = "";
		try {
			metadataLang = ((JAXBElement<String>) mdMetadata.getLanguage().getCharacterString()).getValue();
			if (metadataLang.equals("eng")) {
				metadataLang = "en";
			}
		} catch (Exception e) {
			// no op - empty string
		}
		return metadataLang;
	}

	private List<String> getFileIdentifiers(MDMetadataType mdMetadata) {
		List<String> fileIdentifiers = new ArrayList<String>();
		try {
			@SuppressWarnings("unchecked")
			JAXBElement<String> fileIdentifierElement = (JAXBElement<String>) mdMetadata.getFileIdentifier()
					.getCharacterString();
			fileIdentifiers.add(fileIdentifierElement.getValue());
		} catch (Exception e) {
			// no op - empty list
		}
		return fileIdentifiers;
	}

	private String getAbstract(MDMetadataType mdMetadata) {
		String briefDesc;
		briefDesc = ((JAXBElement<String>) getDataIdentification(mdMetadata).getAbstract().getCharacterString())
				.getValue();
		return briefDesc;
	}

	private List<DateCoverage> getDateCoverage(MDMetadataType mdMetadata) {
		List<DateCoverage> dateCoverageList = new ArrayList<DateCoverage>();

		try {
			List<EXExtentPropertyType> extentElements = getDataIdentification(mdMetadata).getExtent();
			for (EXExtentPropertyType extentElement : extentElements) {
				EXExtentType exExtent = extentElement.getEXExtent();
				for (EXTemporalExtentPropertyType temporalExtentPropType : exExtent.getTemporalElement()) {
					try {
						@SuppressWarnings("unchecked")
						JAXBElement<EXSpatialTemporalExtentType> exTemporalExtent = (JAXBElement<EXSpatialTemporalExtentType>) temporalExtentPropType
								.getEXTemporalExtent();
					} catch (Exception e) {
						// no op - continue on to the item
					}
				}
			}
		} catch (Exception e) {
			// no op - empty list
		}
		return dateCoverageList;
	}

	private List<String> getCoverageDateTextList(MDMetadataType mdMetadata) {
		List<String> coverageDateTextList = new ArrayList<>();

		try {
			@SuppressWarnings("unchecked")
			JAXBElement<String> statement = (JAXBElement<String>) mdMetadata.getDataQualityInfo().get(0)
					.getDQDataQuality().getLineage().getLILineage().getStatement().getCharacterString();
			coverageDateTextList.add(statement.getValue());
		} catch (Exception e) {
			// no op - empty list
		}

		return coverageDateTextList;
	}

	private List<String> getEmails(MDMetadataType mdMetadata) {
		List<String> emails = new ArrayList<>();

		try {
			List<CIResponsiblePartyPropertyType> responsibleParties = getDataIdentification(mdMetadata).getCitation()
					.getCICitation().getCitedResponsibleParty();

			for (CIResponsiblePartyPropertyType party : responsibleParties) {
				try {
					List<CharacterStringPropertyType> emailElements = party.getCIResponsibleParty().getContactInfo()
							.getCIContact().getAddress().getCIAddress().getElectronicMailAddress();
					for (CharacterStringPropertyType emailElement : emailElements) {
						String email = ((JAXBElement<String>) emailElement.getCharacterString()).getValue();
						if (email != null & email.trim().length() > 0 && !emails.contains(email.trim())) {
							emails.add(email.trim());
						}
					}
				} catch (Exception e) {
					// no op - continue on to next item
				}
			}
		} catch (Exception e) {
			// no op - empty list
		}
		return emails;
	}

	private String getContactAddress(MDMetadataType mdMetadata) {
		StringBuilder contactAddress = new StringBuilder();
		try {
			CIContactType ciContact = getDataIdentification(mdMetadata).getPointOfContact().get(0)
					.getCIResponsibleParty().getContactInfo().getCIContact();

			if (ciContact != null) {
				// deliveryPoint
				for (CharacterStringPropertyType i : ciContact.getAddress().getCIAddress().getDeliveryPoint()) {
					@SuppressWarnings("unchecked")
					String deliveryPoint = ((JAXBElement<String>) i.getCharacterString()).getValue().trim();
					contactAddress.append(deliveryPoint);
					contactAddress.append("\r\n");
				}

				// city
				@SuppressWarnings("unchecked")
				String city = ((JAXBElement<String>) ciContact.getAddress().getCIAddress().getCity()
						.getCharacterString()).getValue().trim();
				contactAddress.append(city);
				contactAddress.append("\r\n");

				// postalCode
				@SuppressWarnings("unchecked")
				String postalCode = ((JAXBElement<String>) ciContact.getAddress().getCIAddress().getPostalCode()
						.getCharacterString()).getValue().trim();
				contactAddress.append(postalCode);
				contactAddress.append("\r\n");

				// country
				@SuppressWarnings("unchecked")
				String country = ((JAXBElement<String>) ciContact.getAddress().getCIAddress().getCountry()
						.getCharacterString()).getValue().trim();
				contactAddress.append(country);
				contactAddress.append("\r\n");

			}
		} catch (Exception e) {
			// no op - blank string
		}
		return contactAddress.toString().trim();
	}

	private List<String> getPrincipalInvestigators(MDMetadataType mdMetadata) {
		List<String> investigators = new ArrayList<>();

		try {
			List<CIResponsiblePartyPropertyType> citedResponsiblePartyList = getDataIdentification(mdMetadata)
					.getCitation().getCICitation().getCitedResponsibleParty();

			for (CIResponsiblePartyPropertyType i : citedResponsiblePartyList) {
				try {
					@SuppressWarnings("unchecked")
					String value = ((JAXBElement<String>) i.getCIResponsibleParty().getIndividualName()
							.getCharacterString()).getValue();
					if (value != null && value.trim().length() > 0 && !investigators.contains(value.trim())) {
						investigators.add(value);
					}
				} catch (Exception e) {
					// no op - continue on to the next item
				}
			}
		} catch (Exception e) {
			// no op - empty list
		}

		return investigators;
	}

	private List<String> getKeywords(MDMetadataType mdMetadata) {
		List<String> keywords = new ArrayList<>();

		try {
			List<MDKeywordsPropertyType> descriptiveKeywords = getDataIdentification(mdMetadata)
					.getDescriptiveKeywords();

			for (MDKeywordsPropertyType i : descriptiveKeywords) {
				try {
					for (CharacterStringPropertyType j : i.getMDKeywords().getKeyword()) {
						String keyword = ((JAXBElement<String>) j.getCharacterString()).getValue();
						if (keyword != null && keyword.trim().length() > 0) {
							keywords.add(keyword.trim());
						}
					}
				} catch (Exception e) {
					// no op - continue on to the next item
				}
			}
		} catch (Exception e) {
			// no op - empty list
		}

		return keywords;
	}

	@SuppressWarnings("unchecked")
	private MDDataIdentificationType getDataIdentification(MDMetadataType mdMetadata) {
		return ((JAXBElement<MDDataIdentificationType>) mdMetadata.getIdentificationInfo().get(0)
				.getAbstractMDIdentification()).getValue();
	}
}
