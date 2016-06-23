package dataset_models;

import java.util.ArrayList;

public class Dyntaxa {

	public class Synonym {
		public String name;
		public String origin;
	}
	
	int id;
	String taxonRank;
	String scientificName;
	String author;
	String commonName;
	
	String GUID;
	String recommendedGUID;
	ArrayList<Synonym> synonyms = new ArrayList<Synonym>();
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public String getGUID() {
		return GUID;
	}
	public void setGUID(String gUID) {
		GUID = gUID;
	}
	public String getRecommendedGUID() {
		return recommendedGUID;
	}
	public void setRecommendedGUID(String recommendedGUID) {
		this.recommendedGUID = recommendedGUID;
	}
	public ArrayList<Synonym> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(ArrayList<Synonym> synonyms) {
		this.synonyms = synonyms;
	}
	
	
}
