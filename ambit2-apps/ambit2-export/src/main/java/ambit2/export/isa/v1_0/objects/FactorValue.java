
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA factor value schema
 * <p>
 * JSON-schema representing a factor value in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "category",
    "value",
    "unit"
})
public class FactorValue {

    @JsonProperty("@id")
    public URI Id;
    /**
     * ISA factor schema
     * <p>
     * JSON-schema representing a factor value in the ISA model
     * 
     */
    @JsonProperty("category")
    public Factor category;
    @JsonProperty("value")
    public Object value;
    /**
     * ISA ontology reference schema
     * <p>
     * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
     * 
     */
    @JsonProperty("unit")
    public MeasurementType unit;

}
