package dataset_processors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dataset_models.Dyntaxa;
import dataset_models.Dyntaxa.Synonym;
import wikidata.processors.RdfProcessor;

public class DyntaxaLinker extends Linker<Dyntaxa> {

	private static XSSFWorkbook wb;
	private static OPCPackage pkg;

	public static void init() {
		// Open xlsx file
		// Use a file (rather than InputStream)

		try {
			pkg = OPCPackage
					.open(new File(RdfProcessor.dumpPath + "dyntaxa/Biota.xlsx"));
			wb = new XSSFWorkbook(pkg);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Dyntaxa get(int id) {
		Dyntaxa entity = new Dyntaxa();

		// Search the cell with the correct taxon id
		for (Sheet sheet : wb) {
			for (Row row : sheet) {
				if (row.getRowNum() != 0) {
					// CellReference cellRef = new
					// CellReference(row.getRowNum(), 0);
					// System.out.println("Cell" +
					// cellRef.formatAsString());

					Cell cell = row.getCell(0);
					int taxonId = -1;
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						taxonId = Integer
								.parseInt(cell.getRichStringCellValue().getString());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (!DateUtil.isCellDateFormatted(cell)) {
							taxonId = (int) cell.getNumericCellValue();
						}
						break;
					default:
						System.out.println("Wrong cell");
						break;
					}

					// System.out.println(taxonId + " - " + id);

					if (taxonId == id) {

						// Column 0 : Id
						entity.setId(id);

						// Column 1 : Taxon rank
						entity.setTaxonRank(getCellStringValue(row, 1));

						// Column 2 : Scientific name
						entity.setScientificName(getCellStringValue(row, 2));

						// Column 3 : Author
						entity.setAuthor(getCellStringValue(row, 3));

						// Column 4 : Common swedish name
						entity.setCommonName(getCellStringValue(row, 4));

						// Column 5 : GUID (LSID)
						entity.setGUID(getCellStringValue(row, 5));

						// Column 6 : Recommended GUID/LSID
						entity.setRecommendedGUID(getCellStringValue(row, 6));

						// Column 8 : Syonyms searated by ";"
						cell = row.getCell(8);

						if (cell != null && cell.getRichStringCellValue() != null) {
							ArrayList<String> synText = new ArrayList<String>(
									Arrays.asList(cell.getRichStringCellValue()
											.getString().split(";")));

							ArrayList<Synonym> synonyms = new ArrayList<Synonym>();
							for (String s : synText) {
								Synonym syn = entity.new Synonym();
								String[] inter = s.split("\\(");
								syn.name = inter[0];
								if (inter.length > 1) {
									syn.origin = inter[1]
											.substring(0, inter[1].length()).trim();
								}
								synonyms.add(syn);
							}
							entity.setSynonyms(synonyms);

						}
						return entity;
					}
				}
			}
		}

		// TODO Auto-generated method stub
		return entity;
	}

	private String getCellStringValue(Row row, int i) {
		Cell cell = row.getCell(i);

		if (cell == null || cell.getRichStringCellValue() == null) {
			return null;
		}

		return cell.getRichStringCellValue().getString();
	}

	@Override
	public void write(Dyntaxa dyntItem, String path) {
		// TODO Auto-generated method stub

		// Equivalences with Darwin Core :
		// https://lampetra2-1.artdata.slu.se/ArtDatabankenSOA/Client/html/920a787c-5f51-0c54-14d1-808b8b086448.htm

		try (OutputStreamWriter osw = new OutputStreamWriter(
				new FileOutputStream(path, true), "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter w = new PrintWriter(bw)) {

			// Taxon 0 has no properties in Dyntaxa
			if (dyntItem.getId() != 0) {
				
				String entity = "dynt:" + dyntItem.getId();
				ArrayList<Synonym> syns = dyntItem.getSynonyms();
				w.println("\t" + entity);

				Linker.printPropertyValue(w, 2, "dynt:taxonRank",
						dyntItem.getTaxonRank(), false, false, false);
				Linker.printPropertyValue(w, 2, "dynt:scientificName",
						dyntItem.getScientificName(), false, false, false);
				Linker.printPropertyValue(w, 2, "dynt:author", dyntItem.getAuthor(),
						false, false, false);
				Linker.printPropertyValue(w, 2, "dynt:GUID", dyntItem.getGUID(),
						false, false, false);
				Linker.printPropertyValue(w, 2, "dynt:recommendedGUID",
						dyntItem.getRecommendedGUID(), false, (syns.isEmpty()), false);

				if (!syns.isEmpty()) {
					w.println("\t\tdynt:synonym [");
					int p = 0;
					for (Synonym synonym : syns) {
						p++;
						Linker.printPropertyValue(w, 3, "dynt:synonymName",
								synonym.name, true, false, false);
						Linker.printPropertyValue(w, 3, "dynt:synonymOrigin",
								synonym.origin, true, (p == syns.size()), false);
					}
					w.println("\t\t].");
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

	}

}
