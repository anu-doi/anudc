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

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.ScanResult;

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

    private int timeoutMillisec;
    private final String host;
    private final int port;

    /**
	 * Instantiates a new clam scan object.
	 * 
	 * @param host
	 *            the hostname of the server running ClamAV scanning service
	 * @param port
	 *            the port of the the server running ClamAV scanning service
	 */
    public ClamScan(String host, int port, int timeoutMillisec) {
        this.host = host;
        this.port = port;
        this.timeoutMillisec = timeoutMillisec;
    }

	/**
	 * Sends a STATS request to the ClamAV service.
	 * 
	 * @return Response from service as String
	 */
    public String stats() {
        return cmd(STATS);
    }
    
	/**
	 * Sends a Ping request to the ClamAV service to ensure it's up and running.
	 * 
	 * @return true, if successful, false otherwise
	 */
    public boolean ping() {
        return "PONG\0".equals(cmd(PING));
    }

	/**
	 * Sends a command to the ClamAV service.
	 * 
	 * @param cmd
	 *            Command to send
	 * @return Response as String
	 */
    public String cmd(byte[] cmd) {

        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(getHost(), getPort()), getTimeoutMillisec());
        } catch (IOException e) {
            LOGGER.error("could not connect to clamd server");
            try
			{
				socket.close();
			}
			catch (IOException e1) { }
            return null;
        }

        try {
            socket.setSoTimeout(getTimeoutMillisec());
        } catch (SocketException e) {
            LOGGER.error("Could not set socket timeout to " + getTimeoutMillisec() + "ms", e);
        }

        DataOutputStream dos = null;
        StringBuilder response = new StringBuilder();
        try {  // finally to close resources

            try {
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                LOGGER.error("could not open socket OutputStream", e);
                return null;
            }

            try {
                dos.write(cmd);
                dos.flush();
            } catch (IOException e) {
                LOGGER.debug("error writing " + new String(cmd) + " command", e);
                return null;
            }

            InputStream is;
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                LOGGER.error("error getting InputStream from socket", e);
                return null;
            }

            int read = CHUNK_SIZE;
            byte[] buffer = new byte[CHUNK_SIZE];

            while (read == CHUNK_SIZE) {
                try {
                    read = is.read(buffer);
                } catch (IOException e) {
                    LOGGER.error("error reading result from socket", e);
                    break;
                }
                response.append(new String(buffer, 0, read));
            }

        } finally {
            if (dos != null) try {
                dos.close();
            } catch (IOException e) {
                LOGGER.debug("exception closing DOS", e);
            }
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.debug("exception closing socket", e);
            }
        }

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Response: " + response.toString());

        return response.toString();
    }

	/**
	 * The method to call if you already have the content to scan in-memory as a byte array.
	 * 
	 * @param in
	 *            the byte array to scan
	 * @return the result of the scan
	 * @throws IOException
	 */
    public ScanResult scan(byte[] in) throws IOException {
        return scan(new ByteArrayInputStream(in));
    }

	/**
	 * The preferred method to call. This streams the contents of the InputStream to clamd, so the entire content is not loaded into memory at the same time.
	 * 
	 * @param in
	 *            the InputStream to read. The stream is NOT closed by this method.
	 * @return a ScanResult representing the server response
	 */
    public ScanResult scan(InputStream in) {
        Socket socket = new Socket();
        DataOutputStream dos = null;
        String response = null;
        ScanResult sr = null;

        try {
        	LOGGER.trace("Scanning stream from ClamAV on {}:{} ...", getHost(), getPort());
            socket.connect(new InetSocketAddress(getHost(), getPort()), getTimeoutMillisec());
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(INSTREAM);
            
            int read = CHUNK_SIZE;
            byte[] buffer = new byte[CHUNK_SIZE];
            while (read == CHUNK_SIZE) {
            	read = in.read(buffer);
            	if (read > 0) {
            		dos.writeInt(read);
            		dos.write(buffer, 0, read);
            	}
            }
            dos.writeInt(0);
            dos.flush();
            read = socket.getInputStream().read(buffer);
            if (read > 0) {
            	response = new String(buffer, 0, read);
            }
            LOGGER.debug("ClamAV Response: {}", response.trim());
            sr = new ScanResult(response.trim());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        	sr = new ScanResult(e);
        } finally {
        	IOUtils.closeQuietly(dos);
        	IOUtils.closeQuietly(socket);
        	IOUtils.closeQuietly(in);
        }

        return sr;
    }

    /**
	 * Gets the host.
	 * 
	 * @return the host
	 */
    public String getHost() {
        return host;
    }

    /**
	 * Gets the port.
	 * 
	 * @return the port
	 */
    public int getPort() {
        return port;
    }

    /**
	 * Gets the timeout.
	 * 
	 * @return the timeout
	 */
    public int getTimeoutMillisec() {
        return timeoutMillisec;
    }

    /**
     * Socket timeout in milliseconds
     *
     * @param timeoutMillisec socket timeout in milliseconds
     */
    public void setTimeout(int timeoutMillisec) {
        this.timeoutMillisec = timeoutMillisec;
    }
}
