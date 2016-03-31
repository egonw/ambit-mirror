package ambit2.dbsubstance.test;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.io.DownloadTool;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.dbsubstance.DBSubstanceImport;

/**
<pre>
  -c,--config <file>                    Config file (DB connection
                                       parameters)
 -h,--help                             This help
 -i,--input <file>                     Input file or folder
 -j,--json <file>                      JSON config file
 -m,--clearMeasurements <value>        true|false
 -o,--output <file>                    Output file
 -p,--parser <type>                    File parser mode :
                                       i5z|xlsx|xls|nanowiki
 -r,--maxReferenceSubstances <value>   Maximum reference substances in
                                       *.i5z archive
 -s,--isSplitRecord <value>            true|false
 -t,--clearComposotion <value>         true|false
 -x,--structureMatch <value>           Match structure by
                                       uuid|cas|einecs|smiles|inchi
</pre>
 *
 */
public class I5ZImportTest extends DbUnitTest {
	@Test
	public void test() throws Exception {
		
		setUpDatabaseFromResource("ambit2/db/processors/test/empty-datasets.xml");
		String resource_i5 = "net/idea/i5/_5/substance/i5z/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734.i5z";
		InputStream in = net.idea.i5._5.ambit2.I5AmbitProcessor.class
				.getClassLoader().getResourceAsStream(resource_i5);
		Assert.assertNotNull(in);
		File file = fromResourcestream(in, ".i5z");
		System.out.println(file);

		String resource_config = "ambit2/db/conf/test.properties";

		in = DbUnitTest.class.getClassLoader().getResourceAsStream(resource_config);
		File fileconfig = fromResourcestream(in, ".properties");
		System.out.println(fileconfig);

		String[] args = new String[] { "-i", file.getAbsolutePath(), "-c", fileconfig.getAbsolutePath(), "-m", "true",
				"-t", "true" ,"-r","-1"};
		DBSubstanceImport.main(args);
		
		IDatabaseConnection c = getConnection();
		ITable table  = c
				.createQueryTable("EXPECTED_SUBSTANCES", "SELECT * FROM substance");
		Assert.assertEquals(1, table.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_STRUCTURES",
				"SELECT * FROM structure");
		Assert.assertEquals(6, values.getRowCount());
		c.close();

	}

	private File fromResourcestream(InputStream in, String ext)
			throws Exception {
		File file;
		try {
			file = File.createTempFile("test", ext);
			file.deleteOnExit();
			DownloadTool.download(in, file);
			Assert.assertTrue(file.exists());
			return file;
		} catch (Exception x) {
			throw x;
		} finally {
			in.close();
		}

	}
}