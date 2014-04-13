package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class Protocol {
	String category;
	String endpoint;
	List<String> guideline;
	//hack for IUCLID categories, as i can't find the exact titles in the schema :(
	public enum _categories {
		/**
		 * Phys chem properties	
		 */
		GI_GENERAL_INFORM_SECTION {
			@Override
			public String toString() {
				return "Appearance";
			}
			@Override
			public String getNumber() {
				return "4.1";
			}
			@Override
			public int getSortingOrder() {
				return 401;
			}
		},
		PC_MELTING_SECTION {
			@Override
			public String toString() {
				return "Melting point / freezing point";
			}
			@Override
			public String getNumber() {
				return "4.2";
			}			
			@Override
			public int getSortingOrder() {
				return 402;
			}
		},
		PC_BOILING_SECTION {
			@Override
			public String toString() {
				return "Boiling point";
			}
			@Override
			public String getNumber() {
				return "4.3";
			}		
			@Override
			public int getSortingOrder() {
				return 403;
			}
		},
		PC_GRANULOMETRY_SECTION {
			@Override
			public String toString() {
				return "Particle size distribution (Granulometry)";
			}
			@Override
			public String getNumber() {
				return "4.5";
			}			
			@Override
			public int getSortingOrder() {
				return 405;
			}
		},		
		PC_VAPOUR_SECTION {
			@Override
			public String toString() {
				return "Vapour pressure";
			}
			@Override
			public String getNumber() {
				return "4.6";
			}			
			@Override
			public int getSortingOrder() {
				return 406;
			}
		},
		PC_PARTITION_SECTION {
			@Override
			public String toString() {
				return "Partition coeffcicient";
			}
			@Override
			public String getNumber() {
				return "4.7";
			}	
			@Override
			public int getSortingOrder() {
				return 407;
			}
		},				
		PC_WATER_SOL_SECTION {
			@Override
			public String toString() {
				return "Water solubility";
			}
			@Override
			public String getNumber() {
				return "4.8";
			}	
			@Override
			public int getSortingOrder() {
				return 408;
			}
		},		
		PC_SOL_ORGANIC_SECTION {
			@Override
			public String toString() {
				return "Solubility in organic solvents";
			}
			@Override
			public String getNumber() {
				return "4.9";
			}			
			@Override
			public int getSortingOrder() {
				return 409;
			}
		},
		PC_NON_SATURATED_PH_SECTION {
			@Override
			public String toString() {
				return "pH";
			}
			@Override
			public String getNumber() {
				return "4.20";
			}			
			@Override
			public int getSortingOrder() {
				return 420;
			}
		},		
		PC_DISSOCIATION_SECTION {
			@Override
			public String toString() {
				return "Dissociation constant";
			}
			@Override
			public String getNumber() {
				return "4.21";
			}		
			@Override
			public int getSortingOrder() {
				return 421;
			}
		},
		/**
		 * Environmental fate
		 */
		TO_PHOTOTRANS_AIR_SECTION {
			@Override
			public String toString() {
				return "Phototransformation in Air";
			}		
			@Override
			public String getNumber() {
				return "5.1.1";
			}		
			@Override
			public int getSortingOrder() {
				return 511;
			}
		},
		TO_HYDROLYSIS_SECTION {
			@Override
			public String toString() {
				return "Hydrolysis";
			}
			@Override
			public String getNumber() {
				return "5.1.2";
			}		
			@Override
			public int getSortingOrder() {
				return 512;
			}
		},
		TO_BIODEG_WATER_SCREEN_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in water - screening tests";
			}
			@Override
			public String getNumber() {
				return "5.2.1";
			}	
			@Override
			public int getSortingOrder() {
				return 521;
			}
		},
		TO_BIODEG_WATER_SIM_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in water and sediment: simulation tests";
			}
			@Override
			public String getNumber() {
				return "5.2.2";
			}			
			@Override
			public int getSortingOrder() {
				return 522;
			}
		},
		EN_STABILITY_IN_SOIL_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in Soil";
			}
			@Override
			public String getNumber() {
				return "5.2.3";
			}
			@Override
			public int getSortingOrder() {
				return 523;
			}
		},
		EN_BIOACCUMULATION_SECTION {
			@Override
			public String toString() {
				return "Bioaccumulation: aquatic / sediment";
			}
			@Override
			public String getNumber() {
				return "5.3.1";
			}	
			@Override
			public int getSortingOrder() {
				return 531;
			}
		},
		EN_BIOACCU_TERR_SECTION {
			@Override
			public String toString() {
				return "Bioaccumulation: terrestrial";
			}
			@Override
			public String getNumber() {
				return "5.3.2";
			}	
			@Override
			public int getSortingOrder() {
				return 532;
			}
		},		
		EN_ADSORPTION_SECTION {
			@Override
			public String toString() {
				return "Adsorption / Desorption";
			}
			@Override
			public String getNumber() {
				return "5.4.1";
			}				
			@Override
			public int getSortingOrder() {
				return 541;
			}
		},
		EN_HENRY_LAW_SECTION  {
			@Override
			public String toString() {
				return "Henry's Law constant";
			}
			@Override
			public String getNumber() {
				return "5.4.2";
			}		
			@Override
			public int getSortingOrder() {
				return 542;
			}
		},

		/**
		 * Toxicity
		 */
		TO_ACUTE_ORAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - oral";
			}
			@Override
			public String getNumber() {
				return "7.2.1";
			}			
			@Override
			public int getSortingOrder() {
				return 721;
			}
		},
		TO_ACUTE_INHAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - inhalation";
			}			
			@Override
			public String getNumber() {
				return "7.2.2";
			}	
			@Override
			public int getSortingOrder() {
				return 722;
			}
		},
		TO_ACUTE_DERMAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - dermal";
			}			
			@Override
			public String getNumber() {
				return "7.2.3";
			}		
			@Override
			public int getSortingOrder() {
				return 723;
			}
		},
		TO_SKIN_IRRITATION_SECTION {
			@Override
			public String toString() {
				return "Skin irritation / Corrosion";
			}	
			@Override
			public String getNumber() {
				return "7.3.1";
			}	
			@Override
			public int getSortingOrder() {
				return 731;
			}
		},		
		TO_EYE_IRRITATION_SECTION {
			@Override
			public String toString() {
				return "Eye irritation";
			}	
			@Override
			public String getNumber() {
				return "7.3.2";
			}	
			@Override
			public int getSortingOrder() {
				return 732;
			}
		},	
		TO_SENSITIZATION_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation";
			}
			@Override
			public String getNumber() {
				return "7.4.1";
			}
			@Override
			public int getSortingOrder() {
				return 741;
			}
		},			
		TO_SENSITIZATION_HUMAN_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation (human)";
			}
			@Override
			public String getNumber() {
				return "7.4.1";
			}			
			@Override
			public int getSortingOrder() {
				return 741;
			}
		},		
		TO_REPEATED_ORAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - oral";
			}
			@Override
			public String getNumber() {
				return "7.5.1";
			}		
			@Override
			public int getSortingOrder() {
				return 751;
			}
		},
		TO_REPEATED_INHAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - inhalation";
			}
			@Override
			public String getNumber() {
				return "7.5.2";
			}	
			@Override
			public int getSortingOrder() {
				return 752;
			}
		},
		TO_REPEATED_DERMAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - dermal";
			}
			@Override
			public String getNumber() {
				return "7.5.3";
			}	
			@Override
			public int getSortingOrder() {
				return 753;
			}
		},
		TO_GENETIC_IN_VITRO_SECTION {
			@Override
			public String toString() {
				return "Genetic toxicity in vitro";
			}
			@Override
			public String getNumber() {
				return "7.6.1";
			}			
			@Override
			public int getSortingOrder() {
				return 761;
			}
		},		
		TO_GENETIC_IN_VIVO_SECTION {
			@Override
			public String toString() {
				return "Genetic toxicity in vivo";
			}
			@Override
			public String getNumber() {
				return "7.6.2";
			}			
			@Override
			public int getSortingOrder() {
				return 762;
			}
		},			
		TO_CARCINOGENICITY_SECTION {
			@Override
			public String toString() {
				return "Carcinogenicity";
			}
			@Override
			public String getNumber() {
				return "7.7";
			}	
			@Override
			public int getSortingOrder() {
				return 770;
			}
		},
		TO_REPRODUCTION_SECTION {
			@Override
			public String toString() {
				return "Toxicity to reproduction";
			}
			@Override
			public String getNumber() {
				return "7.8.1";
			}	
			@Override
			public int getSortingOrder() {
				return 781;
			}
		},		
		TO_DEVELOPMENTAL_SECTION {
			@Override
			public String toString() {
				return "Developmental toxicity / teratogenicity";
			}
			@Override
			public String getNumber() {
				return "7.8.2";
			}	
			@Override
			public int getSortingOrder() {
				return 782;
			}
		},	

		/**
		 * Ecotoxicity
		 */
		EC_FISHTOX_SECTION {
			@Override
			public String toString() {
				return "Short-term toxicity to fish";
			}
			@Override
			public String getNumber() {
				return "6.1.1";
			}
			@Override
			public int getSortingOrder() {
				return 611;
			}
		},
		EC_CHRONFISHTOX_SECTION {
			@Override
			public String toString() {
				return "Long-term toxicity to fish";
			}
			@Override
			public String getNumber() {
				return "6.1.2";
			}
			@Override
			public int getSortingOrder() {
				return 612;
			}
		},
		EC_DAPHNIATOX_SECTION {
			@Override
			public String toString() {
				return "Short-term toxicity to aquatic inverterbrates";
			}			
			@Override
			public String getNumber() {
				return "6.1.3";
			}	
			@Override
			public int getSortingOrder() {
				return 613;
			}
		},		
		EC_CHRONDAPHNIATOX_SECTION {
			@Override
			public String toString() {
				return "Long-term toxicity to aquatic inverterbrates";
			}
			@Override
			public String getNumber() {
				return "6.1.4";
			}
			@Override
			public int getSortingOrder() {
				return 614;
			}
		},
		EC_ALGAETOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to aquatic algae and cyanobacteria";
			}
			@Override
			public String getNumber() {
				return "6.1.5";
			}
			@Override
			public int getSortingOrder() {
				return 615;
			}
		},
		EC_BACTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to microorganisms";
			}
			@Override
			public String getNumber() {
				return "6.1.7";
			}
			@Override
			public int getSortingOrder() {
				return 617;
			}
		},
		EC_SEDIMENTDWELLINGTOX_SECTION {
			@Override
			public String toString() {
				return "Sediment toxicity";
			}
			@Override
			public String getNumber() {
				return "6.2";
			}		
			@Override
			public int getSortingOrder() {
				return 620;
			}
		},
		EC_SOILDWELLINGTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to soil macroorganisms";
			}
			@Override
			public String getNumber() {
				return "6.3.1";
			}
			@Override
			public int getSortingOrder() {
				return 631;
			}
		},		
		EC_HONEYBEESTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to terrestrial arthropods";
			}
			@Override
			public String getNumber() {
				return "6.3.2";
			}	
			@Override
			public int getSortingOrder() {
				return 632;
			}
		},
		EC_PLANTTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to terrestrial plants";
			}
			@Override
			public String getNumber() {
				return "6.3.3";
			}
			@Override
			public int getSortingOrder() {
				return 633;
			}
		},
		EC_SOIL_MICRO_TOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to soil microorganisms";
			}
			@Override
			public String getNumber() {
				return "6.3.4";
			}	
			@Override
			public int getSortingOrder() {
				return 634;
			}
		},
		AGGLOMERATION_AGGREGATION_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial agglomeration/aggregation";
			}
			@Override
			public String getNumber() {
				return "4.24";
			}	
			@Override
			public int getSortingOrder() {
				return 424;
			}
		},
		CRYSTALLINE_PHASE_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial crystalline phase";
			}
			@Override
			public String getNumber() {
				return "4.25";
			}
			@Override
			public int getSortingOrder() {
				return 425;
			}
		},
		CRYSTALLITE_AND_GRAIN_SIZE_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial crystallite and grain size";
			}
			@Override
			public String getNumber() {
				return "4.26";
			}				
			@Override
			public int getSortingOrder() {
				return 426;
			}
		},
		ASPECT_RATIO_SHAPE_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial aspect ratio/shape";
			}
			@Override
			public String getNumber() {
				return "4.27";
			}			
			@Override
			public int getSortingOrder() {
				return 427;
			}
		},
		SPECIFIC_SURFACE_AREA_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial specific surface area";
			}
			@Override
			public String getNumber() {
				return "4.28";
			}
			@Override
			public int getSortingOrder() {
				return 428;
			}
		},		
		ZETA_POTENTIAL_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial zeta potential";
			}
			@Override
			public String getNumber() {
				return "4.29";
			}			
			@Override
			public int getSortingOrder() {
				return 429;
			}
		},
		SURFACE_CHEMISTRY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial surface chemistry";
			}
			@Override
			public String getNumber() {
				return "4.30";
			}			
			@Override
			public int getSortingOrder() {
				return 430;
			}
		},
		DUSTINESS_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial dustiness";
			}
			@Override
			public String getNumber() {
				return "4.31";
			}
			@Override
			public int getSortingOrder() {
				return 431;
			}
		},
		POROSITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial porosity";
			}
			@Override
			public String getNumber() {
				return "4.32";
			}
			@Override
			public int getSortingOrder() {
				return 432;
			}
		},
		POUR_DENSITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial pour density";
			}
			@Override
			public String getNumber() {
				return "4.33";
			}
			@Override
			public int getSortingOrder() {
				return 433;
			}
		},
		PHOTOCATALYTIC_ACTIVITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial photocatalytic activity";
			}
			@Override
			public String getNumber() {
				return "4.34";
			}
			@Override
			public int getSortingOrder() {
				return 434;
			}
		},
		CATALYTIC_ACTIVITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial catalytic activity";
			}
			@Override
			public String getNumber() {
				return "4.36";
			}
			@Override
			public int getSortingOrder() {
				return 436;
			}
		},
		UNKNOWN_TOXICITY_SECTION {
			@Override
			public String toString() {
				return "Unclassified toxicity";
			}
			@Override
			public String getNumber() {
				return "7.99";
			}
			@Override
			public int getSortingOrder() {
				return 799;
			}
		}	
		

		;
		public String getNumber() {
			return "";
		}
		public int getSortingOrder() {
			return 10000;
		}


	}
	
	public static enum _fields {
		topcategory,
		category,
		endpoint,
		guideline
	}

	protected String topCategory;	
	public String getTopCategory() {
		return topCategory;
	}
	public void setTopCategory(String topCategory) {
		this.topCategory = topCategory;
	}

	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getGuideline() {
		return guideline;
	}
	public void setGuideline(List<String> guide) {
		this.guideline = guide;
	}
	public void addGuideline(String guide) {
		if (this.guideline==null) this.guideline = new ArrayList<String>();
		this.guideline.add(guide);
	}
	public Protocol(String endpoint) {
		this(endpoint,null);
	}
	public Protocol(String endpoint, String guideline) {
		setEndpoint(endpoint);
		if (guideline!=null) addGuideline(guideline);
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint; 
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{");

		b.append("\n\t");
		b.append(JSONUtils.jsonQuote(_fields.topcategory.name()));		
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(topCategory)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.category.name()));		
		b.append(": {");
			b.append(JSONUtils.jsonQuote("code"));b.append(": ");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
			b.append(",");
			b.append(JSONUtils.jsonQuote("title"));b.append(": ");
			try {
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(
						_categories.valueOf(category).getNumber()) + " " +
						_categories.valueOf(category).toString()) 
						);
			} catch (Exception x) {
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
			}
		b.append("},\n\t");
		b.append(JSONUtils.jsonQuote(_fields.endpoint.name()));		
		b.append(":");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.guideline.name()));		
		b.append(": [");				
		if (guideline!=null)
			for (int i=0; i < guideline.size(); i++) {
				if (i>0) b.append(",");
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(guideline.get(i))));
			}	
		b.append("]}");
		return b.toString();
	}

}
