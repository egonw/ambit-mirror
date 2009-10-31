package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

public class QueryDatasetResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	public final static String datasetName =  String.format("%s/{dataset_name}",DatasetsResource.datasets);

	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		RetrieveDatasets query = new RetrieveDatasets();
		Object name = request.getAttributes().get("dataset_name");
		if (name != null)
			query.setValue(new SourceDataset(Reference.decode(name.toString())));

		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new DatasetsXMLReporter(getRequest()));	
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor(
					new DatasetsHTMLReporter(getRequest(),true),MediaType.TEXT_HTML);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest()) {
				@Override
				public void processItem(SourceDataset dataset) throws AmbitException {
					super.processItem(dataset);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
		} else 
			return new OutputStreamConvertor(
					new DatasetsHTMLReporter(getRequest(),true),MediaType.TEXT_HTML);
	}		
		//return new DocumentConvertor(new DatasetReporter(getRequest().getRootRef()));


}
