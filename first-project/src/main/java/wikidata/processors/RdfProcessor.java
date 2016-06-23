package wikidata.processors;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.jena.query.Dataset;
import org.json.JSONException;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import dataset_models.Dyntaxa;
import dataset_models.EOL;
import dataset_models.GBIF;
import dataset_models.ITIS;
import dataset_models.NCBI;
import dataset_processors.DyntaxaLinker;
import dataset_processors.EOLLinker;
import dataset_processors.GBIFLinker;
import dataset_processors.ITISLinker;
import dataset_processors.Linker;
import dataset_processors.NCBILinker;
import wikidata.examples.ExampleHelpers;

/**
 * Processor prototype for filtering biological data and compute relationships
 * between external biology datasets.
 * 
 * @author R�my Delanaux with some pieces taken from Markus Kroetzsch's examples
 *
 */

public class RdfProcessor implements EntityDocumentProcessor {

	static Dataset dataset;
	final static String storePath = "rdf/store/";
	int progress = 0, progressBio = 0;

	/**
	 * Main method. Processes the whole dump using this processor and writes the
	 * results to a file.
	 */
	public static void main(String[] args) throws IOException {
		// Initialization
		ExampleHelpers.configureLogging();

		System.out.println("Initialization...");
		initStores();

		// Processing
		System.out.println("Starting processing...");
		RdfProcessor processor = new RdfProcessor();
		ExampleHelpers.processEntitiesFromWikidataDump(processor);

		// Output
		closeStores();
	}

	@Override
	public void processPropertyDocument(PropertyDocument propDoc) {
		// Processing for a each property of a dump listing properties
	}

