package rdf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import wikidata.examples.ExampleHelpers;

public class JsonLdReader {
	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		
		Model model = ModelFactory.createDefaultModel();
		
		try {
			// Input
			System.out.println("INPUT");
			model.read("rdf/store/wikidata_bio.ttl", "TURTLE");
			
			// Output
			System.out.println("OUTPUT");
			model.write(new BufferedWriter(new FileWriter(new File("wikidata_bio_turtled.ttl"))), "TURTLE");
			
			System.out.println("Done!");
		} catch (IOException e) {
			System.out.println("How can this ever fail anyway");
		}
	}
}
