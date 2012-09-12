FILES IN PACKAGE
	DcClient-0.0.1-SNAPSHOT.jar
		The JAR file containing java classes and dependencies.
		
	DcClient.bat
		File to execute the application in Windows. Contains URL of application server, path of local directory where bags are stored and other JVM options.
		
	DcClient.sh
		File to execute the application in Linux. Stores local bag location and server location.
		
	Readme.txt
		This file
		
	Sample CollInfo.txt
		Template for a collection information parameter file

	cacerts
		File containing public keys of self-signed certificates used by ANU DC Web Application servers.
		
CONFIGURATION
	Users are expected to specify a directory where the bags will be stored locally before being uploaded to the server. This is specified in the environment variable named 'BagsDir' in DcClient.bat and DcClient.sh . Ensure the user calling the application has read and write access to the directory.

	
HOW TO EXECUTE THE APPLICATION
To run the GUI version of DcClient (make sure the .bat or .sh file has been edited with the correct local bags location):
	
	DcClient.bat
		
		or
		
	DcClient.sh
	
To create an object and upload files to it:
	
	DcClient -c [collection param file] -u [username] -p [password]
		
	files.dir in the collection parameter file specifies the path to the directory containing the files that are to be uploaded against the collection

To download a bag:

	DcClient -d [pid] -u [username] -p [password]
	
To save the bag after making changes to files in the payload directory i.e. [PID]/data:

	DcClient -s [pid] -u [username] -p [password]
	
To upload a saved bag:

	DcClient -l [pid] -u [username] -p [password]
	
Help
	usage: DcClient [-c <arg>] [-d <arg>] [-h] [-i] [-l <arg>] [-p <arg>] [-s
       <arg>] [-u <arg>] [-x]
 -c,--param-file <arg>   Parameter file containing item attributes.
 -d,--download <arg>     Pid of the item whose bag to download.
 -h,--help               Display help
 -i,--instrument         Treat the data as coming from an instrument -
                         existing data cannot be deleted in a bag.
 -l,--upload <arg>       Pid of the item whose bag to upload.
 -p,--password <arg>     Password to be used for logging into ANU Data
                         Commons.
 -s,--save <arg>         Pid of the item whose files on local drive are to
                         be bagged.
 -u,--username <arg>     Username to be used for logging into ANU Data
                         Commons
 -x,--delete             Deletes the local copy of the bag after
                         performing the action.
