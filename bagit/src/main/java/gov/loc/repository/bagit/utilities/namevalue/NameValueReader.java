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

package gov.loc.repository.bagit.utilities.namevalue;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

public interface NameValueReader extends Iterator<NameValueReader.NameValue> {

	public class NameValue implements Map.Entry<String, String> {
		private String name;
		private String value;
		
		public NameValue(String name, String value) {
			assert name != null;
			this.name = name;
			this.value = value;
		}
			
		public NameValue()	{			
		}
		
		public void setName(String name) {
			assert name != null;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public String setValue(String value) {
			this.value = value;
			return value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return MessageFormat.format("Name is {0}. Value is {1}.", this.name, this.value);
		}

		@Override
		public String getKey() {
			return this.name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (! (obj instanceof NameValue)) return false;
			NameValue that = (NameValue)obj;
			if (! this.name.equals(that.getName())) return false;
			if ((this.value != null && that.getValue() == null) || (this.value == null && that.getValue() != null) || (! this.value.equals(that.getValue()))) return false;
			return true;			
		}
		
		@Override
		public int hashCode() {
			return 42 + this.name.hashCode() + (this.value != null ? this.value.hashCode() : 0);
		}
	}	
}
