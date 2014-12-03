package ambit2.rest.dataset.filtered;

import net.idea.modbcum.r.QueryReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.QueryCountProtocolApplications;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.rest.OpenTox;
import ambit2.rest.bundle.BundleStudyJSONReporter;

public class StudySearchResource extends StatisticsResource<SubstanceByCategoryFacet, QueryCountProtocolApplications> {
	public static final String resource = "/study";
	protected SubstanceEndpointsBundle bundle = null;
	public StudySearchResource() {
		super();
		mode = StatsMode.protocol_applications;
		setHtmlbyTemplate(true);
	}
	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.protocol_applications;
	}
	@Override
	public String getTemplateName() {
		return super.getTemplateName();
	}
	@Override
	protected QueryCountProtocolApplications createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Object bundleURI = OpenTox.params.bundle_uri.getFirstValue(getParams());
		if (bundleURI!=null) {
			Integer idbundle = getIdBundle(bundleURI, request);
			bundle = new SubstanceEndpointsBundle(idbundle);
		}	
		return super.createQuery(context, request, response);
	}
	@Override
	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		if (queryObject instanceof QueryCountProtocolApplications) {
			if (((QueryCountProtocolApplications)queryObject).getBundle()!=null)
				bundle = ((QueryCountProtocolApplications)queryObject).getBundle();
		}
		return new BundleStudyJSONReporter(request,jsonp,bundle);
	}
}