	@Override
	public void processItemDocument(ItemDocument itemDoc) {

		progress++;

		// Processing for each item of a dump modeling entities
		boolean bio = false;

		// Determine if the item is a biological one
		for (StatementGroup statementGroup : itemDoc.getStatementGroups()) {
			if (statementGroup.getProperty().getId().equals("P31")) {
				bio = ExampleHelpers.isBiological(statementGroup);
			}
		}

		// If it is, we analyze its fields
		if (bio) {
			progressBio++;
			if (progress >= 25000 || progressBio >= 10000) {
				System.out
						.println("Number of required entites reached ; quitting...");
				closeStores();
				System.exit(0);
			}

			try (OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(storePath + "wikidata.ttl", true), "UTF-8");
					BufferedWriter bw = new BufferedWriter(osw);
					PrintWriter writer = new PrintWriter(bw)) {

				HashMap<String, String> externalIds = new HashMap<String, String>();

				String itemId = itemDoc.getEntityId().getId();

				writer.println("\twd:" + itemId);
				boolean lastLoop = false;
				int p = 0;
				for (StatementGroup statementGroup : itemDoc.getStatementGroups()) {
					p++;
					if (p == itemDoc.getStatementGroups().size()) {
						if (externalIds.isEmpty()) {
							lastLoop = true;
						}

					}

					String value = getRdfValue(statementGroup);

					if (!value.equals("") && value != null) {
						storeIdentifier(statementGroup, externalIds);

						String propId = statementGroup.getProperty().getId();
						Linker.printPropertyValue(writer, 2, "wdt:" + propId, value,
								lastLoop, true);

						// TODO: write differently depending on type of value
						// (literal, entity, ...)
					}
				}

				writeSameAs(externalIds, writer, itemId);

				fetchRemoteData(externalIds);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void writeSameAs(HashMap<String, String> externalIds, PrintWriter writer,
			String wd) {
		// TODO Auto-generated method stub
		int i = 0;
		for (String dataset : externalIds.keySet()) {
			i++;

			Linker.printPropertyValue(writer, 2, "owl:sameAs",
					dataset + ":" + externalIds.get(dataset),
					(i == externalIds.size()), true);
		}

	}

	/*
	 * Iterates external datasets to fetch their data about this taxon.
	 */
	private void fetchRemoteData(HashMap<String, String> ids)
			throws NumberFormatException, JSONException, IOException {
		for (String id : ids.keySet()) {
			switch (id) {

			/*
			case "eol_ent":
				EOLLinker linkEOL = new EOLLinker();
				EOL eolItem = linkEOL.get(Integer.parseInt(ids.get(id)));
				linkEOL.write(eolItem, storePath + "eol.ttl");
				break;

			case "ncbi":
				NCBILinker linkNCBI = new NCBILinker();
				NCBI ncbiItem = linkNCBI.get(Integer.parseInt(ids.get(id)));
				linkNCBI.write(ncbiItem, storePath + "ncbi.ttl");
				break;
			*/
			
			case "gbif_ent":
				GBIFLinker linkGBIF = new GBIFLinker();
				GBIF gbifItem = linkGBIF.get(Integer.parseInt(ids.get(id)));
				linkGBIF.write(gbifItem, storePath + "gbif.ttl");
				break;

				
//			case "dynt":
//				DyntaxaLinker linkDyntaxa = new DyntaxaLinker();
//				Dyntaxa dyntaxaItem = linkDyntaxa.get(Integer.parseInt(ids.get(id)));
//				linkDyntaxa.write(dyntaxaItem, storePath + "dynt.ttl");
//				break;

//			case "feuro":
//				break;
				
//			case "itis":
//				ITISLinker linkITIS = new ITISLinker();
//				ITIS itisItem = linkITIS.get(Integer.parseInt(ids.get(id)));
//				linkITIS.write(itisItem, storePath + "itis.ttl");
//				break;

			default:
				break;
			}
		}
	}

	/*
	 * Helper to store external datasets identifiers
	 */
	private void storeIdentifier(StatementGroup statementGroup,
			HashMap<String, String> ids) {

		String value_untrimmed = getRdfValue(statementGroup);
		String value = value_untrimmed.substring(1, value_untrimmed.length() - 1);

		switch (statementGroup.getProperty().getId()) {
		case "P815":
			// ITIS
			ids.put("itis", value);
			break;
		case "P830":
			// EOL
			ids.put("eol_ent", value);
			break;
		case "P1939":
			// Dyntaxa
			ids.put("dynt", value);
			break;
		case "P1895":
			// FaunaEuropea
			ids.put("feuro", value);
			break;
		case "P846":
			// GBIF
			ids.put("gbif_ent", value);
			break;
		case "P685":
			// NCBI
			ids.put("ncbi", value);
			break;
		default:
			break;
		}

	}

	private String getRdfValue(StatementGroup sg) {

		for (Statement s : sg.getStatements()) {
			if (s.getClaim().getMainSnak() instanceof ValueSnak) {
				Value v = ((ValueSnak) s.getClaim().getMainSnak()).getValue();
				if (v instanceof EntityIdValue) {
					return "wd:" + ((EntityIdValue) v).getId();
				}
				// if (v instanceof StringValue) {
				String inter = ExampleHelpers.getStringProperty(sg).replace("\n",
						" ");
				return "\"" + inter.replace("\"", "\\\"") + "\"";
				// }
			}
		}

		return "";
	}

	private static void initStore(String dataset) {
		try (BufferedWriter writer = Files
				.newBufferedWriter(Paths.get(storePath + dataset + ".ttl"))) {
			writer.write(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
			writer.newLine();
			writer.write("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .");
			writer.newLine();
			writer.write("@prefix owl: <http://www.w3.org/2002/07/owl#> .");
			writer.newLine();
			writer.write("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .");
			writer.newLine();
			writer.write("@prefix wd: <http://www.wikidata.org/entity/> .");
			writer.newLine();
			writer.write("@prefix wdt: <http://www.wikidata.org/prop/direct/> .");
			writer.newLine();
			writer.write("@prefix gbif_ent: <http://www.gbif.org/species/> .");
			writer.newLine();
			writer.write("@prefix gbif: <http://rs.gbif.org/terms/1.0/> .");
			writer.newLine();
			writer.write("@prefix eol_ent: <http://www.eol.org/pages/> .");
			writer.newLine();
			writer.write("@prefix eol: <http://eol.org/schema/> .");
			writer.newLine();
			writer.write(
					"@prefix ncbi: <https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=> .");
			writer.newLine();
			writer.write("@prefix dwc: <http://rs.tdwg.org/dwc/terms/> .");
			writer.newLine();
			writer.write("@prefix dc: <http://purl.org/dc/terms/> .");
			writer.newLine();
			writer.write("@prefix iucn: <http://iucn.org/terms/> .");
			writer.newLine();
			writer.write(
					"@prefix feuro: <http://www.faunaeur.org/full_results.php?id=> .");
			writer.newLine();
			writer.write("@prefix dynt: <https://www.dyntaxa.se/taxon/info/> .");
			writer.newLine();
			writer.write("@prefix lodac: <localhost/> .");
			writer.newLine();

			writer.write("lodac:" + dataset + " {");
			writer.newLine();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void closeStore(String dataset) {
		try (FileWriter fw = new FileWriter(storePath + dataset + ".ttl", true);
			BufferedWriter file = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(file)) {
			out.println("}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void initStores() {
		initStore("wikidata");
		//initStore("eol");
		GBIFLinker.init();
		initStore("gbif");
		//initStore("ncbi");
		//initStore("itis");
		DyntaxaLinker.init();
		initStore("dynt");

	}

	private static void closeStores() {
		closeStore("wikidata");
		//closeStore("eol");
		closeStore("gbif");
		//closeStore("ncbi");
		//closeStore("itis");
		closeStore("dynt");
	}

}
