package dataset_models;

import java.util.ArrayList;



public class GBIF {

	public class Taxon {
		public String taxonID;
		public String datasetID;
	    public String parentNameUsageID;
	    public String acceptedNameUsageID;
	    public String originalNameUsageID;
	    public String scientificName;
		public String taxonRank;
		public String nameAccordingTo;
		public String namePublishedIn;
		public String taxonomicStatus;
		public String nomenclaturalStatus;
		public String kingdom;
		public String phylum;
		public String taxonClass;
		public String order;
		public String family;
		public String genus;
		public String taxonRemarks;
	}
	
	public class VernacularName {
		public String lifeStage;
		public String sex;
	    public String vernacularName;
	    public String language;
	    public String source;
	    public String countryCode;
	    public String country;
	}
	
	public class Multimedia {
		public String title;
		public String license;
		public String creator;
		public String references;
		public String contributor;
		public String description;
		public String source;
		public String identifier;
		public String created;
		public String publisher;
		public String rightsHolder;
	}
	
	public class Distribution {
		public String countryCode;
		public String lifeStage;
		public String country;
		public String locationID;
		public String establishmentMeans;
		public String locality;
		public String source;
		public String threatStatus;
		public String locationRemarks;
		public String occurrenceStatus;
	}
	
	public class Reference {
		public String bibliographicCitation;
		public String references;
		public String source;
		public String identifier;
	}
	
	int id;
	Taxon taxon;
	ArrayList<VernacularName> vernacularNames;
	ArrayList<Multimedia> multimediaFiles;
	ArrayList<Distribution> distributions;
	ArrayList<Reference> references;
	
	
	public Taxon getTaxon() {
		return taxon;
	}
	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
	public ArrayList<VernacularName> getVernacularNames() {
		return vernacularNames;
	}
	public void setVernacularNames(ArrayList<VernacularName> vernacularNames) {
		this.vernacularNames = vernacularNames;
	}
	public ArrayList<Multimedia> getMultimediaFiles() {
		return multimediaFiles;
	}
	public void setMultimediaFiles(ArrayList<Multimedia> multimediaFiles) {
		this.multimediaFiles = multimediaFiles;
	}
	public ArrayList<Distribution> getDistributions() {
		return distributions;
	}
	public void setDistributions(ArrayList<Distribution> distributions) {
		this.distributions = distributions;
	}
	public ArrayList<Reference> getReferences() {
		return references;
	}
	public void setReferences(ArrayList<Reference> references) {
		this.references = references;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	

	
}
