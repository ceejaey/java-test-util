package uk.co.aduffy.util.testing;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit Extension class which scans the associated test case for instances of @DataSet annotated classes
 * If this is found, it will setup the data store for the specified class and if present, populate a sample data set.

 * @author Andrew Duffy
 *
 */
public abstract class DataSetPopulator implements BeforeEachCallback, AfterEachCallback  {

	/**
	 * Constructs a default data set populator
	 */
	public DataSetPopulator() {
		super();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		Optional<AnnotatedElement> element = context.getElement();
		if (element.isPresent()) {
			AnnotatedElement c = element.get();
			if (c.isAnnotationPresent(DataSet.class)) {
				setupDataStore(c);
				populateDataStore(c);
			}
		} else {
			throw new IllegalArgumentException("Provided entity context does not provide lifecycle element");
		}
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Optional<AnnotatedElement> element = context.getElement();
		if (!element.isPresent()) {
			throw new IllegalArgumentException("Provided entity context does not provide lifecycle element");
		}
		AnnotatedElement e = element.get();
		DataSet annotation = e.getAnnotation(DataSet.class);
		tearDownDataStore(annotation.entity());
	}
	

	protected abstract void setupDataStore(AnnotatedElement c);

	protected abstract void populateDataStore(AnnotatedElement c);

	protected abstract void tearDownDataStore(Class<?> entity);

}