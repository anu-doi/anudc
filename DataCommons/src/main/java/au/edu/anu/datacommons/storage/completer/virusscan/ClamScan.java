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
 * @see <a href="https://github.com/philvarner/clamavj/">https://github.com/philvarner/clamavj/</a>
 */
public class ClamScan {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClamScan.class);

    public static final int CHUNK_SIZE = 2048;
    private static final byte[] INSTREAM = "zINSTREAM\0".getBytes();
    private static final byte[] PING = "zPING\0".getBytes();
    private static final byte[] STATS = "nSTATS\n".getBytes();
    private static final byte[] SCAN = "zSCAN\n".getBytes();
    
    // TODO: IDSESSION, END

    //    It is mandatory to prefix this command with n or z, and all commands inside IDSESSION must  be
    //    prefixed.
    //
    //    Start/end  a  clamd  session. Within a session multiple SCAN, INSTREAM, FILDES, VERSION, STATS
    //    commands can be sent on the same socket without opening new connections.  Replies  from  clamd
    //    will  be  in  the form '<id>: <response>' where <id> is the request number (in ascii, starting
    //    from 1) and <response> is the usual clamd reply.  The reply lines have same delimiter  as  the
    //    corresponding  command had.  Clamd will process the commands asynchronously, and reply as soon
    //    as it has finished processing.
    //
    //    Clamd requires clients to read all the replies it sent, before sending more commands  to  pre-vent prevent
    //    vent  send()  deadlocks. The recommended way to implement a client that uses IDSESSION is with
    //    non-blocking sockets, and  a  select()/poll()  loop:  whenever  send  would  block,  sleep  in
    //    select/poll  until either you can write more data, or read more replies.  Note that using non-blocking nonblocking
    //    blocking sockets without the select/poll loop and  alternating  recv()/send()  doesn't  comply
    //    with clamd's requirements.
    //
    //    If  clamd detects that a client has deadlocked,  it will close the connection. Note that clamd
    //    may close an IDSESSION connection too if you don't follow the protocol's requirements.

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
    
    private Socket createSocket() throws UnknownHostException, IOException {
    	return new Socket(getHost(), getPort());
    }
    
    private void sendCommand(Socket socket, byte[] cmd) throws IOException {
    	socket.getOutputStream().write(cmd);
    	socket.getOutputStream().flush();
    }
    
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