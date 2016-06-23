package wikidata.processors;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import wikidata.examples.ExampleHelpers;

/**
 * Processor prototype for filtering biological data and compute relationships
 * between external biology datasets.
 * 
 * @author Rémy Delanaux with some pieces taken from Markus Kroetzsch's examples
 *
 */

public class BiodiversityProcessor implements EntityDocumentProcessor {

	/**
	 * Simple record class to keep track of some usage numbers for one type of
	 * entity. Taken from Wikidata Toolkit examples.
	 */
	class UsageStatistics {
		long count = 0;
		long countStatements = 0;
		long countReferencedStatements = 0;
		long countEntities = 0;

		// Map to store the number of fields for each dataset
		// A Dataset being modeled by an entity, here its ID.
		final HashMap<ItemIdValue, Integer> datasetUsage = new HashMap<>();
		final HashMap<ItemIdValue, Integer> identifiersPerDataset = new HashMap<>();
		final HashMap<ItemIdValue, Integer> entitiesPerDataset = new HashMap<>();
		final HashMap<PropertyIdValue, Boolean> isIdentifier = new HashMap<>();
		final HashSet<String> identifiers = new HashSet<>();
		final Set<String> species = new TreeSet<>();
	}

	UsageStatistics itemStatistics = new UsageStatistics();

