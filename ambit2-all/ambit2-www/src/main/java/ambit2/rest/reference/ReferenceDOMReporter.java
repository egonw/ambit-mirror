package ambit2.rest.reference;

import org.restlet.data.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.reference.ReadReference;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.XMLTags;

/**
 * Generates XML for {@link ReferenceResource}
<pre>
  <?xml version="1.0" encoding="UTF-8" ?> 
 <schema xmlns="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.opentox.org/Reference/1.0" xmlns:tns="http://www.opentox.org/Reference/1.0" elementFormDefault="qualified">
 <complexType name="Reference">
  <attribute name="ID" type="string" use="required" /> 
  <attribute name="Name" type="string" use="required" /> 
  <attribute name="AlgorithmID" type="string" use="optional" /> 
  <attribute name="Parameters" type="string" use="optional" /> 
  <attribute name="ExperimentalProtocol" type="string" use="optional" /> 
  </complexType>
  </schema>
</pre>
 * @author nina
 *
 */
public class ReferenceDOMReporter extends QueryDOMReporter<ILiteratureEntry, ReadReference> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4039338676682812945L;

	public ReferenceDOMReporter(Request reference) {
		super(reference);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
		return new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(reference);
	}

	@Override
	public void footer(Document output, ReadReference query) {
		
	}

	@Override
	public void header(Document doc, ReadReference query) {

		doc.appendChild(doc.createElementNS(XMLTags.ns_opentox_reference,XMLTags.node_references));
	}

	@Override
	public void processItem(ILiteratureEntry item) throws AmbitException  {
		   NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox_reference, XMLTags.node_references);
		   for (int i=0; i < parent.getLength();i++)
		        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
		                parent.item(i).appendChild(getItemElement(output, item));
		        		break;
		   }		

		
	}
	@Override
	public Element getItemElement(Document doc, ILiteratureEntry item) {
		Element e = doc.createElementNS(XMLTags.ns_opentox_reference,XMLTags.node_reference);
        e.setAttribute(XMLTags.attr_id,Integer.toString(item.getId()));
        e.setAttribute(XMLTags.attr_name,item.getName());
        e.setAttribute("AlgorithmID", item.getURL());
        //e.appendChild(getURIElement(doc, item));
        return e;
	}

}
