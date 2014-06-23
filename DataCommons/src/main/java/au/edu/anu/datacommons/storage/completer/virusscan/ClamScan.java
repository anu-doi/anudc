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

package au.edu.anu.datacommons.storage.completer.virusscan;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * This class provides methods to can a file for viruses.
 * 
 * @author Rahul Khanna
 * 
 */
public class ClamScan {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClamScan.class);

    public static final int CHUNK_SIZE = 2048;
    private static final byte[] INSTREAM = "zINSTREAM\0".getBytes();
    private static final byte[] PING = "zPING\0".getBytes();
    private static final byte[] STATS = "nSTATS\n".getBytes();
    private static final byte[] SCAN = "zSCAN\n".getBytes();
    
	/**
	 * Sends a STATS request to the ClamAV service.
	 * 
	 * @return Response from service as String
	 * @throws IOException 
	 */
    public String stats() throws IOException {
    	String stats = null;
    	try (Socket socket = createSocket()) {
    		sendCommand(socket, STATS);
    		stats = readResponse(socket);
		}
        return stats;
    }
    
	/**
	 * Sends a Ping request to the ClamAV service to ensure it's up and running.
	 * 
	 * @return true, if successful, false otherwise
	 * @throws IOException
	 */
    public boolean ping() throws IOException {
    	boolean pingResponse = false;
    	try (Socket socket = createSocket()) {
    		sendCommand(socket, PING);
    		if (readResponse(socket).equals("PONG")) {
    			pingResponse = true;
    		}
		}
        return pingResponse;
    }

	/**
	 * Scans a specified file for viruses
	 * 
	 * @param filepath
	 *            File to scan
	 * @return Scan result as String
	 * @throws IOException
	 */
    public String scan(Path filepath) throws IOException {
    	String scanResult = null;
    	
    	try (Socket socket = createSocket()) {
			sendCommand(socket, SCAN);
			sendCommand(socket, filepath.toString().getBytes(StandardCharsets.UTF_8));
			sendCommand(socket, "\0".getBytes());
			
			scanResult = readResponse(socket);
			LOGGER.debug("ClamAV Daemon response for {}: [{}]", filepath.toString(), scanResult);
		}
    	
    	return scanResult;
    }
    
	/**
	 * Scans a specified stream for viruses
	 * 
	 * @param scanStream
	 *            Stream to scan
	 * @return Scan result as String
	 * @throws IOException
	 */
    public String scan(InputStream scanStream) throws IOException {
    	String scanResult = null;
    	
    	try (Socket socket = createSocket()) {
			sendCommand(socket, INSTREAM);
			sendStream(socket, scanStream);
			
			scanResult = readResponse(socket);
			LOGGER.debug("ClamAV response for stream: [{}]", scanResult);
		} finally {
			IOUtils.closeQuietly(scanStream);
		}
    	
    	return scanResult;
    }
    
	/**
	 * Creates a socket to the ClamAV server.
	 * 
	 * @return Open socket as Socket
	 * @throws UnknownHostException
	 * @throws IOException
	 */
    private Socket createSocket() throws UnknownHostException, IOException {
    	return new Socket(getHost(), getPort());
    }
    
	/**
	 * Sends a command to the ClamAV daemon.
	 * 
	 * @param socket
	 *            Socket to the ClamAV daemon
	 * @param cmd
	 *            Command to send
	 * @throws IOException
	 */
    private void sendCommand(Socket socket, byte[] cmd) throws IOException {
    	socket.getOutputStream().write(cmd);
    	socket.getOutputStream().flush();
    }
    
	/**
	 * Sends the contents of the specified InputStream to the ClamAV daemon.
	 * 
	 * @param socket
	 *            Socket to the ClamAV daemon
	 * @param is
	 *            InputStream to send
	 * @throws IOException
	 */
    private void sendStream(Socket socket, InputStream is) throws IOException {
    	DataOutputStream socketOs = new DataOutputStream(socket.getOutputStream());
    	byte[] sendBuffer = new byte[CHUNK_SIZE];
		for (int nBytesRead = is.read(sendBuffer); nBytesRead != -1; nBytesRead = is.read(sendBuffer)) {
			if (nBytesRead > 0) {
				socketOs.writeInt(nBytesRead);
				socketOs.write(sendBuffer, 0, nBytesRead);
			}
		}
		socketOs.writeInt(0);
		socketOs.flush();
    }
    
	/**
	 * Reads a response from the socket.
	 * 
	 * @param socket
	 *            Socket to the ClamAV daemon
	 * 
	 * @return Response from ClamAV daemon as String
	 * @throws IOException
	 */
    private String readResponse(Socket socket) throws IOException {
    	InputStreamReader socketIs = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
    	StringBuilder resultBuilder = new StringBuilder();
		char[] rcvBuffer = new char[CHUNK_SIZE];
		for (int nCharsRead = socketIs.read(rcvBuffer); nCharsRead != -1; nCharsRead = socketIs.read(rcvBuffer)) {
			resultBuilder.append(rcvBuffer, 0, nCharsRead);
		}
		return resultBuilder.toString().trim();
    }

    /**
	 * Gets the host.
	 * 
	 * @return the host
	 */
    public String getHost() {
        return GlobalProps.getClamScanHost();
    }

    /**
	 * Gets the port.
	 * 
	 * @return the port
	 */
    public int getPort() {
        return GlobalProps.getClamScanPort();
    }

    /**
	 * Gets the timeout.
	 * 
	 * @return the timeout
	 */
    public int getTimeoutMillis() {
        return GlobalProps.getClamScanTimeout();
    }
}