	/**
	 * Main method. Processes the whole dump using this processor and writes the
	 * results to a file.
	 */
	public static void main(String[] args) throws IOException {
		// Initialization
		ExampleHelpers.configureLogging();
		File f = new File("results/wikidatawiki-20160314/taxonData.csv");
		try {
			System.out.println(Files.deleteIfExists(f.toPath()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Processing
		BiodiversityProcessor processor = new BiodiversityProcessor();
		ExampleHelpers.processEntitiesFromWikidataDump(processor);

		// Output
		processor.writeFinalResults();
	}

	@Override
	public void processPropertyDocument(PropertyDocument propDoc) {
		// Processing for a each property of a dump listing properties
	}

	@Override
	public void processItemDocument(ItemDocument itemDoc) {
		
		fillIdentifiers(itemStatistics);
		

		// Processing for each item of a dump modeling entities
		this.itemStatistics.countEntities += 1;
		boolean bio = false/*, relevant = false*/;

		// Determine if the item is a biological one and relevant one
		for (StatementGroup statementGroup : itemDoc.getStatementGroups()) {
			if (statementGroup.getProperty().getId().equals("P31")) {
				bio = ExampleHelpers.isBiological(statementGroup);
			}
			if (statementGroup.getProperty().getId().equals("P225")) {
				this.itemStatistics.species.add(ExampleHelpers.getStringProperty(statementGroup));
			}
		}

		// If it is, we analyze its fields
		if (bio /* && relevant */ ) {

			for (StatementGroup statementGroup : itemDoc.getStatementGroups()) {
				// Count statements
				this.itemStatistics.countStatements += statementGroup.getStatements().size();

				// Separating identifiers from other statements
				boolean isId = this.itemStatistics.identifiers.contains(statementGroup.getProperty().getId());

				for (Statement s : statementGroup.getStatements()) {
					if (s.getReferences().isEmpty()) {
						// Case where the field has no references
						addSource(this.itemStatistics, null);
						this.itemStatistics.datasetUsage.put(null, this.itemStatistics.datasetUsage.get(null) + 1);
						if (isId) {
							this.itemStatistics.identifiersPerDataset.put(null,
									this.itemStatistics.identifiersPerDataset.get(null) + 1);
						}
					} else

					{
						this.itemStatistics.countReferencedStatements++;
						countReferences(s, isId);
					}
				}
			} 

		}
		
	}

	/**
	 * Method that browses a statement to analyze its references and check
	 * whether or not they count in the analysis.
	 */
	private void countReferences(Statement s, boolean isId) {

		boolean referenced = false;
		ArrayList<ItemIdValue> datasetReferenced = new ArrayList<>();

		for (Reference r : s.getReferences()) {
			for (SnakGroup snakGroup : r.getSnakGroups()) {
				// Looking for "stated in" property in the references
				if (snakGroup.getProperty().getId().equals("P248")) {
					for (Snak sn : snakGroup.getSnaks()) {
						if (sn instanceof ValueSnak && sn.getValue() != null) {
							referenced = true;
							ItemIdValue entityValue = (ItemIdValue) sn.getValue();
							addSource(this.itemStatistics, entityValue);

							// statements per dataset
							this.itemStatistics.datasetUsage.put(entityValue,
									this.itemStatistics.datasetUsage.get(entityValue) + 1);

							// entities per dataset
							if (!(datasetReferenced.contains(entityValue))) {
								datasetReferenced.add(entityValue);
								this.itemStatistics.entitiesPerDataset.put(entityValue,
										this.itemStatistics.entitiesPerDataset.get(entityValue) + 1);
							}

							// identifiers
							if (isId) {
								this.itemStatistics.identifiersPerDataset.put(entityValue,
										this.itemStatistics.identifiersPerDataset.get(entityValue) + 1);
							}
						}
					}
				}
			}
		}

		if (!referenced) {
			// If we found no 'stated in' property,
			// we treat it as if there were no reference.
			addSource(this.itemStatistics, null);
			this.itemStatistics.datasetUsage.put(null, this.itemStatistics.datasetUsage.get(null) + 1);
		}

	}

	/**
	 * Initializes the counters for an entity to zero if not done yet.
	 */
	private void addSource(UsageStatistics usageStatistics, ItemIdValue entity) {
		if (!usageStatistics.datasetUsage.containsKey(entity)) {
			usageStatistics.datasetUsage.put(entity, 0);
		}
		if (!usageStatistics.entitiesPerDataset.containsKey(entity)) {
			usageStatistics.entitiesPerDataset.put(entity, 0);
		}
		if (!usageStatistics.identifiersPerDataset.containsKey(entity)) {
			usageStatistics.identifiersPerDataset.put(entity, 0);
		}
	}

	/**
	 * Prints and stores final result of the processing. This should be called
	 * after finishing the processing of a dump. It will print the statistics
	 * gathered during processing and it will write a CSV file with usage counts
	 * for every property.
	 */
	private void writeFinalResults() {

		System.out.println("Total biological entities: " + this.itemStatistics.countEntities);
		System.out.println("Total statements from biological entities: " + this.itemStatistics.countStatements);
		System.out.println(
				"Referenced statements in biological entities: " + this.itemStatistics.countReferencedStatements);
		// Store property counts in files:
		writeStatisticsToFile(this.itemStatistics, "dataset-counts.csv");

		writeSpeciesToFile(this.itemStatistics, "species.txt");

		// Write relation analysis between fields
		writeAnalysis(this.itemStatistics, "dataset-analysis.csv");
		// TODO
	}

	/**
	 * Prints and stores the analysis made with the dataset statistics.
	 */
	private void writeAnalysis(UsageStatistics itemStatistics, String filename) {
		// TODO

	}

	/**
	 * Stores the gathered usage statistics about property uses to a CSV file.
	 */
	private void writeStatisticsToFile(UsageStatistics usageStatistics, String fileName) {
		try (PrintStream out = new PrintStream(ExampleHelpers.openExampleFileOuputStream(fileName))) {
			out.println("Dataset entity id\tStatement references to this dataset" + "\t% of entities using this dataset"
					+ "\tIDs from this dataset\t% of identifiers in references");

			for (Entry<ItemIdValue, Integer> entry : usageStatistics.datasetUsage.entrySet()) {
				int total = entry.getValue();
				int totalId = usageStatistics.identifiersPerDataset.get(entry.getKey());
				DecimalFormat numberFormat = new DecimalFormat("#.###");
				double percent = usageStatistics.entitiesPerDataset.get(entry.getKey()) * 100.0
						/ usageStatistics.countEntities;
				double percentId = totalId * 100.0 / total;
				if (entry.getKey() != null) {
					out.println(entry.getKey().toString() + "\t" + total + "\t" + numberFormat.format(percent) + "\t"
							+ numberFormat.format(percentId) + "\t" + totalId + "\t");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeSpeciesToFile(UsageStatistics usageStatistics, String fileName) {
		try (PrintStream out = new PrintStream(ExampleHelpers.openExampleFileOuputStream(fileName))) {

			for (String species : usageStatistics.species) {
				out.println(species);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * Helper method that tests if the entity (statement group) is relevant,
	 * i.e. if we have to consider it in our reference & dataset analysis
	 */
	/*
	private boolean isRelevant(StatementGroup sg) {
		// Example: keep only statements having a GBIF id

		for (Statement s : sg.getStatements()) {
			if (!(s.getReferences().isEmpty())) {
				for (Reference r : s.getReferences()) {
					for (SnakGroup snakGroup : r.getSnakGroups()) {
						if (snakGroup.getProperty().getId().equals("P248")) {
							for (Snak sn : snakGroup.getSnaks()) {
								if (sn instanceof ValueSnak && sn.getValue() != null
										&& sn.getValue() instanceof ItemIdValue) {
									ItemIdValue entityValue = (ItemIdValue) sn.getValue();

									if (entityValue.getId().equals("Q1531570")) {
										return true;
									}
								}
							}
						}

					}
				}
			}
		}
		return false;
	}
	*/

	/**
	 * Helper method that fills a map to tell which properties are external
	 * identifiers in the Wikidata dump.
	 */
	private void fillIdentifiers(UsageStatistics us) {
		/*
		 * us.identifiers.add("P646"); us.identifiers.add("P685");
		 * us.identifiers.add("P846"); us.identifiers.add("P1939");
		 * us.identifiers.add("P1895"); us.identifiers.add("P830");
		 * us.identifiers.add("P508"); us.identifiers.add("P227");
		 * us.identifiers.add("P349"); us.identifiers.add("P815");
		 * us.identifiers.add("P1417"); us.identifiers.add("P1014");
		 * us.identifiers.add("P1051"); us.identifiers.add("P959");
		 * us.identifiers.add("P850"); us.identifiers.add("P938");
		 */
		List<String> lines = new ArrayList<>();
		try {
			lines = Files.readAllLines(Paths.get("D:/shared_with_vm/dump/wikidata/id.txt"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : lines) {
			us.identifiers.add(line);
		}

	}
}
