package uk.co.aduffy.util.testing.aws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;

import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import uk.co.aduffy.util.testing.DataSet;

@RunWith(LocalstackTestRunner.class)
@ExtendWith({ 
	LocalstackDockerExtension.class, 
	LocalStackDynamoDBDataPopulatorCallback.class 
})
@LocalstackDockerProperties(
	services = { "dynamodb" },
	useSingleDockerContainer = true
)
public class DynamoDBDataPopulationTest {

	@Test
	@DataSet(entity = SampleTestItem.class)
	public void testEmptyLoad() {
		Assertions.assertEquals(
			0, 
			DynamoDBTestUtil.countItemsInTable(TestUtils.getClientDynamoDB(), SampleTestItem.class)
		);
	}
	
	@Test
	@DataSet(entity = SampleTestItem.class, source = "/data/test/testitem/single.json")
	public void testSingleLoad() {
		Assertions.assertEquals(
			1, 
			DynamoDBTestUtil.countItemsInTable(TestUtils.getClientDynamoDB(), SampleTestItem.class)
		);
	}
	
	@Test
	@DataSet(entity = SampleTestItem.class, source = "/data/test/testitem/multiple.json", multiple = true)
	public void testMultipleLoad() {
		Assertions.assertEquals(
			3, 
			DynamoDBTestUtil.countItemsInTable(TestUtils.getClientDynamoDB(), SampleTestItem.class)
		);
	}
	
}
