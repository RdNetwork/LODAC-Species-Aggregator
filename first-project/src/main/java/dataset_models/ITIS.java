package dataset_models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataset_models.ITIS.Reference;
import dataset_models.ITIS.Vernacular;

public class ITIS {

	public class Vernacular {
		public String name;
		public String language;
		public boolean approved;
		public ArrayList<Reference> refs;
	}
	
	public class Jurisdiction {
		public String value;
		public String origin;
	}
	
	public class GeographicDivision {
		public String value;
	}
	
	public class Reference {
		public String occurenceName;
	}
	
	public class Expert extends Reference {
		public String expert;
		public String expertComment;
	}
	
	public class Publication extends Reference {
		public String author;
		public String title;
		public String name;
		public String listedDate;
		public String actualDate;
		public String publisher;
		public String pubPlace;
		public String isbn;
		public String issn;
		public int pages;
		public String comment;
	}
	
	public class Source extends Reference {
		public String sourceType;
		public String source;
		public String version;
		public String acquisitionDate;
		public String sourceComment;
	}
	
	public class Comment {
		public String commentator;
		public String commentDetail;
		public String commentTimestamp;
	}
	

	int tsn;
	String name;
	String usage;
	String credibilityRating;
	String completenessRating;
	String revisionYear;
	String taxonAuthor;
	String kingdom;
	String taxonRank;
	String parentTaxon;
	ArrayList<String> phylogeny;
	
	ArrayList<Vernacular> vernaculars;
	ArrayList<Comment> comments;
	ArrayList<Jurisdiction> jurisdictions;
	ArrayList<GeographicDivision> geoDivisions;
	ArrayList<Integer> synonyms;
	ArrayList<Reference> references;
	
	
	public int getTsn() {
		return tsn;
	}
	public void setTsn(int tsn) {
		this.tsn = tsn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getCredibilityRating() {
		return credibilityRating;
	}
	public void setCredibilityRating(String credibilityRating) {
		this.credibilityRating = credibilityRating;
	}
	public String getCompletenessRating() {
		return completenessRating;
	}
	public void setCompletenessRating(String completenessRating) {
		this.completenessRating = completenessRating;
	}
	public String getRevisionYear() {
		return revisionYear;
	}
	public void setRevisionYear(String revisionYear) {
		this.revisionYear = revisionYear;
	}
	public String getTaxonAuthor() {
		return taxonAuthor;
	}
	public void setTaxonAuthor(String taxonAuthor) {
		this.taxonAuthor = taxonAuthor;
	}
	public String getKingdom() {
		return kingdom;
	}
	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}
	public String getTaxonRank() {
		return taxonRank;
	}
	public void setTaxonRank(String taxonRank) {
		this.taxonRank = taxonRank;
	}
	public String getParentTaxon() {
		return parentTaxon;
	}
	public void setParentTaxon(String parentTaxon) {
		this.parentTaxon = parentTaxon;
	}
	public ArrayList<String> getPhylogeny() {
		return phylogeny;
	}
	public void setPhylogeny(ArrayList<String> phylogeny) {
		this.phylogeny = phylogeny;
	}
	public ArrayList<Vernacular> getVernaculars() {
		return vernaculars;
	}
	public void setVernaculars(ArrayList<Vernacular> vernaculars) {
		this.vernaculars = vernaculars;
	}
	public ArrayList<Comment> getComments() {
		return comments;
	}
	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
	public ArrayList<Jurisdiction> getJurisdictions() {
		return jurisdictions;
	}
	public void setJurisdictions(ArrayList<Jurisdiction> jurisdictions) {
		this.jurisdictions = jurisdictions;
	}
	public ArrayList<GeographicDivision> getGeoDivisions() {
		return geoDivisions;
	}
	public void setGeoDivisions(ArrayList<GeographicDivision> geoDivisions) {
		this.geoDivisions = geoDivisions;
	}
	public ArrayList<Integer> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(ArrayList<Integer> synonyms) {
		this.synonyms = synonyms;
	}
	public ArrayList<Reference> getReferences() {
		return references;
	}
	public void setReferences(ArrayList<Reference> references) {
		this.references = references;
	}
	
	
	public Vernacular fillVernacular(ResultSet rs) {
		Vernacular v = new Vernacular();
		
		try {
			v.approved = (rs.getString("approved_ind").equals("true"));
			v.language = rs.getString("language");
			v.name = rs.getString("vernacular_name");
			
			ArrayList<Reference> refs = new ArrayList<Reference>();
			v.refs = fillReferences(rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return v;
	}

	public ArrayList<Reference> fillReferences(ResultSet rs) {
		ArrayList<Reference> refs = new ArrayList<Reference>();
		
		Reference r = new Reference();
		try {
			switch (rs.getString("doc_id_prefix")) {
			case "EXP":
				
				break;
			case "PUB":
				
				
				break;
			case "SRC":
				
				
				break;
			default:
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return refs;
	}
	
	
}
