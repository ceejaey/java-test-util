package uk.co.aduffy.util.testing;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;

public class TestUtil {

	private TestUtil() {
		// Default private constructor
	}
	
	public static <T> void assertSame(T a, T b) {
		Assertions.assertEquals(a, a);
		Assertions.assertEquals(a, b);
		Assertions.assertEquals(b, a);
	}
	
	public static <T, R>  void testProperty(T a, T b, BiConsumer<T, R> f, Function<T, R> g, R value, R other) {
		f.accept(a, value);
		f.accept(b, value);
		Assertions.assertEquals(value, g.apply(a));
	}
	
	public static <T, R>  void testSetterValue(T a, T b, BiConsumer<T, R> f, Function<T, R> g, R value, R other) {
		assertSame(a, b);

		f.accept(a, value);
		f.accept(b, value);
		Assertions.assertEquals(value, g.apply(a));
		
		Assertions.assertTrue(a.equals(b));
		Assertions.assertTrue(b.equals(a));

		f.accept(a, value);
		f.accept(b, other);
		Assertions.assertFalse(a.equals(b));
		Assertions.assertFalse(b.equals(a));

		f.accept(a, value);
		f.accept(b, null);
		Assertions.assertFalse(a.equals(b));
		Assertions.assertFalse(b.equals(a));
		
		f.accept(a, null);
		f.accept(b, other);
		Assertions.assertFalse(a.equals(b));
		Assertions.assertFalse(b.equals(a));

		f.accept(a, null);
		f.accept(b, null);
		Assertions.assertTrue(a.equals(b));
		Assertions.assertTrue(b.equals(a));
		
		assertSame(a, b);
	}
	
}
