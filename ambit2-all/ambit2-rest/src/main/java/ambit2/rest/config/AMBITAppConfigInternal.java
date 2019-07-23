package ambit2.rest.config;

public class AMBITAppConfigInternal extends AMBITAppConfigProperties {
	protected static final String attachDepict = "attach.depict";
	protected static final String attachSubstance = "attach.substance";
	protected static final String attachInvestigation = "attach.investigation";
	protected static final String attachSubstanceOwner = "attach.substanceowner";
	protected static final String attachToxmatch = "attach.toxmatch";

	
	public AMBITAppConfigInternal() {
		super(null,null);
	}
	

	public synchronized boolean attachDepictRouter() {
		return getBooleanPropertyWithDefault(attachDepict, ambitProperties, true);
	}

	public synchronized boolean attachSubstanceRouter() {
		return getBooleanPropertyWithDefault(attachSubstance, ambitProperties, true);
	}

	public synchronized boolean attachInvestigationRouter() {
		return getBooleanPropertyWithDefault(attachInvestigation, ambitProperties, false);
	}
	
	public synchronized boolean attachSubstanceOwnerRouter() {
		return getBooleanPropertyWithDefault(attachSubstanceOwner, ambitProperties, true);
	}

	public synchronized boolean attachToxmatchRouter() {
		return getBooleanPropertyWithDefault(attachToxmatch, ambitProperties, true);
	}

	public synchronized boolean isOpenToxAAEnabled() {
		return getBooleanPropertyWithDefault(OPENTOX_AA_ENABLED,configProperties,false);
	}


	public synchronized boolean isDBAAEnabled() {
		return getBooleanPropertyWithDefault(DB_AA_ENABLED,configProperties,false);
	}
	
	
	public synchronized boolean isSimpleSecretAAEnabled() {
		return getBooleanPropertyWithDefault(LOCAL_AA_ENABLED,configProperties,false);
	}

	public synchronized boolean isWarmupEnabled() {
		return getBooleanPropertyWithDefault(WARMUP_ENABLED,configProperties,false);
	}	
	public synchronized boolean protectAdminResource() {
		return getBooleanPropertyWithDefault(adminAAEnabled,ambitProperties,false);
	}

	
	public synchronized boolean protectCompoundResource() {
		return getBooleanPropertyWithDefault(compoundAAEnabled,ambitProperties,false);
	}

	public synchronized boolean protectFeatureResource() {
		return getBooleanPropertyWithDefault(featureAAEnabled,ambitProperties,false);
	}
	
	
}
