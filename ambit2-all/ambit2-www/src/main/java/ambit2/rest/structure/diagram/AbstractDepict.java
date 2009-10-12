package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.AbstractReporter;
import ambit2.rest.AmbitResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.EmptyMoleculeException;

/**
 * Returns PNG given a smiles
 * @author nina
 *
 */
public class AbstractDepict extends Resource {

	protected String smiles = null;
	public AbstractDepict(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
	
	}
	protected BufferedImage getImage(String smiles,int width,int height) throws AmbitException {
		return null;
	}
	protected String getTitle(Reference ref, String smiles) {
		StringBuilder b = new StringBuilder();
		b.append(String.format("SMILES %s<br>",	smiles==null?"":smiles));
		b.append("<table width='100%'><tr>");
		b.append(String.format("<td><a href='%s/daylight?search=%s'>%s</a></td><td><a href='%s/cdk?search=%s'>%s</a></td>",
				ref.getHierarchicalPart(),smiles,"Daylight depiction",ref.getHierarchicalPart(),smiles,"CDK depiction"));		
		b.append("</tr><tr>");
		
		b.append(String.format("<td><img src='%s/daylight?search=%s' alt='%s' title='%s'></td><td><img src='%s/cdk?search=%s' alt='%s' title='%s'></td>",
				ref.getHierarchicalPart(),smiles,smiles,smiles,ref.getHierarchicalPart(),smiles,smiles,smiles));
		b.append("</tr></table>");
		return b.toString();
	}
	public Representation getRepresentation(Variant variant) {

		try {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			int w = 400; int h = 200;
			try { w = Integer.parseInt(form.getFirstValue("w"));} catch (Exception x) {w =400;}
			try { h = Integer.parseInt(form.getFirstValue("h"));} catch (Exception x) {h =200;}
			smiles = form.getFirstValue("search");		
        	
	    		if(variant.getMediaType().equals(MediaType.TEXT_HTML)) {
	    			StringConvertor convertor = new StringConvertor(new AbstractReporter<String,Writer>() {
	    				public void close() throws Exception {};
	    				public Writer process(String target) throws AmbitException {
	    					try {
	    					AmbitResource.writeHTMLHeader(getOutput(), smiles, getRequest().getRootRef());
	    					getOutput().write(target);
	    					AmbitResource.writeHTMLFooter(getOutput(), smiles, getRequest().getRootRef());
	    					} catch (Exception x) {}
	    					return getOutput();
	    				};
	    			});
	    			return convertor.process(getTitle(getRequest().getOriginalRef(),smiles));
	    		}
					    		
	    	if (smiles != null) {
	        	final BufferedImage image = getImage(smiles,w,h);
	        	if (image ==  null) {
		        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid smiles %s",smiles));
	        		return null;
	        	}
	        	return new OutputRepresentation(MediaType.IMAGE_PNG) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			ImageIO.write(image, "PNG", out);
	        			out.flush();
	        			out.close();
	        		}
	        	};
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, new EmptyMoleculeException());
	        	return null;   	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,x);
			return null;
		
		}
	}		
}