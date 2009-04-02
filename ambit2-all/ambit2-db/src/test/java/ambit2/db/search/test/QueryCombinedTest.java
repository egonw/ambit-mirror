package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.BitSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.SourceDataset;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.QueryDataset;
import ambit2.db.search.QueryFieldNumeric;
import ambit2.db.search.QueryParam;
import ambit2.db.search.QuerySimilarityBitset;
import ambit2.db.search.QueryStored;
import ambit2.db.search.QueryStructure;
import ambit2.db.search.QueryStructureByID;
import ambit2.db.search.StringCondition;

public class QueryCombinedTest extends QueryTest<QueryCombined> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	@Test
	public void testStructure() throws Exception {
		
		QueryCombined qc = new QueryCombined();
		qc.setId(55);
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		FingerprintGenerator gen = new FingerprintGenerator();
		BitSet bitset = gen.process(MoleculeFactory.makeAlkane(10));
		q.setBitset(bitset);
		
		Assert.assertNotNull(q.getParameters().get(1).getValue());
		qc.add(q);
		
	}
	
	@Test
	public void testDataset() throws Exception {
		
		QueryCombined qc = new QueryCombined();
		qc.setId(55);
		QueryStructure qs = new QueryStructure();
		qs.setFieldname("idchemical");
		qs.setValue("10");
		QueryDataset dataset = new QueryDataset();
		dataset.setValue(new SourceDataset("Dataset 1"));
		qc.setScope(dataset);
		qc.add(qs);
		System.out.println(qc.getSQL());
		
	}	
	@Test
	public void test() throws Exception {
		QueryStored qs = new QueryStored();
		qs.setName("test");
		
		QueryCombined qc = new QueryCombined();
		qc.setId(55);
		QueryStructureByID q = new QueryStructureByID(100);
		q.setCondition(NumberCondition.getInstance("<="));
		
		Assert.assertNotNull(q.getParameters().get(1).getValue());
		qc.add(q);

		//between 150 and 200
		QueryStructureByID q1 = new QueryStructureByID(150,200);
		Assert.assertNotNull(q1.getParameters().get(1).getValue());
		Assert.assertNotNull(q1.getParameters().get(2).getValue());		
		qc.add(q1);
		

		qc.setCombine_as_and(false);
		
		Assert.assertEquals("select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure <= ?\nunion\nselect ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure between ? and ?",
				qc.getSQL());
		
		Assert.assertNotNull(q.getParameters().get(1).getValue());

		
		List<QueryParam> params = qc.getParameters();
		Assert.assertNotNull(params);
		
		Assert.assertEquals(5,params.size());
		int[] values = {55,100,55,150,200};
		for (int i=0; i < params.size(); i++) {
			Assert.assertEquals(Integer.class,params.get(i).getType());
			Assert.assertEquals(values[i],params.get(i).getValue());
		}

		qc.setScope(qs);
		
		/*
		qc.setScope(null);
		qc.setCombine_as_and(true);
		assertEquals("select Q1.idquery,s.idchemical,idstructure,Q1.selected as selected,Q1.metric as metric from structure as s\njoin\n(select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure <= ?)\nas Q1\nusing (idstructure)\njoin\n(select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure between ? and ?)\nas Q2\nusing (idstructure)",qc.getSQL());
		System.out.println(qc.getSQL());
		
		QueryStored qs = new QueryStored();
		qs.setName("test");
		
		qc.setScope(qs);
		System.out.println(qc.getSQL());
		*/
	}
	@Override
	protected QueryCombined createQuery() throws Exception {
		QueryCombined q = new QueryCombined();
		QueryFieldNumeric d = new QueryFieldNumeric();
		d.setCondition(NumberCondition.getInstance(NumberCondition.between));
		d.setValue(new Double(10));
		d.setMaxValue(new Double(15));
		d.setFieldname("Property 1");
		
		QueryStructure qf = new QueryStructure();
		qf.setFieldname("smiles");
		qf.setValue("[Br-].c1ccc(cc1)[P+](c2ccccc2)(c3ccccc3)CC");
		qf.setCondition(StringCondition.getInstance("="));
		
		q.setScope(null);
		q.setCombine_as_and(true);
		q.add(d);
		q.add(qf);
		return q;
	}
	@Override
	protected void verify(QueryCombined query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(11,rs.getInt(2));
			Assert.assertEquals(100215,rs.getInt(3));
			Assert.assertEquals(1,rs.getInt(4));
			Assert.assertEquals(1,rs.getInt(5));			

		}
		Assert.assertEquals(1,records);
		
	}
}
