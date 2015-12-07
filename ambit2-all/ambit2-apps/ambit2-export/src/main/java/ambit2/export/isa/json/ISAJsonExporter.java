package ambit2.export.isa.json;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.IISAExport;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.v1_0.ISAJsonExporter1_0;



public class ISAJsonExporter implements IISAExport
{
	
	protected ISAVersion isaVersion = ISAVersion.Ver1_0;  //default
		
	//Basic io variables
	Iterator<SubstanceRecord> records = null;
	SubstanceEndpointsBundle endpointBundle = null;
	File outputDir = null;
	File exportConfig = null;
	File xmlISAConfig =  null;
	
	public ISAJsonExporter()
	{	
	}
	
	public ISAJsonExporter(	
			ISAVersion isaVersion,
			Iterator<SubstanceRecord> records, 
			File outputDir, 
			File exportConfig,
			SubstanceEndpointsBundle endpointBundle
			)
	{
		this.isaVersion = isaVersion;
		setRecords(records);
		setOutputDir(outputDir);
		setExportJsonConfig(exportConfig);
		setEndpointBundle(endpointBundle);
	}

	public ISAJsonExporter(	
			Iterator<SubstanceRecord> records, 
			File outputDir, 
			File exportConfig,
			SubstanceEndpointsBundle endpointBundle
			)
	{
		setRecords(records);
		setOutputDir(outputDir);
		setExportJsonConfig(exportConfig);
		setEndpointBundle(endpointBundle);
	}
	
	public void export() throws Exception
	{
		switch(isaVersion)
		{
		case Ver1_0:
			ISAJsonExporter1_0 jsonExpV1 = new ISAJsonExporter1_0(
					records, outputDir, exportConfig, 	endpointBundle);
			jsonExpV1.export();
			break;
		case Ver2_0:			
			throw (new Exception("ISA-Json version 2 export not implemented!"));
		}
	}
	
	
	public File getExportJsonConfig() {
		return exportConfig;
	}

	public void setExportJsonConfig(File exportConfig) {
		this.exportConfig = exportConfig;
	}

	public File getXmlISAConfig() {
		return xmlISAConfig;
	}

	public void setXmlISAConfig(File xmlISAConfig) {
		this.xmlISAConfig = xmlISAConfig;
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public Iterator<SubstanceRecord> getRecords() {
		return records;
	}

	public void setRecords(Iterator<SubstanceRecord> records) {
		this.records = records;
	}

	public SubstanceEndpointsBundle getEndpointBundle() {
		return endpointBundle;
	}

	public void setEndpointBundle(SubstanceEndpointsBundle endpointBundle) {
		this.endpointBundle = endpointBundle;
	}
	
	@Override
	public ISAFormat getISAFormat() {
		return ISAFormat.JSON;
	}

	@Override
	public ISAVersion getISAVersion() {
		return isaVersion;
	}
}
