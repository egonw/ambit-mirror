package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.AbstractDataset;
import ambit2.base.data.ISourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.IStructureKey.Matcher;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;

/**Generates html page for {@link QueryDatasetResource}
 * @author nina
 *
 */
public class DatasetsHTMLReporter extends QueryHTMLReporter<ISourceDataset, IQueryRetrieval<ISourceDataset>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public static String fileUploadField = "file";
	public DatasetsHTMLReporter(ResourceDoc doc) {
		this(null,true,doc);
	}
	public DatasetsHTMLReporter(Request baseRef,boolean collapsed,ResourceDoc doc) {
		this(baseRef,baseRef,collapsed,doc);
	}
	public DatasetsHTMLReporter(Request baseRef,Request originalRef,boolean collapsed,ResourceDoc doc) {
		super(baseRef,collapsed,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<ISourceDataset>>(request,doc);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ISourceDataset> query) {
		super.header(w, query);
		uploadUI(w, query);
		//if (collapsed) {
			String alphabet = "abcdefghijklmnopqrstuvwxyz";  
			try {
				w.write(String.format("<a href='?search=' title='List all datasets'>%s</a>&nbsp","All"));
				w.write(String.format("<a href='' title='Refresh this page'>%s</a>&nbsp","Refresh"));
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toUpperCase(),search.toUpperCase(),search.toUpperCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toLowerCase(),search.toLowerCase(),search.toLowerCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < 10; i++) {
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								i,i,i));
				}			
				w.write("<hr>");
			} catch (Exception x) {
				
			}
		//}
		
			
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<ISourceDataset> query) {
		super.footer(output, query);
	}
	public void uploadUI(Writer output, IQueryRetrieval<ISourceDataset> query) {		
		try {
			String[][] methods = new String[][] {
					{"post","Add new dataset","Adds all compounds and data from the file, even empty structures."},
					{"put","Import properties","Import properties only for compounds from the file, which could be found in the database"}
			};
			output.write("<table width='95%' border='1' border-style='solid' >");
			output.write("<tr>");
			for (int i=0; i < methods.length;i ++) {
				String[] method = methods[i];
				output.write("<td width='50%'>\n");
				output.write("<table border='0' width='95%'>");
				output.write("<caption>");
				output.write(String.format("<label accesskey='F' title='%s'>%s</label>",
						method[2],
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1])
				)); 	
				output.write("</caption>");
				output.write("<tbody>");
				output.write(String.format("<form method=\"post\" action=\"?method=%s\" ENCTYPE=\"multipart/form-data\">",method[0]));
				//file
				output.write("<tr>");
				output.write("<th>File<label title='Mandatory'>*</label></th>");
				output.write("<td>");
				output.write(String.format("<input type=\"file\" name=\"%s\" accept=\"%s\" title='%s' size=\"60\">",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString(),
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1]))); 
				output.write("</td>");
				output.write("</tr>");
				//title
				output.write("<tr>");
				output.write("<th>Dataset name</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='title' title='%s' size=\"60\">","Dataset name (dc:title)")); 
				output.write("</td>");
				output.write("</tr>");
				//match
				output.write("<tr>");
				output.write("<th>Match</th>");
				output.write("<td>");
				output.write("<select name='match'>");
				for (Matcher matcher : IStructureKey.Matcher.values())
					output.write(String.format("<option title='%s' value='%s' %s>%s</option>",
							    "On import, finds the same compound in the database by matching with the selected criteria \""+matcher.getDescription()+"\"\n",
								matcher.toString(),
								IStructureKey.Matcher.CAS.equals(matcher)?"selected":"",
								(matcher.getDescription().length()>60)?matcher.getDescription().substring(0,60)+"...":matcher.getDescription()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				//URL
				output.write("<tr>");
				output.write("<th>URL</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='seeAlso' title='%s' size=\"60\">","Related URL (rdfs:seeAlso)")); 
				output.write("</td>");
				
				output.write("</tr>");
				
				//match
				output.write("<tr>");
				output.write("<th>License</th>");
				output.write("<td>");
				output.write("<select name='license'>");
				for (ISourceDataset.license license : ISourceDataset.license.values())
					output.write(String.format("<option title='%s' value='%s'>%s</option>",
							    license.getTitle(),
								license.getURI(),
								license.getURI()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				
				output.write("<tr><td align='right'><input type='submit' value='Submit'></td></tr>");
				output.write("</form>");
				output.write("</tbody>");
				output.write("</table>");
				
				output.write("</td>\n");
			}
			output.write("</tr>");
			output.write("</table>");
			output.write("<hr>");
			//if (collapsed) {

				
				/*
				output.write("<div class=\"actions\"><span class=\"center\">");
				output.write("<form method=\"post\" action=\"?method=put\" ENCTYPE=\"multipart/form-data\">");

				output.write(String.format("<label accesskey=F>%s&nbsp;<input type=\"file\" name=\"%s\" accept=\"%s\" size=\"80\"></label>",
						"Import properties (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString())); 
				
				output.write("<select name='match'>");
				for (Matcher matcher : IStructureKey.Matcher.values())
					output.write(String.format("<option value='%s' %s>%s</option>",
							matcher.toString(),
							IStructureKey.Matcher.CAS.equals(matcher)?"selected":"",
							matcher.getDescription()));
				output.write("</select>");
				
				output.write("<br><input type='submit' value='Submit'>");
				output.write("</form>");
				output.write("</span></div>\n");	
				*/					
			//}
		} catch (Exception x) {}
		
	}
	@Override
	public Object processItem(ISourceDataset dataset) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(dataset);
			
			
			output.write("<div id=\"div-1b\">");

			output.write("<div class=\"rowwhite\"><span class=\"left\">");
			
			if (!collapsed) {

				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/table.png\" alt=\"compounds\" title=\"Browse compounds\" border=\"0\"/></a>",
						w.toString(),
						CompoundResource.compound,
						uriReporter.getBaseReference().toString()));	
				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/feature.png\" alt=\"features\" title=\"Retrieve feature definitions\" border=\"0\"/></a>",
						w.toString(),
						PropertyResource.featuredef,
						uriReporter.getBaseReference().toString()));	

				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/search.png\" alt=\"/smarts\" title=\"Search compounds with SMARTS\" border=\"0\"/></a>",
						w.toString(),
						"/smarts",
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");
				
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/search.png\" alt=\"/similarity\" title=\"Search for similarcompounds within this dataset\" border=\"0\"/></a>",
						w.toString(),
						"/similarity",
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");				
				
				output.write("</span><span class=\"center\">");
				MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,

						ChemicalMediaType.CHEMICAL_CML,
						ChemicalMediaType.CHEMICAL_SMILES,						
						MediaType.TEXT_URI_LIST,
						MediaType.APPLICATION_PDF,
						MediaType.TEXT_CSV,
						ChemicalMediaType.WEKA_ARFF,
						MediaType.APPLICATION_RDF_XML
						};
				String[] image = {
						"sdf.jpg",
						"cml.jpg",
						"smi.png",						
						"link.png",
						"pdf.png",
						"excel.png",
						"weka.jpg",
						"rdf.gif"
						
				};		
				for (int i=0;i<mimes.length;i++) {
					MediaType mime = mimes[i];
					output.write("&nbsp;");
					output.write(String.format(
							"<a href=\"%s%s?media=%s&max=100\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
							w.toString(),
							"",
							//CompoundResource.compound,
							Reference.encode(mime.toString()),
							uriReporter.getBaseReference().toString(),
							image[i],
							mime,
							mime));	
				}				
	

				output.write("&nbsp;");	
				
			} else {
			
			}
			output.write(String.format(
					"&nbsp;<a href=\"%s%s\">[Metadata]</a>",
					w.toString(),
					"/metadata"));
			
			if (dataset.getLicenseURI()!= null)
				output.write(String.format(
						"&nbsp;<a href=\"%s\" target=_blank title='%s'>[License]</a>",
						dataset.getLicenseURI(),
						dataset.getLicenseURI()
						));			
			else 
				output.write(String.format(
						"&nbsp;<label title='%s'>[License]</label>",
						ISourceDataset.license.Unknown
						));		
			
			output.write(String.format(
					"&nbsp;<a href=\"%s?max=100\">%s</a>",
					w.toString(),
					(dataset.getName()==null)||(dataset.getName().equals(""))?Integer.toString(dataset.getID()):dataset.getName()
					));
			/*
			output.write(String.format(
					"<form method=POST action='%s?method=DELETE'><input type='submit' value='Delete'></form>",
					w.toString()
					));
					*/			
			output.write("</span></div>");
			/*
			output.write(String.format(
					"&nbsp;<a href=\"%s\">%s</a>",
					w.toString(),
					dataset.getReference().getTitle()));			
			*/
			output.write("</div>");
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}


}
/*
public String getReCaptchaHtml() {
ReCaptcha recaptcha = createReCaptcha();
return recaptcha.createRecaptchaHtml("You did not type the captcha correctly", new Properties());
}

private ReCaptcha createReCaptcha() {
String publicKey = //your public key
String privateKey = //your private key
return ReCaptchaFactory.newReCaptcha(publicKey, privateKey, true);
}

@ValidationMethod(on = "submit")
public void captchaValidation(ValidationErrors errors) {
    ReCaptchaResponse response = createReCaptcha().checkAnswer(context.getRequest().getRemoteAddr(),
            context.getRequest().getParameter("recaptcha_challenge_field"),
            context.getRequest().getParameter("recaptcha_response_field"));
    if (!response.isValid()) {
        errors.add("Captcha", new SimpleError("You didn't type the captcha correctly!"));
    }
}
*/