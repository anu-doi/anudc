package au.edu.anu.datacommons.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * AtLeastOneOf
 * 
 * Australian National University Data Commons
 * 
 * Annotation that indicates that there is at least one of the given fields
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
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneOfValidator.class)
@Documented
public @interface AtLeastOneOf {
	/**
	 * fieldNames
	 *
	 * The fields names for which at least one should contain a value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The field names
	 */
	String[] fieldNames();
	
	String message() default "has a missing required field";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default{};
	
	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	@Retention(value=RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		RequiredProperty[] value();
	}
}
