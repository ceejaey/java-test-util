package uk.co.aduffy.util.testing.aws;

import java.lang.reflect.AnnotatedElement;

import uk.co.aduffy.persistance.aws.DynamoDBDataSource;
import uk.co.aduffy.util.testing.DataSet;
import uk.co.aduffy.util.testing.DataSetPopulator;

/**
 * Implementation of DataSetPopulator which targets a DynamoDB implementation.
 * 
 * @author Andrew Duffy
 *
 */
public class DynamoDBDataPopulatorCallback extends DataSetPopulator {

	private DynamoDBDataSource dataSource;
	
	public DynamoDBDataPopulatorCallback(DynamoDBDataSource dataSource) {
		//Default Constructor
		this.setDynamoDBDataSource(dataSource);
	}

	public DynamoDBDataSource getDynamoDBDataSource() {
		return dataSource;
	}
	
	public void setDynamoDBDataSource(DynamoDBDataSource dataSource) {
		this.dataSource = dataSource;
	}
		
	@Override
	public void setupDataStore(AnnotatedElement e) {
		DataSet dataSet = e.getAnnotation(DataSet.class);
		DynamoDBTestUtil.createTableFor(
			getDynamoDBDataSource().getConnection(), 
			dataSet.entity()
		);
	}

	@Override
	protected void populateDataStore(AnnotatedElement c) {
		DataSet dataSet = c.getAnnotation(DataSet.class);
		if (dataSet.source() == null || dataSet.source().trim().isEmpty()) {
			return;
		}

		if (dataSet.multiple()) {
			DynamoDBTestUtil.loadBatchTestData(
				getDynamoDBDataSource().getConnection(), 
				dataSet.entity().arrayType(), 
				dataSet.source()
			);
		} else {
			DynamoDBTestUtil.loadTestData(
				getDynamoDBDataSource().getConnection(), 
				dataSet.entity(), 
				dataSet.source()
			);
		}
	}

	@Override
	protected void tearDownDataStore(Class<?> entityClass) {
		DynamoDBTestUtil.deleteTableFor(
			getDynamoDBDataSource().getConnection(), 
			entityClass
		);
	}
	
}
