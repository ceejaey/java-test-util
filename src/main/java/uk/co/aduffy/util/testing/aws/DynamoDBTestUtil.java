package uk.co.aduffy.util.testing.aws;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import cloud.localstack.TestUtils;
import uk.co.aduffy.util.model.Versioned;
import uk.co.aduffy.util.testing.JSonTestUtil;

public final class DynamoDBTestUtil {

	private DynamoDBTestUtil() {
		// Private Constructor
	}
	
	public static AmazonDynamoDB getDynamoDBConnection() {
		return TestUtils.getClientDynamoDB();
	}
	
	/**
 	 * Evaluates how many matching items are present in the data store without retrieving them.
 	 * 
	 * @param entityClass to count
	 * @return count of how many matching items are present within the specified table
	 * @deprecated developers should be encouraged to specify their own 
	 * 				managed connections to DynamoDb rather than rely on inbuilt 
	 * 				test container connection provider.
	 */
	@Deprecated
	public static int countItemsInTable(Class<?> entityClass) {
		DynamoDBMapper m = new DynamoDBMapper(getDynamoDBConnection());
		return m.count(entityClass, new DynamoDBScanExpression());
	}
	
	/**
	 * Evaluates how many matching items are present in the data store without retrieving them.
	 * 
	 * @param connection to DynamoDb data source
	 * @param entityClass to count
	 * @return count of how many matching items are present within the specified table
	 */
	public static int countItemsInTable(AmazonDynamoDB connection, Class<?> entityClass) {
		DynamoDBMapper m = new DynamoDBMapper(connection);
		return m.count(entityClass, new DynamoDBScanExpression());
	}

	/**
	 * Constructs a basic table structure for a DynamoDB table for the specified entity class.
	 * 
	 * @param connection
	 * @param entityClass
	 */
	public static void createTableFor(AmazonDynamoDB connection, Class<?> entityClass) {
		CreateTableRequest schema = DynamoDBTestUtil.createTableStructureFor(entityClass);
		connection.createTable(schema);
	}
	
	/**
	 * Constructs a basic table structure request for a DynamoDB table for the specified entity class.
	 * 
	 * @param entityClass which has been annotated with DynamoDB attributes
	 * @return
	 */
	public static CreateTableRequest createTableStructureFor(Class<?> entityClass) {
		checkSupportedEntityClass(entityClass);

		DynamoDBTable entityTable = entityClass.getAnnotation(DynamoDBTable.class);
		CreateTableRequest tt = new CreateTableRequest();
		tt.setTableName(entityTable.tableName());
		tt.setBillingMode("PAY_PER_REQUEST");
		tt.setAttributeDefinitions(new ArrayList<>());
		tt.setKeySchema(new ArrayList<>());

		for (Field f : entityClass.getDeclaredFields()) {
			if (f.isAnnotationPresent(DynamoDBHashKey.class) && f.getAnnotation(DynamoDBHashKey.class) != null) {
				AttributeDefinition a = parseFieldAttribute(f);
				tt.getAttributeDefinitions().add(a);
				tt.getKeySchema().add(new KeySchemaElement(a.getAttributeName(), KeyType.HASH));
			} else if (f.isAnnotationPresent(DynamoDBRangeKey.class) && f.getAnnotation(DynamoDBRangeKey.class) != null) {
				AttributeDefinition a = parseFieldAttribute(f);
				tt.getAttributeDefinitions().add(a);
				tt.getKeySchema().add(new KeySchemaElement(a.getAttributeName(), KeyType.RANGE));
			}
		} 
		return tt;	
	}
	
	/**
	 * Asserts that the specified entity class has sufficient attributes defined, 
	 * to allow it to specify a DynamoDB Table
	 * 
	 * @param entityClass to test
	 */
	private static void checkSupportedEntityClass(Class<?> entityClass) {
		if (!entityClass.isAnnotationPresent(DynamoDBTable.class)) {
			throw new IllegalArgumentException("Provided entity class does not define dynamodb metadata");
		} else {
			List<DynamoDBHashKey> hashKeys = new ArrayList<>();
			List<DynamoDBRangeKey> rangeKeys = new ArrayList<>();
			
			for (Field f : entityClass.getDeclaredFields()) {
				if (f.getAnnotation(DynamoDBHashKey.class) != null) {
					hashKeys.addAll(Arrays.asList(f.getAnnotationsByType(DynamoDBHashKey.class)));
				} else if (f.getAnnotation(DynamoDBRangeKey.class) != null) {
					rangeKeys.addAll(Arrays.asList(f.getAnnotationsByType(DynamoDBRangeKey.class)));
				}
			}
			
			if (hashKeys.isEmpty()) {
				throw new IllegalArgumentException("Specified object has no hash key definitions");
			} else if (hashKeys.size() > 1) {
				throw new IllegalArgumentException("Specified object has multiple hash key definitions");
			}

			if (rangeKeys.size() > 1) {
				throw new IllegalArgumentException("Specified object has multiple range key definitions");
			}
		}
	}

