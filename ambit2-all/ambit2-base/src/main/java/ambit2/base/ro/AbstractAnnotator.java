package ambit2.base.ro;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import net.idea.modbcum.p.DefaultAmbitProcessor;

public abstract class AbstractAnnotator<U,V> extends DefaultAmbitProcessor<U, V> {
	protected Map<String, Properties> lookup;
	protected boolean fulllinks = true;
	protected enum _dictionaries {
		endpoints,
		guideline,
		params,
		conditions,
		units,
		substance
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -1938797533321541056L;

	public AbstractAnnotator(File lookupfolder) {
		this(lookupfolder, new String[] {_dictionaries.endpoints.name()}, true);
	}
	
	public AbstractAnnotator(File lookupfolder, String[] dictionaries) {
		this(lookupfolder, dictionaries, true);
	}

	public AbstractAnnotator(File lookupfolder, String[] dictionaries, boolean fulllinks) {
		super();
		this.fulllinks = fulllinks;
		for (String dictionary : dictionaries)
			try {
				Properties dict = new Properties();
				dict.load(new FileInputStream(new File(lookupfolder, String.format("%s.properties", dict))));
				lookup.put(dictionary, dict);
			} catch (Exception x) {
				logger.log(Level.WARNING, lookupfolder.getAbsolutePath(), x);
			}
	}

	@Override
	public abstract V process(U target) throws Exception;
		

	public String[] annotate(Properties lookup, String endpoint)  {
		if (lookup == null)
			return null;
		String[] terms = lookup.getProperty(endpoint.toUpperCase().replaceAll(" ", "_")).split(";");
		if (!fulllinks)
			for (int i = 0; i < terms.length; i++)
				terms[i] = ontolink2id(terms[i]);

		return terms;
	}


	public static String ontolink2id(String ontolink) {
		if (ontolink.startsWith("http")) {
			String[] terms = null;
			if (ontolink.indexOf("#") > 0)
				terms = ontolink.split("#");
			else
				terms = ontolink.split("/");
			return terms[terms.length - 1];
		} else
			return ontolink;
	}
}
