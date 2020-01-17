package au.edu.anu.datacommons.xml.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAO;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAOImpl;
import au.edu.anu.datacommons.data.db.model.AuditObject;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.data.db.model.SelectCodePK;
import au.edu.anu.datacommons.data.db.model.Template;
import au.edu.anu.datacommons.data.db.model.TemplateAttribute;
import au.edu.anu.datacommons.data.db.model.TemplateAttributeColumn;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;
import au.edu.anu.datacommons.xml.dc.DublinCore;
import au.edu.anu.datacommons.xml.dc.DublinCoreConstants;

public class SaveTransform {
	static final Logger LOGGER = LoggerFactory.getLogger(SaveTransform.class);
	
	/**
	 * Save data
	 * 
	 * @param template The template
	 * @param fedoraObject The object to save against
	 * @param form The data to save
	 * @param rid
	 * @return The saved fedora object
	 * @throws FedoraClientException
	 * @throws JAXBException
	 * @throws IOException
	 */
	public FedoraObject saveData(Template template, FedoraObject fedoraObject, Map<String, List<String>> form, Long rid) throws FedoraClientException, JAXBException, IOException {
		List<DataItem> items = new ArrayList<DataItem>();
		for (TemplateAttribute attr : template.getTemplateAttributes()) {
			if (attr.getFieldType().getName().equals("Table")) {
				items.addAll(generateItem(attr, form));
			}
			else {
				String name = attr.getName();
				List<String> values = form.get(name);
				if (values != null) {
					items.addAll(generateItem(attr, values));
				}
			}
		}
		Data newData = new Data();
		newData.setItems(items);
		
		if (fedoraObject == null) {
			fedoraObject = saveNewData(template, newData, rid);
		}
		else {
			fedoraObject = updateData(template, fedoraObject, newData, rid);
		}
		
		return fedoraObject;
	}
	
	private FedoraObject saveNewData(Template template, Data data, Long rid) throws FedoraClientException, JAXBException {
		setName(data);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		JAXBTransform jaxbTransform = new JAXBTransform();
		String dublinCore = generateDublinCore(jaxbTransform, data);
		
		//Marshal the data for saving then create/update the appropriate streams
		StringWriter sw = new StringWriter();
		jaxbTransform.marshalStream(sw, data, Data.class, properties);
		
		String location = String.format("%s/objects/%s/datastreams/%s/content"
				, GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI)
				, template.getTemplatePid()
				, Constants.XML_TEMPLATE);
		
		String item = FedoraBroker.createNewObject(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SAVENAMESPACE));
		
		FedoraBroker.addDatastreamBySource(item, Constants.XML_SOURCE, "XML Source", sw.toString());
		FedoraBroker.addDatastreamByReference(item, Constants.XML_TEMPLATE, "M", "XML Template", location);
		if (Util.isNotEmpty(dublinCore)) {
			FedoraBroker.modifyDatastreamBySource(item, Constants.DC, "Dublin Core Record for this object", dublinCore);
		}
		
		DataItem groupItem = data.getFirstElementByName("ownerGroup");
		String ownerGroup = groupItem.getValue();
		
		FedoraObject fedoraObject = new FedoraObject();
		fedoraObject.setObject_id(item);
		fedoraObject.setGroup_id(new Long(ownerGroup));
		fedoraObject.setPublished(Boolean.FALSE);
		fedoraObject.setTmplt_id(template.getTemplatePid());

		FedoraObjectDAOImpl fedoraObjectDAO = new FedoraObjectDAOImpl();
		fedoraObjectDAO.create(fedoraObject);
		saveAuditModifyRow(fedoraObject, null, sw.toString(), rid);
		
		return fedoraObject;
	}
	
	private FedoraObject updateData(Template template, FedoraObject fedoraObject, Data data, Long rid) throws IOException, JAXBException, FedoraClientException {
		setName(data);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		String existingData = getData(fedoraObject);

		JAXBTransform jaxbTransform = new JAXBTransform();
		String dublinCore = generateDublinCore(jaxbTransform, data);
		
		//Marshal the data for saving then create/update the appropriate streams
		StringWriter sw = new StringWriter();
		jaxbTransform.marshalStream(sw, data, Data.class, properties);

		FedoraBroker.modifyDatastreamBySource(fedoraObject.getObject_id(), Constants.XML_SOURCE, "XML Source", sw.toString());
		if(Util.isNotEmpty(dublinCore)) {
			FedoraBroker.modifyDatastreamBySource(fedoraObject.getObject_id(), Constants.DC, "Dublin Core Record for this object", dublinCore);
		}
		
		DataItem groupItem = data.getFirstElementByName("ownerGroup");
		Long ownerGroup = new Long(groupItem.getValue());
		
		if (!fedoraObject.getGroup_id().equals(ownerGroup)) {
			fedoraObject.setGroup_id(new Long(ownerGroup));
			FedoraObjectDAOImpl fedoraObjectDAO = new FedoraObjectDAOImpl();
			fedoraObjectDAO.update(fedoraObject);
		}
		
		saveAuditModifyRow(fedoraObject, existingData, sw.toString(), rid);
		
		return fedoraObject;
	}
	
