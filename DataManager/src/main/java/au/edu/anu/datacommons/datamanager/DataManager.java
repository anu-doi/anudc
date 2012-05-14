/**
 * 
 */
package au.edu.anu.datacommons.datamanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;

/**
 * DataManager
 * 
 * Australian National University Data Commons
 * 
 * Provides a command line interface to reference a file to a Fedora Object.
 * 
 * Usage: <code>
 * FILEMANAGER filename [filename]...
 * </code>
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
public final class DataManager
{
	/**
	 * main
	 * 
	 * Australian National University Data Commons
	 * 
	 * Entry point for this class when called from the command line. Checks that the parameters passed to it are correct, then creates a job for each file that
	 * needs to be referenced.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param args
	 *            Command line arguments as String[]
	 * 
	 */
	public static void main(String[] args)
	{
		// Check if an argument is provided. If not, display syntax help.
		if (args.length > 0)
		{
			FedoraClient client = FedoraProps.getFedoraClient();
			
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].indexOf(".properties") != -1)
				{
					System.out.print("Processing file " + args[i] + "...");
					try
					{
						FileJob job;
						job = new FileJob(new File(args[i]), FedoraProps.getUploadBaseUri());
						job.execute(client);
						System.out.println("done.");
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
						System.out.println("failed. Unable to open the file " + args[i]);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						System.out.println("failed. Unable to open the file " + args[i]);
					}
					catch (FedoraClientException e)
					{
						e.printStackTrace();
						System.out.println("failed. Unable to execute request to the Fedora repository. ");
					}
				}
				else
				{
					System.out.println("Skipping non-properties file " + args[i]);
				}
			}
		}
		else
		{
			// Display help.
			dispHelp();
		}
	}

	/**
	 * dispHelp
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method's called when the parameters passed to the main method or their syntax are incorrect. Displays the syntax of this utility.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	private static void dispHelp()
	{
		System.out.println("Processes a file that's been uploaded by associating it with a Fedora Object and one of its Datastreams.");
		System.out.println();
		System.out.println("FILEMANAGER filename [filename]...");
		System.out.println();
		System.out.println("\tfilename\t\tSpecifies the full path to a .properties file. More than one file can be specified as additional arguments.");
		System.out.println();
	}

}
