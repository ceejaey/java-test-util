package uk.co.aduffy.util.testing.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "TestItem")
public class SampleTestItem {

	@DynamoDBHashKey
	private String key;

	@DynamoDBRangeKey
	private String rangeKey;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRangeKey() {
		return rangeKey;
	}

	public void setRangeKey(String rangeKey) {
		this.rangeKey = rangeKey;
	}
	
}
