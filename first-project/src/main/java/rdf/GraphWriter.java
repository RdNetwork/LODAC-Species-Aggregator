package rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

import wikidata.examples.ExampleHelpers;

public class GraphWriter {

	public final static String baseURL = "http://127.0.0.1/";
	
	/*
	 * Model initilization standard method
	 */
	public static Model init() {
		return ModelFactory.createDefaultModel();
	}
	
	
	/*
	 * Writes a WikiData in the graph model with no check, using 
	 * every statement found in the entity.
	 */
	public static void writeWikiSpecies(Model m, ItemDocument itemDoc) {
		Resource species = m.createResource(baseURL + itemDoc.getEntityId().getId());
		
		for (StatementGroup statementGroup : itemDoc.getStatementGroups()) {
			writeWikiProperty(species, statementGroup);
		}
	}
	
	
	public static void writeWikiProperty(Resource r, StatementGroup sg) {
		Property prop = ResourceFactory.createProperty(sg.getProperty().getIri());
		r.addProperty(prop, ExampleHelpers.getStringProperty(sg));
	}
	


}
