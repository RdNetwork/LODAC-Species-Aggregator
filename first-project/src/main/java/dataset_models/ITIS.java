package dataset_models;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class ITIS {

	public class Publication {
		String author;
		String name;
		String place;
		String date;
	}

	public class Source {
		String name;
		String type;
		String updateDate;
	}

	HashMap<String, String> vernacularNames = new HashMap<String, String>();
	LinkedHashSet<String> taxonomy = new LinkedHashSet<String>();
	HashMap<String, String> experts = new HashMap<String, String>();

	String name;
	String completeName;
	boolean valid;
	String taxonAuthor;
	String rank;
	String kingdom;
	String parentTaxon;
	int childrenCount;

	String geographicDiv;

	Publication[] publications;
	Source[] otherSources;
	
	
	public HashMap<String, String> getVernacularNames() {
		return vernacularNames;
	}

	public void setVernacularNames(HashMap<String, String> vernacularNames) {
		this.vernacularNames = vernacularNames;
	}

	public LinkedHashSet<String> getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(LinkedHashSet<String> taxonomy) {
		this.taxonomy = taxonomy;
	}

	public HashMap<String, String> getExperts() {
		return experts;
	}

	public void setExperts(HashMap<String, String> experts) {
		this.experts = experts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getTaxonAuthor() {
		return taxonAuthor;
	}

	public void setTaxonAuthor(String taxonAuthor) {
		this.taxonAuthor = taxonAuthor;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getKingdom() {
		return kingdom;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public String getParentTaxon() {
		return parentTaxon;
	}

	public void setParentTaxon(String parentTaxon) {
		this.parentTaxon = parentTaxon;
	}

	public int getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}

	public String getGeographicDiv() {
		return geographicDiv;
	}

	public void setGeographicDiv(String geographicDiv) {
		this.geographicDiv = geographicDiv;
	}

	public Publication[] getPublications() {
		return publications;
	}

	public void setPublications(Publication[] publications) {
		this.publications = publications;
	}

}
