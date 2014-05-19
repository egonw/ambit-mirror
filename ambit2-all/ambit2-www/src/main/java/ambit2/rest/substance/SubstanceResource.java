package ambit2.rest.substance;

import java.util.List;

import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskStorage;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.processors.CallableSubstanceI5Query;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.substance.DeleteSubstance;
import ambit2.db.substance.ReadByReliabilityFlags;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.ReadSubstanceByExternalIDentifier;
import ambit2.db.substance.ReadSubstanceByName;
import ambit2.db.update.AbstractUpdate;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.TaskApplication;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.task.AmbitFactoryTaskConvertor;
import ambit2.rest.task.FactoryTaskConvertor;

/**
 * Substances (in the sense of IUCLID5) 
 * @author nina
 *
 * @param <Q>
 */
public class SubstanceResource<Q extends IQueryRetrieval<SubstanceRecord>> extends QueryResource<Q,SubstanceRecord> {
	public final static String substance = OpenTox.URI.substance.getURI();
	public final static String idsubstance = OpenTox.URI.substance.getKey();
	public final static String substanceID = OpenTox.URI.substance.getResourceID();
	enum search_mode {
		reference,
		related
	}
	public SubstanceResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "substance.ftl";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.APPLICATION_JAVASCRIPT,

				});
				
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(getRequest(),getDocumentation(),jsonpcallback);
			return new OutputWriterConvertor<SubstanceRecord, Q>(
					cmpreporter,
					MediaType.APPLICATION_JAVASCRIPT,filenamePrefix);
		} else { //json by default
		//else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(getRequest(),getDocumentation(),null);
			return new OutputWriterConvertor<SubstanceRecord, Q>(
					cmpreporter,
					MediaType.APPLICATION_JSON,filenamePrefix);
		}
	}	

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(idsubstance);
		if (key==null) {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			Object cmpURI = OpenTox.params.compound_uri.getFirstValue(form);
			if (cmpURI!=null) {
				Integer idchemical = getIdChemical(OpenTox.params.compound_uri.getFirstValue(form), request);
				if (idchemical != null) {
					search_mode mode = search_mode.reference;
					try { 
						mode = search_mode.valueOf(form.getFirstValue("type"));
					} catch (Exception x) {}
					switch (mode) {
					case reference: {
						SubstanceRecord substance = new SubstanceRecord();
						substance.setIdchemical(idchemical);
						return (Q)new ReadSubstance(substance);			
					}
					case related: {
						CompositionRelation composition = new CompositionRelation(null,new StructureRecord(idchemical,-1,null,null),null);
						return (Q)new ReadSubstance(composition);								
					}
					}
				} else 
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
		
			String search = form.getFirstValue(QueryResource.search_param);
			if (search!=null) {
				String type = form.getFirstValue("type"); 
				if ("uuid".equals(type)) {
					SubstanceRecord record = new SubstanceRecord();
					record.setCompanyUUID(search.trim());
					return (Q)new ReadSubstance(record);
				} else if ("name".equals(type)) {
					return (Q)new ReadSubstanceByName(type,search);
				} else if ("reliability".equals(type)) {
					return (Q)new ReadByReliabilityFlags(type,search);
				} else if ("purposeFlag".equals(type)) {
					return (Q)new ReadByReliabilityFlags(type,search);
				} else if ("studyResultType".equals(type)) {
					return (Q)new ReadByReliabilityFlags(type,search);
				} else if ("isRobustStudy".equals(type)) {
					return (Q)new ReadByReliabilityFlags(type,search);					
				} else {
					return (Q)new ReadSubstanceByExternalIDentifier(type,search);
				}
			} else
				return (Q)new ReadSubstance();			

		} else 
			try {
			return (Q)new ReadSubstance(new SubstanceRecord(Integer.parseInt(key.toString())));
			} catch (Exception x) {
				int len = key.toString().trim().length(); 
				if ((len > 40) && (len <=45)) {
					SubstanceRecord record = new SubstanceRecord();
					record.setCompanyUUID(key.toString());
					return (Q)new ReadSubstance(record);
				}	
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
	}

	protected Integer getIdChemical(Object cmpURI, Request request) {
		if (cmpURI!=null) {
			Object id = OpenTox.URI.compound.getId(cmpURI.toString(), request.getRootRef());
			if (id!=null && (id instanceof Integer)) 
				return (Integer)id;		
			else {
				Object[] ids = OpenTox.URI.conformer.getIds(cmpURI.toString(), request.getRootRef());
				if (ids!=null && (ids.length>1) && (ids[0] instanceof Integer)) 
					return (Integer)ids[0];
			}
		} 
		return null;
	}
	protected QueryURIReporter getURIReporter() {
		return new SubstanceURIReporter<Q>(getRequest(),getDocumentation());
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		
		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");

		if (entity.getMediaType()!= null)
			if (MediaType.MULTIPART_FORM_DATA.getName().equals(entity.getMediaType().getName())) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
	            RestletFileUpload upload = new RestletFileUpload(factory);
	            try {
		            List<FileItem> items = upload.parseRequest(getRequest());
					String token = getToken();
					CallableSubstanceImporter<String> callable = new CallableSubstanceImporter<String>(
								items, 
								"files[]",
								getRootRef(),
								getContext(),
								new SubstanceURIReporter(getRequest().getRootRef(), null),
								new DatasetURIReporter(getRequest().getRootRef(), null),
								token,true); //assumes i5z split record!
					ITask<Reference,Object> task =  ((ITaskApplication)getApplication()).addTask(
								"Substance import",
								callable,
								getRequest().getRootRef(),
								token);
								
					  ITaskStorage storage = ((ITaskApplication)getApplication()).getTaskStorage();				  
					  FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
					  task.update();
					  getResponse().setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
		              return tc.createTaskRepresentation(task.getUuid(), variant,getRequest(), getResponse(),null);
	            } catch (ResourceException x) {
	            	throw x;
	            } catch (Exception x) {
	            	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
	            }
			} else if (MediaType.APPLICATION_WWW_FORM.getName().equals(entity.getMediaType().getName())) {
				/* web form to update substances from IUCLID5 server
				 * Expected web form fields :
				 * substance: UUID or query URL
				 * type : UUID or query or URL (ambit substance URL)
				 * qa options
				 * query : one of {@link QueryToolClient.PredefinedQuery}
				 * query parameters: depend on the query type
				 * iuclid5 server; credentials - optional, use preconfigured if not submitted
				 * [(option,UUID), (uuid,ZZZZZZZZZZ), (extidtype,CompTox), (extidvalue,Ambit Transfer), (i5server,null), (i5user,null), (i5pass,null)]
				 */

				Form form = new Form(entity);
				String token = getToken();
				CallableSubstanceI5Query<String> callable = new CallableSubstanceI5Query<String>(
						getRootRef(),
						form,
						getContext(),
						new SubstanceURIReporter(getRequest().getRootRef(), null),
						new DatasetURIReporter(getRequest().getRootRef(), null),
						token);
						ITask<Reference,Object> task =  ((TaskApplication)getApplication()).addTask(
						"Retrieve substance from IUCLID5 server",
						callable,
						getRequest().getRootRef(),
						token);
						
				  ITaskStorage storage = ((ITaskApplication)getApplication()).getTaskStorage();				  
				  FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
				  task.update();
				  getResponse().setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
	              return tc.createTaskRepresentation(task.getUuid(), variant,getRequest(), getResponse(),null);				
				
			}
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}
	

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		
		try {
			Object key = getRequest().getAttributes().get(idsubstance);
			if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			String uri = getRequest().getRootRef()+ "/substance/" + key.toString();
			if (!uri.toString().equals(form.getFirstValue("substance_uri"))) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

			SubstanceRecord record = new SubstanceRecord();
			record.setCompanyUUID(key.toString());
			executeUpdate(getRequestEntity(),null,createDeleteObject(record));
			getResponse().setStatus(Status.SUCCESS_OK);
			return new StringRepresentation(String.format("%s/dataset", getRequest().getRootRef()),MediaType.TEXT_URI_LIST);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	
	@Override
	protected AbstractUpdate createDeleteObject(SubstanceRecord entry)
			throws ResourceException {
		DeleteSubstance c = new DeleteSubstance();
		c.setObject(entry);
		return c;
	}	
}
