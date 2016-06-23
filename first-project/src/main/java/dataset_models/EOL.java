package dataset_models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class EOL {

	@Override
	public String toString() {
		return "EOL [taxonRank=" + taxonRank + ", scientificName=" + scientificName
				+ ", parentId=" + parentId + ", vernacularNames=" + vernacularNames
				+ ", relatedTaxons=" + Arrays.toString(relatedTaxons) + ", measures="
				+ Arrays.toString(measures) + "]";
	}

	public class Measurement {
		public String type;
		public String typeURI;
		public String value;
		public String valueURI;
		public String method;
		public String statMethod;
		public String units;
		public String unitsURI;
		public String citation;
		public String source;
	}

	public class VernacularName {
		public String name;
		public boolean isPreferredName;	
	}
	
	protected int id;
	protected String taxonRank;
	protected String scientificName;
	protected String parentId;
	protected ArrayList<VernacularName> vernacularNames = new ArrayList<VernacularName>();

	protected String[] relatedTaxons;
	protected Measurement[] measures;

	public String getTaxonRank() {
		return taxonRank;
	}

	public void setTaxonRank(String taxonRank) {
		this.taxonRank = taxonRank;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public ArrayList<VernacularName> getVernacularNames() {
		return vernacularNames;
	}

	public void setVernacularNames(ArrayList<VernacularName> vernacularNames) {
		this.vernacularNames = vernacularNames;
	}

	public Measurement[] getMeasures() {
		return measures;
	}

	public void setMeasures(Measurement[] measures) {
		this.measures = measures;
	}

	public String[] getRelatedTaxons() {
		return relatedTaxons;
	}

	public void setRelatedTaxons(String[] relatedTaxons) {
		this.relatedTaxons = relatedTaxons;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Measurement fillMeasure(LinkedHashMap<String, String> measure) {
		Measurement m = new Measurement();

		m.type = measure.get("predicate");
		m.citation = measure.get("dc:bibliographicCitation");
		m.source = measure.get("dc:source");
		m.value = measure.get("value");
		m.valueURI = measure.get("dwc:measurementValue");
		m.typeURI = measure.get("dwc:measurementType");
		m.method = measure.get("dwc:measurementMethod");
		m.statMethod = measure.get("eol:terms/statisticalMethod");
		m.units = measure.get("units");
		m.unitsURI = measure.get("dwc:measurementUnit");

		return m;

	}

	public VernacularName fillName(LinkedHashMap<String, Object> name) {
		VernacularName vn = new VernacularName();

		Object pref = name.get("gbif:isPreferredName");
		vn.isPreferredName = pref == null ? false : Boolean.parseBoolean(pref.toString());
		vn.name = name.get("dwc:vernacularName").toString();
		return vn;
	}

}
