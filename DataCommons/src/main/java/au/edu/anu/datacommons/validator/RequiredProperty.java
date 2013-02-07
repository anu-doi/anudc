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

package au.edu.anu.datacommons.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * RequiredProperty
 * 
 * Australian National University Data Commons
 * 
 * Class to specify that at least one of the properties have been given a value
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredPropertyValidator.class)
@Documented
public @interface RequiredProperty {
	/**
	 * The field of the class to verify
	 */
	String subFieldName();
	
	/**
	 * The expected values for at least one of which there should be a value
	 */
	String[] expectedValues();
	
	String message() default "has a missing required property";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Retention(value=RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		RequiredProperty[] value();
	}
}
