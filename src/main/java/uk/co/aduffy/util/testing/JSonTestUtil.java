package uk.co.aduffy.util.testing;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uk.co.aduffy.util.model.Versioned;

public class JSonTestUtil {

	private JSonTestUtil() {
		// Default Constructor
	}
	
	public static <T> T loadJsonTestResource(String resourcePath, Class<T> clazz) {
		try (InputStreamReader isr = (new InputStreamReader(clazz.getResourceAsStream(resourcePath)))) {
			Gson g = new GsonBuilder().create();
			return g.fromJson(isr, clazz);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load test data for entity: " + clazz.getCanonicalName(), e);
		}
	}
	
	public static <T> T[] loadBatchTestData(Class<T[]> clazz, String resource) {
		T[] nodes = loadJsonTestResource(resource, clazz);
		for (T item: nodes) {
			if (item instanceof Versioned) {
				Versioned v = ((Versioned)item);
				if (v.getVersion() != null) {
					v.setVersion(v.getVersion() - 1);
				}
			}
		}
		return nodes;
	}

	public static <T> T loadTestData(Class<T> clazz, String resource) {
		T item = loadJsonTestResource(resource, clazz);
		if (item instanceof Versioned) {
			Versioned v = ((Versioned)item);
			if (v.getVersion() != null) {
				v.setVersion(v.getVersion()-1);
			}
		}
		return item;
	}
	
	
}
