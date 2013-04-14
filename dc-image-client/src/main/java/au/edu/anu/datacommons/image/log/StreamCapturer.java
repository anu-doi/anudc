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

package au.edu.anu.datacommons.image.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * StreamCapturer
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class StreamCapturer extends OutputStream {
    private StringBuilder buffer;
    private Consumer consumer;
    private PrintStream old;

    /**
     * Constructor
     * 
     * Constructor class for capturing an output stream
     * 
     * <pre>
     * Version	Date		Developer				Description
     * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
     * </pre>
     * 
     * @param consumer The consumer class
     * @param old The stream that we are redirecting
     */
    public StreamCapturer(Consumer consumer, PrintStream old) {
        buffer = new StringBuilder(128);
        this.old = old;
        this.consumer = consumer;
    }

    /**
     * write
     * 
     * Append the text to the consumer
     *
     * <pre>
     * Version	Date		Developer				Description
     * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
     * </pre>
     * 
     * @param b The byte to write
     * @throws IOException
     * @see java.io.OutputStream#write(int)
     */
	@Override
	public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            consumer.appendText(buffer.toString());
            buffer.delete(0, buffer.length());
        }
        old.print(c);
	}

}
