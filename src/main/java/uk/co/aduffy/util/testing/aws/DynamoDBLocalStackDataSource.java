package uk.co.aduffy.util.testing.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import cloud.localstack.TestUtils;
import uk.co.aduffy.persistance.aws.DynamoDBDataSource;

/**
 * Utility class which can substitute a connection to DynamoDB for the
 * running LocalStack instance
 * 
 * @author Andrew Duffy
 *
 */
public class DynamoDBLocalStackDataSource extends DynamoDBDataSource {

	@Override
	public AmazonDynamoDB getConnection() {
		return TestUtils.getClientDynamoDB();
	}
	
}
