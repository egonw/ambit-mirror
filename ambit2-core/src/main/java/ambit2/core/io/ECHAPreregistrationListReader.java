package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.xmlcml.cml.tools.MoleculeTool;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.CASProcessor;

/**
 * Reads preregistration list in XML format as in http://apps.echa.europa.eu/preregistered/prsDownload.aspx
 * @author nina
 *
 */
public class ECHAPreregistrationListReader extends
		DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord> {
	protected XMLStreamReader reader ;
	protected static String ECHA_URL="http://apps.echa.europa.eu/preregistered/prsDownload.aspx";
	protected static String ECHA_REFERENCE="ECHA";
	protected ArrayList<String> synonyms = new ArrayList<String>();
	protected IStructureRecord record;
	protected String tmpValue="";	
	protected CASProcessor casProcessor = new CASProcessor();
	protected Property casProperty = Property.getInstance(CDKConstants.CASRN,
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL)); 
	protected Property ecProperty = Property.getInstance("EC",
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL));
	protected Property nameProperty = Property.getInstance(CDKConstants.NAMES,
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL));
	protected Property registrationProperty = Property.getInstance(echa_tags.REGISTRATION_DATE.toString(),
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL));
	protected enum echa_tags {
		dataroot,
		PRE_REGISTERED_SUBSTANCE,
		EC_NUMBER,
		CAS_NUMBER,
		NAME,
		REGISTRATION_DATE,
		RELATED_SUBSTANCE,
		RELATED_CAS_NUMBER,
		RELATED_EC_NUMBER,
		RELATED_NAME,
		SYNONYM,
		SYNONYM_NAME,
		SYNONYM_LANGUAGE,
		NONE
	}
	
    public ECHAPreregistrationListReader(InputStream in) {
    	record = new StructureRecord();
    	record.setFormat(MOL_TYPE.SDF.toString());
    	record.setContent("");
    	try {
    		reader =   XMLInputFactory.newInstance().createXMLStreamReader(in,"UTF-8");
    	} catch (Exception x) {
    		reader = null;
    		x.printStackTrace();
    	}
    }	
	
	public void close() throws IOException {
		try {
			reader.close();
		} catch (XMLStreamException x) {
			throw new IOException(x.getMessage());
		}

	}

	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext() {
		try {
		    while (reader.hasNext()) {
		    	int type = reader.next();
	            switch (type) {
	            case XMLStreamConstants.START_ELEMENT: {
	    			echa_tags tag = echa_tags.valueOf(reader.getName().getLocalPart());
	    			switch (tag) {
	    			case PRE_REGISTERED_SUBSTANCE: { 
	    				record.clear();
	    				synonyms.clear();
	    				break;
	    			}
	    			}            	
	            	break;
	            }
	            case XMLStreamConstants.END_DOCUMENT: {
	            	return false;
	            }
	            case XMLStreamConstants.END_ELEMENT: {
	        		echa_tags tag = echa_tags.valueOf(reader.getName().getLocalPart());
	        		switch (tag) {
	        		case PRE_REGISTERED_SUBSTANCE: { 
	        			for (int i=0;i < synonyms.size();i++)
	        				record.setProperty(
	        						Property.getInstance(CDKConstants.NAMES,
	        						LiteratureEntry.getInstance(String.format("%s %s#%d",ECHA_REFERENCE,echa_tags.SYNONYM.toString(),i+1, 
	        								ECHA_URL),ECHA_URL))
	        						,synonyms.get(i));
	
	        			
	        			return true;
	        		}
	        		case NAME : {
	        			record.setProperty(nameProperty,tmpValue);
	        			break;				
	        		}
	        		case EC_NUMBER: {
	        			record.setProperty(ecProperty,tmpValue);
	        			break;
	        		}			
	        		case CAS_NUMBER: {
	        			try {
	        				record.setProperty(casProperty,casProcessor.process(tmpValue));
	        			} catch (Exception x) {
	            			record.setProperty(casProperty,tmpValue);
	        			}
	        			break;
	        		}
	        		case REGISTRATION_DATE: {
	        			record.setProperty(registrationProperty,tmpValue);
	        			break;
	        		}	
	        		case SYNONYM_NAME: {
	        			synonyms.add(tmpValue);
	        			break;			
	        		}
	        		default: {
	        			tmpValue = null;
	        		}
	        		}
	        		break;
	            }
	            case XMLStreamConstants.CHARACTERS: {
	            	 
	            	tmpValue = reader.getText().trim();

	            	break;
	            }
	            }
		    	
		    }
		} catch (XMLStreamException x) {
			x.printStackTrace();

		}
		return false;
	}

	public Object next() {
		return record;
	}

	public IStructureRecord nextRecord() {
		return record;
	}

	
	public void parseDocument() throws Exception {

	}
}
