package dataset_processors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataset_models.ITIS;
import dataset_models.EOL.VernacularName;
import dataset_models.ITIS.Reference;
import dataset_models.ITIS.Vernacular;
import dataset_models.NCBI.Citation;
import wikidata.examples.ExampleHelpers;

public class ITISLinker extends Linker<ITIS> {

	static final String DB_HOST = ExampleHelpers.loadProp("host");
	static final String DB_USER = ExampleHelpers.loadProp("user");
	static final String DB_PASS = ExampleHelpers.loadProp("password");

	@Override
	public ITIS get(int id) {

		ITIS entity = new ITIS();

		try {
			entity.setTsn(id);
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn;
			conn = DriverManager.getConnection(DB_HOST, DB_USER, DB_PASS);

			conn.setCatalog("itis");

			String query = "SELECT * FROM taxonomics_units tu "
					+ "WHERE tu.tsn = ? ;";
			PreparedStatement prest = conn.prepareStatement(query);
			prest.setInt(1, id);

			String vernsQuery = "SELECT * FROM vernaculars vr, vern_ref_links vl, reference_links rf"
					+ "WHERE vr.tsn = ? " + "AND vr.vern_id = vl.vern_id "
					+ "AND vl.documentation_id = rf.documentation_id ;";
			PreparedStatement prestV = conn.prepareStatement(query);
			prestV.setInt(1, id);

			String jurisQuery = "SELECT * FROM jurisdction jr "
					+ "WHERE jr.tsn = ? ;";
			PreparedStatement prestJ = conn.prepareStatement(query);
			prestJ.setInt(1, id);

			String geodivQuery = "SELECT * FROM geographic_div gd "
					+ "WHERE gd.tsn = ? ;";
			PreparedStatement prestG = conn.prepareStatement(query);
			prestG.setInt(1, id);

			String refsQuery = "SELECT * FROM reference_links rf "
					+ "WHERE rf.tsn = ? ;";
			PreparedStatement prestR = conn.prepareStatement(query);
			prestR.setInt(1, id);

			String commentsQuery = "SELECT * FROM comment_links cl, comments cm "
					+ "WHERE cl.tsn = ? " + "AND cl.comment_id = cm.comment_id ;";
			PreparedStatement prestC = conn.prepareStatement(query);
			prestC.setInt(1, id);

			String synsQuery = "SELECT * FROM synonym_links sy "
					+ "WHERE sy.tsn = ? ;";
			PreparedStatement prestS = conn.prepareStatement(query);
			prestS.setInt(1, id);

			String expSubQuery = "SELECT * FROM experts ex, reference_links rf "
					+ "WHERE doc_id_prefix = expert_id_prefix " + "AND tsn = ?";
			String pubSubQuery = "SELECT * FROM publications pb, reference_links rf "
					+ "WHERE doc_id_prefix = publication_id_prefix " + "AND tsn = ?";
			String srcSubQuery = "SELECT * FROM other_sources os, reference_links rf "
					+ "WHERE doc_id_prefix = source_id_prefix " + "AND tsn = ?";

			ResultSet rs = prest.executeQuery();

			// Main query
			while (rs.next()) {
				String name = rs.getString("unit_name1") + rs.getString("unit_name3")
						+ rs.getString("unit_name3") + rs.getString("unit_name4");
				entity.setName(name);
				entity.setUsage(rs.getString("usage"));
				entity.setCompletenessRating(rs.getString("completeness_rtng"));
				entity.setCredibilityRating(rs.getString("credibility_rtng"));
				entity.setRevisionYear(rs.getString("currency_rating"));
				entity.setTaxonAuthor(rs.getString("usage"));
				entity.setKingdom(getKingdomName(rs.getString("kingdom_id")));
				entity.setTaxonRank(getRankName(rs.getString("rank_id"), entity.getKingdom()));
				entity.setParentTaxon(rs.getString("parent_tsn"));

			}


			// Vernaculars query
			rs = prestV.executeQuery();
			ArrayList<Vernacular> verns = new ArrayList<Vernacular>();
			while (rs.next()) {
				verns.add(entity.fillVernacular(rs));
			}
			entity.setVernaculars(verns);

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

	

	private String getRankName(String rank, String kingdom) {
		switch (rank) {
		case "10":
			return "Kingdom";
		case "20":
			return "Subkingdom";
		case "25":
			boolean infra = (!kingdom.equals("Bacteria"))
					&& (!kingdom.equals("Fungi"));
			if (infra) {
				return "Infrakingdom";
			}
			break;
		case "27":
			if (kingdom.equals("Plantae") || kingdom.equals("Chromista")) {
				return "Superdivision";
			}
			if (kingdom.equals("Animalia")) {
				return "Superphylum";
			}
			break;
		case "30":
			switch (kingdom) {
			case "Plantae":
			case "Chromista":
			case "Fungi":
				return "Division";
			default:
				return "Phylum";
			}
		case "40":
			switch (kingdom) {
			case "Plantae":
			case "Chromista":
			case "Fungi":
				return "Subdivision";

			default:
				return "Subphylum";
			}

		case "45":
			switch (kingdom) {
			case "Protozoa" :
			case "Animalia" :
				return "Infraphylum";
			case "Plantae" :
			case "Chromista" :
				return "Infradivision";
			default:
				break;
			}
			break;
		case "47":
			if (kingdom.equals("Chromsita")) {
				return "Parvdivision";
			}
			if (kingdom.equals("Protozoa")) {
				return "Parvphylum";
			}
			break;
		case "50":
			if (!kingdom.equals("Fungi")) {
				return "Superclass";
			}
			break;
		case "60":
			return "Class";
		case "70":
			return "Subclass";
		case "80":
			if (!kingdom.equals("Fungi")) {
				return "Infraclass";
			}
			break;
		case "90":
			return "Superorder";
		case "100":
			return "Order";
		case "110":
			return "Suborder";
		case "120":
			switch (kingdom) {
			case "Animalia":
			case "Protozoa" :
			case "Bacteria":
				return "Infraorder";
			default:
				break;
			}
			break;
		case "124":
			if (kingdom.equals("Animalia")) {
				return "Section";
			}
			break;
		case "126":
			if (kingdom.equals("Animalia")) {
				return "Subsection";
			}
			break;			
		case "130":
			switch (kingdom) {
			case "Animalia":
			case "Protozoa" :
			case "Bacteria":
				return "Superfamily";
			default:
				break;
			}
			break;
		case "140":
			return "Family";
		case "150":
			return "Subfamily";
		case "160":
			return "Tribe";
		case "170":
			return "Subtribe";
		case "180":
			return "Genus";
		case "190":
			return "Subgenus";
		case "200":
			switch (kingdom) {
			case "Plantae":
			case "Chromista" :
			case "Fungi":
				return "Section";
			default:
				break;
			}
			break;
		case "210":
			switch (kingdom) {
			case "Plantae":
			case "Chromista" :
			case "Fungi":
				return "Subsection";
			default:
				break;
			}
			break;
		case "220":
			return "Species";
		case "230":
			return "Subspecies";
		case "240":
			if (!kingdom.equals("Bacteria")) {
				return "Variety";
			}
			break;
		case "245":
			if (kingdom.equals("Animalia")) {
				return "Form";
			}
			break;
		case "250":
			switch (kingdom) {
			case "Plantae":
			case "Chromista":
			case "Fungi":
				return "Subvariety";
			case "Animalia":
				return "Race";
			default:
				break;
			}
			break;
		case "255":
			if (kingdom.equals("Animalia")) {
				return "Stirp";
			}
			break;
		case "260":
			switch (kingdom) {
			case "Plantae":
			case "Chromista":
			case "Fungi":
				return "Form";
			case "Animalia":
				return "Morph";
			default:
				break;
			}
			break;
		case "265":
			if (kingdom.equals("Animalia")) {
				return "Aberration";
			}
			break;
		case "270":
			switch (kingdom) {
			case "Plantae":
			case "Chromista":
			case "Fungi":
				return "Suborm";
			default:
				break;
			}
			break;
		case "300":
			if (kingdom.equals("Animalia")) {
				return "Unspecified";
			}
			break;
		default:
			break;
		}
		return null;
	}

	private String getKingdomName(String string) {
		switch (string) {
		case "1":
			return "Bacteria";
		case "2":
			return "Protozoa";
		case "3":
			return "Plantae";
		case "4":
			return "Fungi";
		case "5":
			return "Animalia";
		case "6":
			return "Chromista";
		case "7":
			return "Archae";
		default:
			break;
		}
		return null;
	}

	@Override
	public void write(ITIS itisItem, String storePath) {
		// TODO
	}


}
