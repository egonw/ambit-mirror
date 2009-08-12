package ambit2.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.template.OntologyResource;

public class AmbitResource extends Resource {
	protected String[][] uri = {
			
			{DatasetsResource.datasets,"Datasets"},
			{ReferenceResource.reference,"References"},
			{PropertyResource.featuredef,"Feature definitions"},
			{QueryResource.query_resource,"Similarity search"},
			/*
			{"/endpoints","Endpoints"},
			{"/descriptors","Descriptors"},
			*/
			{String.format("%s/%d",CompoundResource.compound,100),"Chemical compounds"},
			{String.format("%s/%d%s/%s",CompoundResource.compound,100,ConformerResource.conformerKey,"all"),"All conformer of a compound"},
			{String.format("%s/%d%s/%d",CompoundResource.compound,100,ConformerResource.conformerKey,100),"Conformer of a compound"},
			
			{String.format("%s/%d%s",CompoundResource.compound,100,PropertyResource.featuredef),"All features, available for a compound"},
			{String.format("%s/%d%s/%d%s",CompoundResource.compound,100,ConformerResource.conformerKey,100,PropertyResource.featuredef),"All features, available for a conformer"},
			
			
			{"/query/similarity/method/fp1024/distance/tanimoto/0.5/smiles/c1ccccc1","Demo similarity search"},
			{"/query/feature/like/benzene","Search by name"},
			{"/query/feature/=/50-00-0","Search by property or an identifier (CAS, Name, etc.)"},
			{String.format("/query/smarts?search=%s",Reference.encode("[NX3][CX3](=[OX1])[#6]")),"Search by SMARTS NX3][CX3](=[OX1])[#6]"},
			{"/query/qlabel?search=ProbablyERROR","Search compounds by Quality Labels"},
			{"/query/results/1","Display previous search results"},
			
			{"/query/pubchem","PubChem query"},
			{"/depict/cdk?search=c1ccccc1","Structure diagram (based on CDK)"},
			{"/depict/daylight?search=c1ccccc1","Structure diagram (based on Daylight depict"},
			{"/build3d/smiles/c1ccccc1","Generate 3D structure"}
	};
	public AmbitResource() {
		super();
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		
		//consumer = new DatasetConsumer();

	}
	
	protected String getSearchString() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue("search");
		if (key != null) {
	        return Reference.decode(key.toString());
		} else return null;		
	}
	
	@Override
	public Representation getRepresentation(Variant variant) {
		try {
			String search = getSearchString();
			if (search != null) {
				getResponse().setLocationRef(String.format("%s/compound?search=%s",getRequest().getRootRef(),Reference.encode(search)));
				getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
				return null;
			}
		} catch (Exception x) {}
	    System.out.println(getRequest().getAttributes());    
		try {
			if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
				StringBuilder xml = new StringBuilder();
				xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
				xml.append("<ambit xmlns=\"http://ambit.sourceforge.net/ambit/rest/v2\">");
				for (String[] s:uri) {
					xml.append("<uri>");
					xml.append(getRequest().getRootRef());
					xml.append(s[0]);
					xml.append("</uri>");
				}
				xml.append("</ambit>");
				return new StringRepresentation(xml.toString());
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				StringBuilder xml = new StringBuilder();
				for (String[] s:uri) {
					xml.append(getRequest().getRootRef());
					xml.append(s[0]);
				}
				return new StringRepresentation(xml.toString());				
			} else { //if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
				variant.setMediaType(MediaType.TEXT_HTML);
				StringWriter writer = new StringWriter();
				writeHTMLHeader(writer, "AMBIT", getRequest().getRootRef());
				writer.write("<h2>AMBIT REST services site is under development!</h2>");
				writer.write("<p>API is under development at <a href=\"http://opentox.org\" target=\"blank\">http://opentox.org</a><p>URL of some services: ");
				writer.write("<ul>");
				for (String[] s:uri) {
					writer.write("<li>");
					writer.write(s[1]);
					writer.write("&nbsp;<a href=\"");
					writer.write(getRequest().getRootRef().toString());
					writer.write(s[0]);
					writer.write("\">");
					writer.write(s[0]);
					writer.write("</a>");
					writer.write("\n");
				}
				writer.write("</ul>");
				writeHTMLFooter(writer, "AMBIT", getRequest().getRootRef());
				return new StringRepresentation(writer.toString(),MediaType.TEXT_HTML);				
			}
			
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}
	
	public static void writeHTMLHeader(Writer w,String title,Reference baseReference) throws IOException {
		writeHTMLHeader(w, title, baseReference,"");
	}
	public static void writeHTMLHeader(Writer w,String title,Reference baseReference,String meta) throws IOException {

		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			);
		w.write(String.format("<html><head><title>%s</title>%s\n",title,meta));
		w.write(String.format("<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">",baseReference));
		w.write("</head>\n");
		w.write("<body>\n");
		w.write("<div style= \"width: 100%; background-color: #516373;");
		w.write("border: 1px solid #333; padding: 0px; margin: 0px auto;\">");
		w.write("<div class=\"spacer\"></div>");

		w.write("<div class=\"row\"><span class=\"left\">Home</span>");
		w.write("	<span class=\"right\">Login</span></div>");
		w.write("	<div class=\"spacer\"></div>");
		w.write("</div>");
		w.write("<div>");		
		w.write(String.format("<a href='%s/compound'>Chemical compounds</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/similarity'>Similar structures</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/substructure'>Substructure</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/query/smarts'>SMARTS patterns</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s%s/Endpoints'>Endpoints</a>&nbsp;",baseReference,OntologyResource.resource));
		w.write(String.format("<a href='%s/dataset'>Datasets</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s/algorithm'>Algorithms</a>&nbsp;",baseReference));
		w.write(String.format("<a href='%s%s'>References</a>&nbsp;",baseReference,ReferenceResource.reference));
		w.write(String.format("<a href='%s%s'>Feature definitions</a>&nbsp;",baseReference,PropertyResource.featuredef));
		w.write(String.format("<a href='%s/model'>Models</a>&nbsp;",baseReference));
		w.write("</div>");
		w.write("<table width='100%' bgcolor='#ffffff'>");
		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String.format("<a href=\"http://ambit.sourceforge.net\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("</td>");
		w.write("<td align='center'>");

		w.write("<form action='' method='get'>\n");
		w.write("<input name='search' size='80'>\n");
		w.write("<input type='submit' value='Search'><br>");
		//w.write(baseReference.toString());

		w.write("</form>\n");
		w.write("</td>");
		w.write("<td align='right' width='256px'>");
		w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		w.write("<hr>");
		
	}
	public static void writeHTMLFooter(Writer output,String title,Reference baseReference) throws IOException {
		output.write("<div class=\"footer\"><span class=\"right\">");
		output.write("<font color='#D6DFF7'>");
		output.write("Developed by Ideaconsult Ltd. (2005-2009)"); 
		output.write("</font><br>");
		output.write("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>&nbsp; ");

		output.write("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>");
		output.write("</span></div>");
		output.write("</body>");
		output.write("</html>");

	}	
}
