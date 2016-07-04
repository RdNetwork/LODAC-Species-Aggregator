package dataset_models;

import java.util.ArrayList;

public class ITIS {

	
	// Darwin Core model
	public int tsn;
	Taxon taxon;
	ArrayList<VernacularName> vernacularNames;
	
	public class VernacularName {
		public String vernacularName;
		public String language;
	}
	
	public class Taxon {
		public String parentNameUsageID;
		public String acceptedNameUsageID;
		public String scientificName;
		public String completeName;
		public String taxonomicStatus;
		public String taxonRank;
	}

	public int getTsn() {
		return tsn;
	}

	public void setTsn(int tsn) {
		this.tsn = tsn;
	}

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
	
	// SQL Model
	/*
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
	*/
	
	
	
	/*
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
	*/
	
}
