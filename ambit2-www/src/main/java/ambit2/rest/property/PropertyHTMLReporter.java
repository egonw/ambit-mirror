package ambit2.rest.property;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

/**
 * HTML for {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyHTMLReporter extends QueryHTMLReporter<Property, IQueryRetrieval<Property>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3196496706491834527L;
	public PropertyHTMLReporter(Request ref,boolean collapsed) {
		super(ref,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new PropertyURIReporter(request);
	}

	@Override
	public Object processItem(Property item) throws AmbitException  {
		try {
			output.write(String.format(
						"<a href=\"%s\">%s %s</a>&nbsp;",
						uriReporter.getURI(item),
						item.getName(),
						item.getUnits()));
			if (!collapsed) {
				output.write(String.format("Same As : <a href='%s'>%s</a>&nbsp;",
						item.getLabel(),
						item.getLabel()));				
				output.write(String.format("Created by : <a href='%s'>%s</a>",
						item.getReference().getURL(),
						item.getReference().getName()));
			}
			output.write("<br>");
		} catch (Exception x) {
			logger.warn(x);
		}		
		return null;
	}
	@Override
	public void header(Writer w, IQueryRetrieval<Property> query) {
		super.header(w, query);
		try {w.write(collapsed?"<h3>Feature</h3>":"<h3>Features</h3>");} catch (Exception x) {}
	}

}
