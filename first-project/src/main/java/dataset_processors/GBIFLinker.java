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

import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.IucnTerm;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveFactory;
import org.gbif.dwca.io.UnsupportedArchiveException;
import org.gbif.dwca.record.Record;
import org.gbif.dwca.record.StarRecord;

import dataset_models.GBIF;
import dataset_models.GBIF.Distribution;
import dataset_models.GBIF.Multimedia;
import dataset_models.GBIF.Reference;
import dataset_models.GBIF.Taxon;
import dataset_models.GBIF.VernacularName;
import wikidata.processors.RdfProcessor;

public class GBIFLinker extends Linker<GBIF> {

	public static Archive dwcArchive;

	public static void init() {
		File myArchiveFile = new File(
				RdfProcessor.dumpPath + "gbif/backbone-current/");
		try {
			dwcArchive = ArchiveFactory.openArchive(myArchiveFile);
		} catch (UnsupportedArchiveException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public GBIF get(int id) {
		GBIF entity = new GBIF();
		entity.setId(id);
		
		for (StarRecord rec : dwcArchive) {

			if (Integer.parseInt(rec.core().id()) == id) {
				Taxon taxon = entity.new Taxon();
				ArrayList<VernacularName> vernacularNames = new ArrayList<VernacularName>();
				ArrayList<Multimedia> multimediaFiles = new ArrayList<Multimedia>();
				ArrayList<Distribution> distributions = new ArrayList<Distribution>();
				ArrayList<Reference> references = new ArrayList<Reference>();

				// Taxon fields (core)
				
				taxon.taxonID = rec.core().value(DwcTerm.taxonID);
				taxon.datasetID = rec.core().value(DwcTerm.datasetID);
				taxon.parentNameUsageID = rec.core()
						.value(DwcTerm.parentNameUsageID);
				taxon.acceptedNameUsageID = rec.core()
						.value(DwcTerm.acceptedNameUsageID);
				taxon.originalNameUsageID = rec.core()
						.value(DwcTerm.originalNameUsageID);
				taxon.scientificName = rec.core().value(DwcTerm.scientificName);
				taxon.taxonRank = rec.core().value(DwcTerm.taxonRank);
				taxon.nameAccordingTo = rec.core().value(DwcTerm.nameAccordingTo);
				taxon.namePublishedIn = rec.core().value(DwcTerm.namePublishedIn);
				taxon.taxonomicStatus = rec.core().value(DwcTerm.taxonomicStatus);
				taxon.nomenclaturalStatus = rec.core()
						.value(DwcTerm.nomenclaturalStatus);
				taxon.kingdom = rec.core().value(DwcTerm.kingdom);
				taxon.phylum = rec.core().value(DwcTerm.phylum);
				taxon.taxonClass = rec.core().value(DwcTerm.class_);
				taxon.order = rec.core().value(DwcTerm.order);
				taxon.family = rec.core().value(DwcTerm.family);
				taxon.genus = rec.core().value(DwcTerm.genus);
				taxon.taxonRemarks = rec.core().value(DwcTerm.taxonRemarks);

				entity.setTaxon(taxon);

				// Vernacular names fields
				if (rec.hasExtension(GbifTerm.VernacularName)) {
					for (Record extRec : rec.extension(GbifTerm.VernacularName)) {
						VernacularName vn = entity.new VernacularName();
						vn.lifeStage = extRec.value(DwcTerm.lifeStage);
						vn.sex = extRec.value(DwcTerm.sex);
						vn.vernacularName = extRec.value(DwcTerm.vernacularName);
						vn.language = extRec.value(DcTerm.language);
						vn.source = extRec.value(DcTerm.source);
						vn.countryCode = extRec.value(DwcTerm.countryCode);
						vn.country = extRec.value(DwcTerm.country);
						vernacularNames.add(vn);
					}
					entity.setVernacularNames(vernacularNames);
				}

				// Multimedia fields
				if (rec.hasExtension(GbifTerm.Multimedia)) {
					for (Record extRec : rec.extension(GbifTerm.Multimedia)) {
						Multimedia m = entity.new Multimedia();
						m.title = extRec.value(DcTerm.title);
						m.license = extRec.value(DcTerm.license);
						m.creator = extRec.value(DcTerm.creator);
						m.references = extRec.value(DcTerm.references);
						m.contributor = extRec.value(DcTerm.contributor);
						m.description = extRec.value(DcTerm.description);
						m.source = extRec.value(DcTerm.source);
						m.identifier = extRec.value(DcTerm.identifier);
						m.created = extRec.value(DcTerm.created);
						m.publisher = extRec.value(DcTerm.publisher);
						m.rightsHolder = extRec.value(DcTerm.rightsHolder);
						multimediaFiles.add(m);
					}
					entity.setMultimediaFiles(multimediaFiles);
				}

				// Distribution fields
				if (rec.hasExtension(GbifTerm.Distribution)) {
					for (Record extRec : rec.extension(GbifTerm.Distribution)) {
						Distribution d = entity.new Distribution();
						d.countryCode = extRec.value(DwcTerm.countryCode);
						d.lifeStage = extRec.value(DwcTerm.lifeStage);
						d.country = extRec.value(DwcTerm.country);
						d.locationID = extRec.value(DwcTerm.locationID);
						d.establishmentMeans = extRec
								.value(DwcTerm.establishmentMeans);
						d.locality = extRec.value(DwcTerm.locality);
						d.source = extRec.value(DcTerm.source);
						d.threatStatus = extRec.value(IucnTerm.threatStatus);
						d.locationRemarks = extRec.value(DwcTerm.locationRemarks);
						d.occurrenceStatus = extRec.value(DwcTerm.occurrenceStatus);
						distributions.add(d);
					}
					entity.setDistributions(distributions);
				}

				// Distribution fields
				if (rec.hasExtension(GbifTerm.Reference)) {
					for (Record extRec : rec.extension(GbifTerm.Reference)) {
						Reference r = entity.new Reference();
						r.bibliographicCitation = extRec
								.value(DcTerm.bibliographicCitation);
						r.references = extRec.value(DcTerm.references);
						r.source = extRec.value(DcTerm.source);
						r.identifier = extRec.value(DcTerm.identifier);
						references.add(r);
					}
					entity.setReferences(references);
				}
				
				return entity;
			}
		}

		return entity;
	}

	@Override
	public void write(GBIF gbifItem, String path) {

		try (OutputStreamWriter osw = new OutputStreamWriter(
				new FileOutputStream(path, true), "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter w = new PrintWriter(bw)) {

			String entity = "gbif_ent:" + gbifItem.getId();

			// Taxon node
			w.println("\t" + entity);
			w.println("\t\tdwc:Taxon [");
			Linker.printPropertyValue(w, 3, "dwc:taxonID",
					gbifItem.getTaxon().taxonID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:datasetID",
					gbifItem.getTaxon().datasetID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID",
					gbifItem.getTaxon().parentNameUsageID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:acceptedNameUsageID",
					gbifItem.getTaxon().acceptedNameUsageID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:originalNameUsageID",
					gbifItem.getTaxon().originalNameUsageID, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:scientificName",
					gbifItem.getTaxon().scientificName, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank",
					gbifItem.getTaxon().taxonRank, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:nameAccordingTo",
					gbifItem.getTaxon().nameAccordingTo, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:namePublishedIn",
					gbifItem.getTaxon().namePublishedIn, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonomicStatus",
					gbifItem.getTaxon().taxonomicStatus, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:nomenclaturalStatus",
					gbifItem.getTaxon().nomenclaturalStatus, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:kingdom",
					gbifItem.getTaxon().kingdom, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:phylum", gbifItem.getTaxon().phylum,
					 true,false, false);
			Linker.printPropertyValue(w, 3, "dwc:class",
					gbifItem.getTaxon().taxonClass, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:order", gbifItem.getTaxon().order,
					 true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:family", gbifItem.getTaxon().family,
					 true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:genus", gbifItem.getTaxon().genus,
					 true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRemarks",
					gbifItem.getTaxon().taxonRemarks, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank",
					gbifItem.getTaxon().taxonRank, true, false, false);
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID ",
					gbifItem.getTaxon().parentNameUsageID, true, true, false);

			char endChar = (gbifItem.getVernacularNames() == null || gbifItem.getVernacularNames().isEmpty()) ? '.' : ';';
			w.println("\t\t] " + endChar);

			endChar = (gbifItem.getDistributions() == null  || gbifItem.getDistributions().isEmpty() ) ? '.' : ';';
			int num = 0;

			// Vernacular names node
			if (gbifItem.getVernacularNames() != null) {
				for (VernacularName vn : gbifItem.getVernacularNames()) {
					num++;
					w.println("\t\tgbif:vernacularName [");
					Linker.printPropertyValue(w, 3, "dwc:lifeStage", vn.lifeStage,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dwc:sex", vn.sex, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:vernacularName",
							vn.vernacularName, true, false, false);
					Linker.printPropertyValue(w, 3, "dc:language", vn.language,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:source", vn.source, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dwc:countryCode",
							vn.countryCode, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:country", vn.country, true, true,
							false);
					boolean last = (num == gbifItem.getVernacularNames().size());
					w.println("\t\t] " + (last ? endChar : ';'));
				}
			}

			// Distributions nodes
			num = 0;
			if (gbifItem.getDistributions() != null) {
				for (Distribution d : gbifItem.getDistributions()) {
					num++;
					w.println("\t\tgbif:Distribution [");
					Linker.printPropertyValue(w, 3, "dwc:countryCode", d.countryCode,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dwc:lifeStage", d.lifeStage,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dwc:country", d.country, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dwc:locationID", d.locationID,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dwc:establishmentMeans",
							d.establishmentMeans, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:locality", d.locality,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:source", d.source, true, false,
							false);
					Linker.printPropertyValue(w, 3, "iucn:threatStatus",
							d.threatStatus, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:locationRemarks",
							d.locationRemarks, true, false, false);
					Linker.printPropertyValue(w, 3, "dwc:occurrenceStatus",
							d.occurrenceStatus, true, true, false);

					if (num == gbifItem.getMultimediaFiles().size()) {
						endChar = '.';
					} else {
						endChar = ';';
					}
					w.println("\t\t] " + endChar);
				}
			}

			// Multimedia nodes
			num = 0;
			if (gbifItem.getMultimediaFiles() != null) {
				for (Multimedia m : gbifItem.getMultimediaFiles()) {
					num++;
					w.println("\t\tgbif:Multimedia [");
					Linker.printPropertyValue(w, 3, "dc:title", m.title, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:license", m.license, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:creator", m.creator, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:references", m.references,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:contributor", m.contributor,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:description", m.description,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:source", m.source, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:identifier", m.identifier,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:created", m.created, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:publisher", m.publisher,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:rightsHolder",
							m.rightsHolder, true, true, false);

					if (num == gbifItem.getReferences().size()) {
						endChar = '.';
					} else {
						endChar = ';';
					}
					w.println("\t\t] " + endChar);
				}
			}

			// References nodes
			num = 0;
			if (gbifItem.getReferences() != null) {
				for (Reference r : gbifItem.getReferences()) {
					num++;
					w.println("\t\tgbif:Reference [");
					Linker.printPropertyValue(w, 3, "dc:bibliographicCitation",
							r.bibliographicCitation, true, false, false);
					Linker.printPropertyValue(w, 3, "dc:references", r.references,
							 true,false, false);
					Linker.printPropertyValue(w, 3, "dc:source", r.source, true, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:identifier", r.identifier,
							 true,true, false);

					if (num == gbifItem.getReferences().size()) {
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

	}

}
