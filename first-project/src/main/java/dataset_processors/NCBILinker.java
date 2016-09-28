package dataset_processors;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataset_models.NCBI;
import dataset_models.NCBI.Citation;
import wikidata.examples.ExampleHelpers;

public class NCBILinker extends Linker<NCBI> {


	static final String DB_HOST = ExampleHelpers.loadProp("host");
	static final String DB_USER = ExampleHelpers.loadProp("user");
	static final String DB_PASS = ExampleHelpers.loadProp("password");
	
	
	/*
	 * Method to query the NCBI entity using MySQL with the NCBI taxonomy
	 * database deployed
	 */	
	@Override
	public NCBI get(int id) {
		NCBI entity = new NCBI();

		// RDF method :

		// SQL method :

		try {
			entity.setId(id);
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn;
			conn = DriverManager.getConnection(DB_HOST, DB_USER, DB_PASS);

			conn.setCatalog("ncbi_taxonomy");

			String query = "SELECT * from division d, gencode g, names na, nodes n "
					+ "WHERE n.tax_id = ? " + "AND n.tax_id=na.tax_id "
					+ "AND g.genetic_code_id=n.genetic_code_id "
					+ "AND d.division_id=n.division_id;";

			String citationQuery = "SELECT * from citations c, nodes n "
					+ "WHERE n.tax_id = ? " + "AND c.taxid_list LIKE ? ;";

			PreparedStatement prest = conn.prepareStatement(query);
			prest.setInt(1, id);

			PreparedStatement prestCit = conn.prepareStatement(citationQuery);
			prestCit.setInt(1, id);
			prestCit.setString(2, "%" + id + "%");

			ResultSet rs = prest.executeQuery();

			ArrayList<String> syn = new ArrayList<String>();
			ArrayList<String> comm = new ArrayList<String>();

			// Main query
			while (rs.next()) {
				entity.setRank(rs.getString("rank"));
				entity.setParentId(rs.getString("parent_tax_id"));

				switch (rs.getString("name_class")) {
				case "scientific name":
					entity.setScientificName(rs.getString("name_txt"));
					break;
				case "authority":
					entity.setCompleteName(rs.getString("name_txt"));
					break;
				case "common name":
					comm.add(rs.getString("name_txt"));
					break;
				case "synonym":
					syn.add(rs.getString("name_txt"));
					break;
				default:
					break;
				}

				entity.setGenCode(entity.fillGenCode(rs));
			}

			entity.setSynonyms(syn.toArray(new String[syn.size()]));
			entity.setCommonNames(comm.toArray(new String[comm.size()]));

			// Citations query

			ResultSet rsCit = prestCit.executeQuery();
			ArrayList<Citation> cits = new ArrayList<Citation>();
			while (rsCit.next()) {
				cits.add(entity.fillCitation(rsCit));
			}
			entity.setCitations(cits.toArray(new Citation[cits.size()]));

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return entity;

	}


	@Override
	public void write(NCBI NCBIItem, String path) {

		try (OutputStreamWriter osw = new OutputStreamWriter(
				new FileOutputStream(path, true), "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter w = new PrintWriter(bw)) {

			String entity = "ncbi_ent:" + NCBIItem.getId();

			w.println("\t" + entity);
			Linker.printPropertyValue(w, 2, "ncbi:scientificName",
					NCBIItem.getScientificName(), false,false, false);
			Linker.printPropertyValue(w, 2, "ncbi:completeName",
					NCBIItem.getCompleteName(),false, false, false);
			Linker.printPropertyValue(w, 2, "ncbi:rank", NCBIItem.getRank(),false, false, false);
			Linker.printPropertyValue(w, 2, "ncbi:parentID", NCBIItem.getParentId(),
					false,false, false);

			if (NCBIItem.getCommonNames() != null) {
				for (String commonName : NCBIItem.getCommonNames()) {
					Linker.printPropertyValue(w, 2, "ncbi:commonName", commonName,
							false,false, false);
				}
			}

			if (NCBIItem.getSynonyms() != null) {
				for (String syn : NCBIItem.getSynonyms()) {
					Linker.printPropertyValue(w, 2, "ncbi:synonym", syn,false, false, false);
				}
			}

			if (NCBIItem.getCitations() != null) {
				for (Citation cit : NCBIItem.getCitations()) {
					w.println("\t\tncbi:citation [");
					Linker.printPropertyValue(w, 3, "ncbi:cit_key", cit.key, true, false, false);
					Linker.printPropertyValue(w, 3, "ncbi:cit_url", cit.url, true, false, false);
					Linker.printPropertyValue(w, 3, "ncbi:cit_text", cit.text, true, true, false);
					w.println("\t\t] ;");
				}
			}

			if (NCBIItem.getGenCode() != null) {
				w.println("\t\tncbi:gencode [");
				Linker.printPropertyValue(w, 3, "ncbi:gencode_abbrev",
						NCBIItem.getGenCode().abbrev, true, false, false);
				Linker.printPropertyValue(w, 3, "ncbi:gencode_name",
						NCBIItem.getGenCode().name, true, false, false);
				Linker.printPropertyValue(w, 3, "ncbi:gencode_transtable",
						NCBIItem.getGenCode().transTable, true, false, false);
				Linker.printPropertyValue(w, 3, "ncbi:gencode_startcodons",
						NCBIItem.getGenCode().startCodons, true, true, false);
				w.println("\t\t] .");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Taxon rank already available
		// --> check if same value or more/less precise value

		// Same for complete name, may be already available
		// --> check value

		// Same for scientific name, may be already available
		// --> check value

		// Same for synonyms, may be already available
		// --> check values

		// parentID : may be useless

		// Taxonomy may be already there, but also with different format
		// --> deep check value

		// Same for citations, may be already available
		// --> check values

		// Write gencode
		// --> several properties : how to deal with this?

	}

}
