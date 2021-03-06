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

package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import au.edu.anu.datacommons.validator.AtLeastOneOf;

/**
 * Coverage
 * 
 * Australian National University Data Commons
 * 
 * Class for the citationMetadata element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
//TODO add validation on coverage parts
@AtLeastOneOf(fieldNames={"temporalDates", "spatialCoverage"}, message="Coverage requires at least one temporal date or coverage date")
public class Coverage {
	private List<Temporal> temporalDates;
	private List<Spatial> spatialCoverage;
	
	public Coverage() {
		temporalDates = new ArrayList<Temporal>();
		spatialCoverage = new ArrayList<Spatial>();
	}
	
	/**
	 * getTemporalDates
	 *
	 * Get the temporal dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the temporalDates
	 */
	@Valid
	@XmlElement(name="temporal", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Temporal> getTemporalDates() {
		return temporalDates;
	}
	
	/**
	 * setTemporalDates
	 *
	 * Set the temporal dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param temporalDates the temporalDates to set
	 */
	public void setTemporalDates(List<Temporal> temporalDates) {
		this.temporalDates = temporalDates;
	}
	
	/**
	 * getSpatialCoverage
	 *
	 * Get the spatial coverage
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the spatialCoverage
	 */
	@Valid
	@XmlElement(name="spatial", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Spatial> getSpatialCoverage() {
		return spatialCoverage;
	}
	
	/**
	 * setSpatialCoverage
	 *
	 * Set the spatial coverage
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param spatialCoverage the spatialCoverage to set
	 */
	public void setSpatialCoverage(List<Spatial> spatialCoverage) {
		this.spatialCoverage = spatialCoverage;
	}
}
