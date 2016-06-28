package dataset_processors;

import java.io.PrintWriter;

public abstract class Linker<T> {

	public abstract T get(int id);

	public abstract void write(T item, String storePath);

	/**
	 * Print property and value for a given Rdf entity in specified file.
	 * 
	 * @param w
	 *            : Writer for rdf file
	 * @param i
	 *            : number of indentation tabulation at beginning of the line
	 * @param end
	 *            : last property for this entity ?
	 * @param linked
	 *            : is the value a linked value or a string literal? (basically
	 *            : does it need quote-escaping?)
	 */
	public static boolean printPropertyValue(PrintWriter w, int i, String property,
			String value, boolean collection, boolean end, boolean linked) {

		if (value != null) {
			// Tabulations
			for (int j = 1; j <= i; j++) {
				w.print("\t");
			}

			// Property
			w.print(property);

			// Value

			if (linked) {
				w.print("\t" + value);
			} else {
				value = value.replace("\"", "\\\""); // Escape double quotes
				// inside the string
				w.print("\t\"" + value + "\"");
			}

			if (end) {
				if (collection) {
					w.print(" \n");
				} else {
					w.print(" .\n");
				}
			} else {
				w.print(" ;\n");
			}
			
			return true;
		} else {
			return false;
		}
	}

}
