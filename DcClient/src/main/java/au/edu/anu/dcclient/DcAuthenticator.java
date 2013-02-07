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

package au.edu.anu.dcclient;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This class extends the Authenticator class to enable credentials management.
 */
public class DcAuthenticator extends Authenticator
{
	private final String username;
	private final String password;
	
	/**
	 * Creates a new instance of DcAuthenticator with specified username and password that will be returned when getPasswordAuthentication is called.
	 * 
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public DcAuthenticator(String username, String password)
	{
		super();
		this.username = username;
		this.password = password;
		CustomClient.setAuth(username, password);
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(this.username, this.password.toCharArray());
	}
}
