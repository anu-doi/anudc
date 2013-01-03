Bulk Creation of Collections and Upload of Data
-----------------------------------------------

SUMMARY

This document explains the process for creating multiple collections and uploading data (files) to those collections in the ANU Data Commons System.


COMMAND LINE INTERFACE

Creating a collection through the Command Line Interface (CLI) requires the creation of a text file containing details of a single collection. The text file, known as a parameter file, contains details of a collection in a simple Key=Value format. The following is an example of a parameter file with basic fields:

	name=Survey of students at ANU
	briefDesc=A survey of undergraduate students at the ANU of their opinions on university facilities
	ownerGroup=13
	email=someone@anu.edu.au;someone2@anu.edu.au
	externalId=Astronomy Collection Identifier

"name" is the title given to the collection
"briefDesc" is a brief description of the collection
"ownerGroup" is the unique identifier of the group the collection should belong to. Optical Astronomy is group 13 in the ANU Data Commons.
"email" is the email address(es) of the contact person for that collection.
"externalId" is an identifier, if any, assigned to that collection outside of the ANU Data Commons.

To specify the location of the files to be uploaded to the collection, add the following to the parameter file:

	Windows:
		files.dir=C:\Path To\The Directory\Containing collection files
		
	Linux/Mac OS:
		files.dir=/home/user/location/of/files

Also, when a collection is created, it is highly recommended that it is linked to a project in the ANU Data Commons to facilitate easier administration of collections by knowing which project they belong to. This requires the creation of a project through the web interface accessible at https://datacommons.anu.edu.au:8443/DataCommons . To associate a collection to a project, add the following to the parameter file:

	relation=isOutputOf,anudc:123

where 'anudc:123' is the identifier of the project in ANU Data Commons. Please check with the ANU Data Commons team of the actual value to use.

The above parameter file contains all the details required to create a collection in ANU Data Commons with a title, brief description and a contact person's email address. The created collection will also be allocated to a group in the system with the ID '1'. A collection is assigned to a group to restrict access to that collection and the files associated with it, only to members of that group.

To create a single collection using the parameter file containing the above details, execute the following at the command prompt:

	Windows:
		DcClient.bat -c PARAMETERFILE.TXT -u [UNI_ID] -p [PASSWORD] -x

	Linux/Mac OS:
		sh DcClient.sh -c PARAMETERFILE.TXT -u [UNI_ID] -p [PASSWORD] -x

Once the command has finished executing the identifier assigned by ANU Data Commons to the created collection is appended to the collection file, so executing the same command with the same parameter file will update the previously created collection instead of creating the new one.


BULK CREATION

The command line interface described above creates a single collection using a single parameter file passed to it as an argument. To create more than one collection, the aforementioned command must be executed multiple times, each with a different parameter file. Assuming each parameter file is named with the extension .txt and is located in a subdirectory 'collfiles' in the DcClient directory:

	On Windows:
		for %file in (collfiles\*.txt) do @DcClient.bat -c "%file" -u [UNI_ID] -p [PASSWORD] -x

	On Linux/Mac OS:
		for file in collfiles/*.txt; do sh DcClient.sh -c $file -u [UNI_ID] -p [PASSWORD] -x; done

The command above runs the command repeatedly each time with a single collection parameter file.