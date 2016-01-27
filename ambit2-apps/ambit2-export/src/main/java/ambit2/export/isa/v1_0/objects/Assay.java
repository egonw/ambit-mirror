
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Assay JSON Schema
 * <p>
 * JSON Schema describing an Assay
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "comments",
    "filename",
    "measurementType",
    "technologyType",
    "technologyPlatform",
    "dataFiles",
    "materials",
    "charactersitics",
    "processSequence"
})
public class Assay {

    @JsonProperty("@id")
    public URI Id;
    @JsonProperty("comments")
    public List<Comment> comments = new ArrayList<Comment>();
    @JsonProperty("filename")
    public String filename;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("measurementType")
    public MeasurementType measurementType;
    @JsonProperty("technologyType")
    public TechnologyType technologyType;
    @JsonProperty("technologyPlatform")
    public String technologyPlatform;
    @JsonProperty("dataFiles")
    public List<DataFile> dataFiles = new ArrayList<DataFile>();
    @JsonProperty("materials")
    public Object materials;
    /**
     * List of all the characteristics (or material attributes) defined in the study, used to avoid duplication of their declaration when each material_attribute_value is created. 
     * 
     */
    @JsonProperty("charactersitics")
    public List<Charactersitic> charactersitics = new ArrayList<Charactersitic>();
    @JsonProperty("processSequence")
    public List<Process> processSequence = new ArrayList<Process>();

}