//	private DublinCore getDublinCore(Data data) {
//		DublinCore
//	}
	
	/**
	 * Generate the table items
	 * 
	 * @param attr Attribute
	 * @param form Submitted values
	 * @return
	 */
	private List<DataItem> generateItem(TemplateAttribute attr, Map<String, List<String>> form) {
		List<TemplateAttributeColumn> columns = attr.getColumns();
		
		List<DataItem> tableData = new ArrayList<DataItem>();
		
		for (TemplateAttributeColumn column : columns) {
			List<String> values = form.get(column.getName());
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					if (tableData.size() <= i) {
//						generateItem(column, values.get(i));
						DataItem item = new DataItem();
						item.setName(attr.getName());
						tableData.add(item);
					}
					if (Util.isNotEmpty(values.get(i))) {
						DataItem childItem = generateItem(column, values.get(i));
						if (childItem != null) {
							tableData.get(i).getChildValues().add(childItem);
						}
					}
				}
			}
		}
		
		for (int i = tableData.size() - 1; i >= 0; i--) {
			if (tableData.get(i).getChildValues().size() == 0) {
				tableData.remove(i);
			}
		}
		
		return tableData;
	}
	
	private DataItem generateItem(TemplateAttributeColumn column, String value) {
		if (Util.isNotEmpty(value)) {
			DataItem dataItem = new DataItem();
			dataItem.setName(column.getName());
			dataItem.setValue(value);
			
			dataItem.setDescription(getOptionDescription(column.getSelectCode(), value));
			
			return  dataItem;
		}
		return null;
	}
	
	/**
	 * Generate items
	 * 
	 * @param attr
	 * @param values
	 * @return
	 */
	private List<DataItem> generateItem(TemplateAttribute attr, List<String> values) {
		List<DataItem> dataItems = new ArrayList<DataItem>();
		for (String value : values) {
			DataItem item = generateItem(attr, value);
			if (item != null) {
				dataItems.add(item);
			}
		}
		return dataItems;
	}
	
	private DataItem generateItem(TemplateAttribute attr, String value) {
		if (Util.isNotEmpty(value)) {
			DataItem dataItem = new DataItem();
			dataItem.setName(attr.getName());
			dataItem.setValue(value);
			
			dataItem.setDescription(getOptionDescription(attr.getSelectCode(), value));
			
			return  dataItem;
		}
		return null;
	}
	
	private String getOptionDescription(String code, String value) {
		if (code != null) {
			SelectCodePK selectCodePK = new SelectCodePK();
			selectCodePK.setSelect_name(code);
			selectCodePK.setCode(value);
			SelectCodeDAO selectCodeDAO = new SelectCodeDAOImpl();
			SelectCode selectCode = selectCodeDAO.getSingleById(selectCodePK);
			if (selectCode != null) {
				return selectCode.getDescription();
			}
		}
		// Should this be expanded to add the group description?
		
		return null;
	}
	
//	private 
	
	private String getData(FedoraObject fedoraObject) throws FedoraClientException, IOException {
		if (fedoraObject != null) {
			InputStream dataStream = getInputStream(fedoraObject.getObject_id(), Constants.XML_SOURCE);
			String data = IOUtils.toString(dataStream, StandardCharsets.UTF_8.name());
			return data;
		}
		return null;
	}
	
	private InputStream getInputStream (String pid, String dsId) throws FedoraClientException {
		InputStream xslStream = null;
		if(Util.isNotEmpty(pid)) {
			xslStream = FedoraBroker.getDatastreamAsStream(pid, dsId);
		}
		return xslStream;
	}
	
	private void setName(Data data) {
		if (data.getFirstElementByName("name") != null) {
			return;
		}

		String nameFields = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_NAMEFIELDS);
		String[] splitName = nameFields.split(",");
		
		StringBuilder sb = new StringBuilder();
		
		for (String nameField : splitName) {
			DataItem nameElement = data.getFirstElementByName(nameField);
			if (nameElement != null) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(nameElement.getValue());
			}
		}
		
		DataItem nameItem = new DataItem();
		nameItem.setName("name");
		nameItem.setValue(sb.toString());
		data.getItems().add(nameItem);
	}
	
	private String generateDublinCore(JAXBTransform jaxbTransform, Data data) throws JAXBException {
		DublinCore dublinCore = new DublinCore();
		
//		DublinCoreConstants.getFieldName(propertyName);
		
		for (DataItem item : data.getItems()) {
			String dublinCoreLocalpart = DublinCoreConstants.getFieldName(item.getName());
			if (Util.isNotEmpty(dublinCoreLocalpart)) {
				dublinCore.getItems_().add(createJAXBElement(DublinCoreConstants.DC, dublinCoreLocalpart, item.getValue()));
			}
		}
		
		if (dublinCore != null && dublinCore.getItems_().size() > 0) {
			StringWriter dcSW = new StringWriter();
			Map<String, Object> dublinCoreProperties = new HashMap<String, Object>();
			dublinCoreProperties.put(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
			dublinCoreProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbTransform.marshalStream(dcSW, dublinCore, DublinCore.class, dublinCoreProperties);
			return dcSW.toString();
		}
		
		return null;
	}
	
	private JAXBElement<String> createJAXBElement(String namespace, String localpart, String value) {
		QName qname = null;
		if (Util.isNotEmpty(namespace)) {
			qname = new QName(namespace, localpart);
		}
		else {
			qname = new QName(localpart);
		}
		JAXBElement<String> element = new JAXBElement<String>(qname, String.class, value);
		
		return element;
	}

	private void saveAuditModifyRow(FedoraObject fedoraObject, String oldMetadata, String newMetadata, Long rid) {
		CustomUser customUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AuditObject auditObject = new AuditObject();
		auditObject.setLog_date(new java.util.Date());
		auditObject.setLog_type("MODIFIED");
		auditObject.setObject_id(fedoraObject.getId());
		auditObject.setUser_id(customUser.getId());
		auditObject.setRid(rid);
		auditObject.setBefore(oldMetadata);
		auditObject.setAfter(newMetadata);

		GenericDAO<AuditObject,Long> auditDao = new GenericDAOImpl<AuditObject,Long>(AuditObject.class);
		auditDao.create(auditObject);
		
	}
}
