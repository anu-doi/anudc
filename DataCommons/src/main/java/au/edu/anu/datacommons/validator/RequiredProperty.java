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
