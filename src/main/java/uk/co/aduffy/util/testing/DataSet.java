package uk.co.aduffy.util.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks the presence of a data set which should be pre-loaded prior to executing of test case.
 * @author Andrew Duffy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSet {

	/**
	 * Data Entity represented by test data set.
	 * @return class encapsulated by data set
	 */
	public Class<?> entity();
	
	/**
	 * Resource path to data set accessible on classpath.
	 * @return Resource path to data set accessible on classpath.
	 */
	public String source() default "";
	
	/**
	 * Indicates if the data set contains multiple instances of the represented entity.
	 * @return true if multiple occurrences exist, false if only one is represented.
	 */
	public boolean multiple() default false;
	
}
