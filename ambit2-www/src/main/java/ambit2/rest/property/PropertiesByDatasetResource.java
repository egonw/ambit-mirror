package ambit2.rest.property;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.base.data.Property;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.PropertiesByDataset;
import ambit2.rest.StatusException;
import ambit2.rest.dataset.DatasetsResource;

/**
 * Retrieves feature definitions by dataset 
<pre>
	/dataset/{id}/feature_definition
</pre>
 * @author nina
 *
 */
public class PropertiesByDatasetResource extends PropertyResource {
	public final static String DatasetFeaturedefID = String.format("%s%s/{%s}",DatasetsResource.datasetID,featuredef,idfeaturedef);
	public final static String DatasetFeaturedef = String.format("%s%s",DatasetsResource.datasetID,featuredef);

	public PropertiesByDatasetResource(Context context, Request request,
			Response response) {
		super(context, request, response);
	}
	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws StatusException {
		Object id = request.getAttributes().get(DatasetsResource.datasetKey);
		collapsed = true;
		PropertiesByDataset q = new PropertiesByDataset();
		SourceDataset dataset = new SourceDataset();
		if (id != null) try {
			dataset.setId(new Integer(Reference.decode(id.toString())));
			q.setFieldname(null);
			q.setValue(dataset);
			collapsed = false;
		} catch (NumberFormatException x) {
			dataset.setName(id.toString());
			q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
			dataset.setId(-1);
			q.setValue(dataset);
		} catch (Exception x) {
			throw new StatusException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,x));
		}
		else {
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key != null) {
				Property property = new Property(Reference.decode(key.toString()));
				q.setFieldname(property);
				
			} 
		}
		//feature definition
		Object fid = request.getAttributes().get(idfeaturedef);
		if (fid != null) try {
			Property p = new Property(null);
			p.setId(new Integer(Reference.decode(fid.toString())));
			q.setFieldname(p);
		} catch (Exception x) {
			//do nothing
		}
		return q;
	}
}
