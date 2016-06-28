package dataset_processors;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import dataset_models.EOL;
import dataset_models.EOL.Measurement;
import dataset_models.EOL.VernacularName;

public class EOLLinker extends Linker<EOL> {

	@Override
	public EOL get(int id) {
		ReadContext ctx;
		EOL entity = new EOL();
		try {
			// System.out.println(id);

			String json = IOUtils.toString(
					new URL("http://eol.org/api/traits/" + id
							+ "?key=8829118179928b2fa26f116131b0acbfe10ce763"),
					"UTF-8");
			ctx = JsonPath.parse(json);

			entity.setId(id);

			// Taxon rank
			List<String> nodeList = ctx.read("$..dwc:taxonRank");
			entity.setTaxonRank(nodeList.get(0));

			// Parent id
			nodeList = ctx.read("$..dwc:parentNameUsageID");
			if (nodeList.size() > 0) {
				entity.setTaxonRank(nodeList.get(0));
			}

			// Vernacular names
			List<LinkedHashMap<String, Object>> names = ctx
					.read("$.*[?(@.@type == 'gbif:VernacularName')]");
			ArrayList<VernacularName> nameNodes = new ArrayList<VernacularName>();
			for (LinkedHashMap<String, Object> name : names) {
				VernacularName vn = entity.fillName(name);
				nameNodes.add(vn);
			}
			entity.setVernacularNames(nameNodes);

			// Measurements
			List<LinkedHashMap<String, String>> measurements = ctx
					.read("$.*[?(@.@type == 'dwc:MeasurementOrFact')]");
			ArrayList<Measurement> measures = new ArrayList<Measurement>();
			for (LinkedHashMap<String, String> measure : measurements) {
				Measurement m = entity.fillMeasure(measure);
				measures.add(m);
			}
			entity.setMeasures(measures.toArray(new Measurement[measures.size()]));

		} catch (IOException e) {
			// No data for this id
		}

		return entity;
	}

	/*
	 * Method that decides if the property is to be written in the RDF store
	 * file, and how to write it.
	 */
	@Override
	public void write(EOL eolItem, String path) {

		try (OutputStreamWriter osw = new OutputStreamWriter(
				new FileOutputStream(path, true), "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter w = new PrintWriter(bw)) {

			String entity = "eol:" + eolItem.getId();

			// Taxon node
			w.println("\t" + entity);
			w.println("\t\tdwc:Taxon [");
			Linker.printPropertyValue(w, 3, "dwc:scientificName",
					eolItem.getScientificName(), true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank", eolItem.getTaxonRank(),
					true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID",
					eolItem.getParentId(), true, true, false);
			char endChar = ( (eolItem.getVernacularNames() == null || eolItem.getVernacularNames().isEmpty()) &&
					(eolItem.getMeasures() == null || eolItem.getMeasures().length == 0) ? '.' : ';');
			w.println("\t\t] " + endChar);

			endChar = (eolItem.getMeasures() == null || eolItem.getMeasures().length == 0) ? '.' : ';';
			int num = 0;

			// Vernacular names node
			if (eolItem.getVernacularNames() != null) {
				for (VernacularName vernacularName : eolItem.getVernacularNames()) {
					num++;
					w.println("\t\tgbif:vernacularName [");
					Linker.printPropertyValue(w, 3, "dwc:vernacularName",
							vernacularName.name, true, false, false);
					Linker.printPropertyValue(w, 3, "gbif:isPreferredName",
							Boolean.toString(vernacularName.isPreferredName), true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:taxonID",
							"http://eol.org/pages/" + eolItem.getId(), true, true, false);
					boolean last = (num == eolItem.getVernacularNames().size());
					w.println("\t\t] " + (last ? endChar : ';'));
				}
			}

			// Measures node
			num = 0;
			if (eolItem.getMeasures() != null) {
				for (Measurement m : eolItem.getMeasures()) {
					num++;
					w.println("\t\tdwc:MeasurementOrFact [");
					Linker.printPropertyValue(w, 3, "dwc:taxonID",
							"http://eol.org/pages/" + eolItem.getId(), true, false, false);
					Linker.printPropertyValue(w, 3, "dc:source", m.source, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:measurementMethod", m.method,
							true, false, false);
					Linker.printPropertyValue(w, 3, "dc:bibliographicCitation",
							m.citation, true, false, false);
					Linker.printPropertyValue(w, 3, "eol_terms:statisticalMethod",
							m.statMethod, true, false, false);
					Linker.printPropertyValue(w, 3, ":predicate", m.type, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:measurementType", m.typeURI,
							true, false, false);
					Linker.printPropertyValue(w, 3, ":units", m.units, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:measurementUnit", m.unitsURI,
							true, false, false);
					Linker.printPropertyValue(w, 3, ":value", m.value, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:measurementValue", m.valueURI,
							true, true, false);

					if (num == eolItem.getMeasures().length) {
						endChar = '.';
					} else {
						endChar = ';';
					}
					w.println("\t\t] " + endChar);
				}
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

		// System.out.println(eolItem);

		// Taxon rank already available
		// --> check if same value or more/less precise value

		// Same for parent taxon, already available
		// --> check value

		// Same for scientific name, already available
		// --> check value

		// Write measurements
		// --> Group them ? "hasMeasurement" + same DWC syntax ?

		// Write vernacular names
		// --> How to get and specify language? Necessary?

		// Write related taxons
		// --> How to get their wikidata ID?

	}

}