	private static AttributeDefinition parseFieldAttribute(Field field) {
		String fieldName = field.getName();
		if (field.isAnnotationPresent(DynamoDBRangeKey.class) && !field.getAnnotation(DynamoDBRangeKey.class).attributeName().isEmpty()) {
			fieldName = field.getAnnotation(DynamoDBRangeKey.class).attributeName();
		} else if (field.isAnnotationPresent(DynamoDBHashKey.class) && !field.getAnnotation(DynamoDBHashKey.class).attributeName().isEmpty()) {
			fieldName = field.getAnnotation(DynamoDBHashKey.class).attributeName();
		} else if (field.isAnnotationPresent(DynamoDBAttribute.class) && !field.getAnnotation(DynamoDBAttribute.class).attributeName().isEmpty()) {
			fieldName = field.getAnnotation(DynamoDBAttribute.class).attributeName();
		}

		ScalarAttributeType type = ScalarAttributeType.S;
		if (Double.class.equals(field.getType()) || Integer.class.equals(field.getType()) || Float.class.equals(field.getType())) {
			type = ScalarAttributeType.N;
		}
		return new AttributeDefinition(fieldName, type);
	}	
	
	/**
	 * Parses the specified class hierarchy and identifies the table name associated with a @DynamoDBTable annotation.
	 * 
	 * @param entityClass
	 * @throws IllegalArgumentExeption if no annotation is present, or if multiple conflicting annotations are present
	 * @return name of table specified by entity class annotations
	 */
	public static String getDynamoTableNameFor(Class<?> entityClass) {
		DynamoDBTable[] tableDef = entityClass.getAnnotationsByType(DynamoDBTable.class);
		//Note that need to actually make this traverse the entire class hierarchy rather than this specific instance
		if (tableDef.length == 0) {
			throw new IllegalArgumentException("Specified object has no table definition");
		} else if(tableDef.length > 1) {
			throw new IllegalArgumentException("Specified object has multiple table definitions");
		}
		return tableDef[0].tableName();
	}
	
	/**
	 * Drops the DynamoDB table which is identified with the provided class
	 * @param entityClass
	 * @deprecated developers should be encouraged to specify their own 
	 * 				managed connections to DynamoDb rather than rely on inbuilt 
	 * 				test container connection provider.
	 */
	@Deprecated
	public static void deleteTableFor(Class<?> entityClass) {
		deleteTableFor(getDynamoDBConnection(), entityClass);
	}

	public static void deleteTableFor(AmazonDynamoDB connection, Class<?> entityClass) {
		DynamoDBTable entityTable = entityClass.getAnnotation(DynamoDBTable.class);
		if (connection.listTables().getTableNames().contains(entityTable.tableName())) {
			connection.deleteTable(entityTable.tableName());
		}

	}
	
	public static <T> void loadTestData(AmazonDynamoDB connection, Class<T> clazz, String resource) {
		T item = JSonTestUtil.loadJsonTestResource(resource, clazz);
		if (item instanceof Versioned) {
			Versioned v = ((Versioned)item);
			if (v.getVersion() != null) {
				v.setVersion(v.getVersion()-1);
			}
		}

		new DynamoDBMapper(connection).save(
			item, 
			DynamoDBMapperConfig.SaveBehavior.CLOBBER.config()
		);
	}
	
	public static void loadBatchTestData(AmazonDynamoDB connection, Class<?> clazz, String resource) {
		Object[] items = (Object[]) JSonTestUtil.loadJsonTestResource(resource, clazz);
		for (Object item: items) {
			if (item instanceof Versioned) {
				Versioned v = ((Versioned)item);
				if (v.getVersion() != null) {
					v.setVersion(v.getVersion()-1);
				}
			}
			
			//This would be better to batch load, but this would
			//mean we need to start batching up into groups of 25
			//If performance testing show this, is where to focus first
			new DynamoDBMapper(connection).save(
				item, 
				DynamoDBMapperConfig.SaveBehavior.CLOBBER.config()
			);
		}
	}
	
}
