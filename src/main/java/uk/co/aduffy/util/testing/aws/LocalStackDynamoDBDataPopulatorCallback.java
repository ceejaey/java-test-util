package uk.co.aduffy.util.testing.aws;

/**
 * JUnit Extension class which will initialise a DynamoDBDataPopulatorCallback with a connection
 * to a LocalStack provided data source.
 * 
 * @author Andrew Duffy
 *
 */
public class LocalStackDynamoDBDataPopulatorCallback extends DynamoDBDataPopulatorCallback {

	public LocalStackDynamoDBDataPopulatorCallback() {
		super(new DynamoDBLocalStackDataSource());
	}

}
