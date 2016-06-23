package coverage.processors;

import java.util.HashMap;
import java.util.Map;

import wikidata.examples.ExampleHelpers;

public class GBIFWikidataProcessor {
	
	class UsageStatistics {
		long count = 0;

		final Map<String, Integer> sources = new HashMap<>();
	}

	UsageStatistics recordStatistics = new UsageStatistics();

	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		GBIFWikidataProcessor processor = new GBIFWikidataProcessor();
		//SourceProcessor sourceProc = new SourceProcessor();
		//BiodiversityProcessor bioProc = new BiodiversityProcessor();
		
		
		System.out.println("Reading data...");
		processor.readData();
		System.out.println("Writing results...");
		processor.writeResults("Wikidata_GBIF_coverage.csv");
		
		System.out.println("Done! " + processor.recordStatistics.count + " items were processed.");
	}

	private void writeResults(String string) {
		// TODO Auto-generated method stub
		
	}

	private void readData() {
		// TODO Auto-generated method stub
		
	}
}
