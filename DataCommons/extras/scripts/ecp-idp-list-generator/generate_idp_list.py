'''
Australian National University Data Commons
Copyright (C) 2014  The Australian National University

This file is part of Australian National University Data Commons.

Australian National University Data Commons is free software: you
can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation,
either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.


This script parses the Federation or IdP metadata and looks for the SingleSignOn
end points that end with the provided end point string (this defaults to the ECP end
point).  The found end points are then written to the provided location

@author: Genevieve Turner
'''

import xml.etree.ElementTree as ET
import argparse
import sys
import logging

from xml.dom import minidom

def init_cmd_parser():
	parser = argparse.ArgumentParser()
	
	parser.add_argument("-i", "--input", dest="input_file", help="The file containing the idp/federation xml data", required="true")
	parser.add_argument("-o", "--output", dest="output_file", help="The file to write the list of end points to", required="true")
	parser.add_argument("-e", "--endpoint", dest="endpoint", help="The idp endpoint to find", default="/SAML2/SOAP/ECP")
	parser.add_argument("-l", "--log", dest="log_level", help="Logging level.  Values are DEBUG, INFO, WARN, ERROR, and CRITICAL",default="error")
	
	return parser.parse_args()

def process_entity(entity, namespaces, idps, endpoint):
	values = entity.findall(".//ms:IDPSSODescriptor/ms:SingleSignOnService", namespaces)
	for i in values:
		if i.attrib["Location"].endswith(endpoint):
			organization_name = entity.find(".//ms:Organization/ms:OrganizationDisplayName", namespaces)
			idp = ET.SubElement(idps, "identity-provider")
			entity_id = ET.SubElement(idp, "entityID")
			entity_id.text = entity.attrib["entityID"]
			ecp_id = ET.SubElement(idp, "ecp-url")
			ecp_id.text =  i.attrib["Location"]
			display_name = ET.SubElement(idp, "display-name")
			display_name.text = organization_name.text
	
def process_entities(entities, namespaces, idps, endpoint):
	entity_list = entities.findall(".//ms:EntityDescriptor", namespaces)
	for entity in entity_list:
		process_entity(entity, namespaces, idps, endpoint)

def set_logging_level(log_level):
	numeric_level = getattr(logging, log_level.upper(), None)
	if not isinstance(numeric_level, int):
		raise ValueError("Invalid log level: " + log_level)
	logging.basicConfig(level=numeric_level)
	
def main():
	cmd_params = init_cmd_parser()
	set_logging_level(cmd_params.log_level)
	
	idps = ET.Element("identity-providers")
	
	tree = ET.ElementTree()
	logging.info("Parsing file: " + cmd_params.input_file)
	tree.parse(cmd_params.input_file)
	
	namespaces = {
				'ms' : "urn:oasis:names:tc:SAML:2.0:metadata",
				'shibmd' : "urn:mace:shibboleth:metadata:1.0",
				'ds' : "http://www.w3.org/2000/09/xmldsig#",
				'saml' : "urn:oasis:names:tc:SAML:2.0:assertion"
				}
	
	root = tree.getroot()
	if (root.tag == "{urn:oasis:names:tc:SAML:2.0:metadata}EntityDescriptor"):
		print("Matched Entity Descriptor")
		process_entity(root, namespaces, idps, cmd_params.endpoint)
	elif (root.tag == "{urn:oasis:names:tc:SAML:2.0:metadata}EntitiesDescriptor"):
		process_entities(root, namespaces, idps, cmd_params.endpoint)
	else:
		raise ValueError("Input file did not have an EntityDescriptor or EntitiesDescriptor")
	
	f = open(cmd_params.output_file, 'w')
	logging.info("Writing to file: " + cmd_params.output_file)
	idps_str = ET.tostring(idps, "UTF-8")
	f.write(idps_str)
	f.close()

if __name__ == '__main__':
	main()
