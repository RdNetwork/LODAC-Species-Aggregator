package rdf;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

public class RdfHelpers {

	public static Dataset initTDB() {
		  // Assembler way: Make a TDB-back Jena model in the named directory.
		  // This way, you can change the model being used without changing the code.
		  // The assembler file is a configuration file.
		  // The same assembler description will work in Fuseki.
		  final String assemblerFile = "rdf/store/tdb-assembler.ttl" ;
		  
		  return TDBFactory.assembleDataset(assemblerFile) ;
		  
		 
	}
}
