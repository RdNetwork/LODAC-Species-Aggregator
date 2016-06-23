package gbif.processors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import wikidata.examples.ExampleHelpers;

/**
 * Processor class used to filter and parse GBIF dump files to extract the
 * datasets it uses to fetch and provide its data.
 * 
 * @author R�my Delanaux
 *
 */

public class SourceProcessor {

	class UsageStatistics {
		long count = 0;
		final Set<String> species = new TreeSet<>();
		final Map<String, Integer> sources = new HashMap<>();
	}

	UsageStatistics recordStatistics = new UsageStatistics();

	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		SourceProcessor processor = new SourceProcessor();

		System.out.println("Reading data...");
		processor.readData();
		System.out.println("Writing results...");
		processor.writeResults("GBIF_results.csv");
		processor.writeSpecies("GBIF_species.txt");
		
		System.out.println("Done! " + processor.recordStatistics.count + " items were processed.");
	}

	private void readData() {
		Reader in;
		try {
			in = new FileReader("D:/shared_with_vm/dump/gbif/taxon.txt");
			Iterable<CSVRecord> records = CSVFormat.TDF.withQuote(null).parse(in);

			for (CSVRecord record : records) {
				this.recordStatistics.count += 1;
				String source = record.get(12);
				this.recordStatistics.species.add(record.get(4));
				
				if (!this.recordStatistics.sources.containsKey(source)) {
					this.recordStatistics.sources.put(source, 1);
				} else {
					this.recordStatistics.sources.put(source, (this.recordStatistics.sources.get(source) + 1));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File not found!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to parse!");
		}
	}

	private void writeResults(String fileName) {
		try (PrintStream out = new PrintStream(ExampleHelpers.openExampleFileOuputStream(fileName))) {
			out.println("Data source\tNumber of references to this source\tPercentage among all items");

			for (Entry<String, Integer> entry : recordStatistics.sources.entrySet()) {
				if (entry.getKey() != null) {
					double percent = entry.getValue() * 100.0 / recordStatistics.count;
					DecimalFormat numberFormat = new DecimalFormat("#.###");
					out.println(entry.getKey() + "\t" + entry.getValue() + "\t" + numberFormat.format(percent));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void writeSpecies(String fileName) {
		try (PrintStream out = new PrintStream(ExampleHelpers.openExampleFileOuputStream(fileName))) {

			for (String species : recordStatistics.species) {
				out.println(species);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
