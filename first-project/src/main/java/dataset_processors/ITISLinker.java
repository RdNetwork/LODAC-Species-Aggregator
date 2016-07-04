package dataset_processors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveFactory;
import org.gbif.dwca.io.UnsupportedArchiveException;
import org.gbif.dwca.record.Record;
import org.gbif.dwca.record.StarRecord;

import dataset_models.ITIS;
import dataset_models.ITIS.Taxon;
import dataset_models.ITIS.VernacularName;
import wikidata.examples.ExampleHelpers;
import wikidata.processors.RdfProcessor;

public class ITISLinker extends Linker<ITIS> {

	/*
	static final String DB_HOST = ExampleHelpers.loadProp("host");
	static final String DB_USER = ExampleHelpers.loadProp("user");
	static final String DB_PASS = ExampleHelpers.loadProp("password");
	*/
	
	public static Archive dwcArchive;

	public static void init() {
		File myArchiveFile = new File(RdfProcessor.dumpPath + "itis/dwca/");
		try {
			dwcArchive = ArchiveFactory.openArchive(myArchiveFile);
		} catch (UnsupportedArchiveException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ITIS get(int id) {

		/**
		 * So far, this method only extracts and uses taxonomy & names values
		 */

		ITIS entity = new ITIS();

		entity.setTsn(id);

		for (StarRecord rec : dwcArchive) {

			if (Integer.parseInt(rec.core().id()) == id) {
				Taxon taxon = entity.new Taxon();
				ArrayList<VernacularName> vernacularNames = new ArrayList<VernacularName>();

				// Taxon fields (core)

				taxon.parentNameUsageID = rec.core()
						.value(DwcTerm.parentNameUsageID);
				taxon.acceptedNameUsageID = rec.core()
						.value(DwcTerm.acceptedNameUsageID);
				taxon.scientificName = rec.core().value(DwcTerm.scientificName);
				taxon.taxonRank = rec.core().value(DwcTerm.taxonRank);
				taxon.taxonomicStatus = rec.core().value(DwcTerm.taxonomicStatus);

				entity.setTaxon(taxon);

				// Vernacular names fields
				if (rec.hasExtension(GbifTerm.VernacularName)) {
					for (Record extRec : rec.extension(GbifTerm.VernacularName)) {
						VernacularName vn = entity.new VernacularName();
						vn.vernacularName = extRec.value(DwcTerm.vernacularName);
						vn.language = extRec.value(DcTerm.language);
						vernacularNames.add(vn);
					}
					entity.setVernacularNames(vernacularNames);
				}

				return entity;
			}
		}

		/*
		 * try { entity.setTsn(id); Class.forName("com.mysql.jdbc.Driver");
		 * Connection conn; conn = DriverManager.getConnection(DB_HOST, DB_USER,
		 * DB_PASS);
		 * 
		 * conn.setCatalog("itis");
		 * 
		 * String query =
		 * "SELECT * FROM taxonomics_units tu, taxon_authors_lkp ta" +
		 * "WHERE tu.tsn = ? " + "AND tu.taxon_author_id = ta.taxon_author_id ;"
		 * ; PreparedStatement prest = conn.prepareStatement(query);
		 * prest.setInt(1, id);
		 * 
		 * 
		 * ResultSet rs = prest.executeQuery();
		 * 
		 * // Main query while (rs.next()) { String name =
		 * rs.getString("unit_name1") + rs.getString("unit_name3") +
		 * rs.getString("unit_name3") + rs.getString("unit_name4");
		 * entity.setName(name); entity.setUsage(rs.getString("usage"));
		 * entity.setCompletenessRating(rs.getString("completeness_rtng"));
		 * entity.setCredibilityRating(rs.getString("credibility_rtng"));
		 * entity.setRevisionYear(rs.getString("currency_rating"));
		 * entity.setTaxonAuthor(rs.getString("usage"));
		 * entity.setKingdom(getKingdomName(rs.getString("kingdom_id")));
		 * entity.setTaxonRank(getRankName(rs.getString("rank_id"),
		 * entity.getKingdom()));
		 * entity.setParentTaxon(rs.getString("parent_tsn"));
		 * 
		 * }
		 * 
		 * conn.close();
		 * 
		 * } catch (SQLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ClassNotFoundException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 */
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
			case "Protozoa":
			case "Animalia":
				return "Infraphylum";
			case "Plantae":
			case "Chromista":
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
			case "Protozoa":
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
			case "Protozoa":
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
			case "Chromista":
			case "Fungi":
				return "Section";
			default:
				break;
			}
			break;
		case "210":
			switch (kingdom) {
			case "Plantae":
			case "Chromista":
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
	public void write(ITIS itisItem, String path) {
		try (OutputStreamWriter osw = new OutputStreamWriter(
				new FileOutputStream(path, true), "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter w = new PrintWriter(bw)) {

			w.println("\titis:" + itisItem.getTsn());

			// Taxon node
			w.println("\t\tdwc:Taxon [");
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID",
					itisItem.getTaxon().parentNameUsageID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:acceptedNameUsageID",
					itisItem.getTaxon().acceptedNameUsageID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:scientificName",
					itisItem.getTaxon().scientificName, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank",
					itisItem.getTaxon().taxonRank, true, false, false);

			Linker.printPropertyValue(w, 3, "dwc:taxonomicStatus",
					itisItem.getTaxon().taxonomicStatus, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank",
					itisItem.getTaxon().taxonRank, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID ",
					itisItem.getTaxon().parentNameUsageID, true, true, false);

			char endChar = (itisItem.getVernacularNames() == null || itisItem.getVernacularNames().isEmpty()) ? '.' : ';';
			w.println("\t\t] " + endChar);

			int num = 0;

			// Vernacular names node
			if (itisItem.getVernacularNames() != null) {
				for (VernacularName vn : itisItem.getVernacularNames()) {
					num++;
					w.println("\t\tgbif:vernacularName [");
					Linker.printPropertyValue(w, 3, "dwc:vernacularName",
							vn.vernacularName, true, false, false);
					Linker.printPropertyValue(w, 3, "dc:language", vn.language, true,
							true, false);

					boolean last = (num == itisItem.getVernacularNames().size());
					w.println("\t\t] " + (last ? '.' : ';'));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
