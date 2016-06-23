package dataset_models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class NCBI {

	/*
	 * public class Taxonomy { String division; String genus; String species;
	 * String subspecies; }
	 */

	@Override
	public String toString() {
		return "NCBI [rank=" + rank + ", scientficName=" + scientificName
				+ ", completeName=" + completeName + ", commonNames="
				+ Arrays.toString(commonNames) + ", synonyms="
				+ Arrays.toString(synonyms) + ", parentId=" + parentId
				+ ", citations=" + Arrays.toString(citations) + ", genCode="
				+ genCode + "]";
	}

	public class Citation {
		public String key;
		public String url;
		public String text;
	}

	public class GenCode {
		public String abbrev;
		public String name;
		public String transTable;
		public String startCodons;
	}

	protected int id;
	protected String rank;
	protected String scientificName;
	protected String completeName;
	protected String[] commonNames;
	protected String[] synonyms;

	protected String parentId;
	// protected Taxonomy taxonomy;
	protected Citation[] citations;
	protected GenCode genCode;

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public String[] getCommonNames() {
		return commonNames;
	}

	public void setCommonNames(String[] commonNames) {
		this.commonNames = commonNames;
	}

	public String[] getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String[] synonyms) {
		this.synonyms = synonyms;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/*
	 * public Taxonomy getTaxonomy() { return taxonomy; }
	 * 
	 * public void setTaxonomy(Taxonomy taxonomy) { this.taxonomy = taxonomy; }
	 */

	public Citation[] getCitations() {
		return citations;
	}

	public void setCitations(Citation[] citations) {
		this.citations = citations;
	}

	public GenCode getGenCode() {
		return genCode;
	}

	public void setGenCode(GenCode genCode) {
		this.genCode = genCode;
	}

	public Citation fillCitation(ResultSet rsCit) throws SQLException {
		Citation c = new Citation();

		c.key = rsCit.getString("cit_key");
		c.url = rsCit.getString("url");
		c.text = rsCit.getString("citation_text");

		return c;
	}

	public GenCode fillGenCode(ResultSet rs) throws SQLException {
		GenCode g = new GenCode();
		
		g.abbrev = rs.getString("abbreviation");
		g.name = rs.getString("name");
		g.transTable = rs.getString("cde");
		g.startCodons = rs.getString("starts");
		
		return g;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
