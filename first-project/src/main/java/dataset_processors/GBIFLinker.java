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
					gbifItem.getTaxon().taxonID, false, false);
			Linker.printPropertyValue(w, 3, "dwc:datasetID",
					gbifItem.getTaxon().datasetID, false, false);
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID",
					gbifItem.getTaxon().parentNameUsageID, false, false);
			Linker.printPropertyValue(w, 3, "dwc:acceptedNameUsageID",
					gbifItem.getTaxon().acceptedNameUsageID, false, false);
			Linker.printPropertyValue(w, 3, "dwc:originalNameUsageID",
					gbifItem.getTaxon().originalNameUsageID, false, false);
			Linker.printPropertyValue(w, 3, "dwc:scientificName",
					gbifItem.getTaxon().scientificName, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank",
					gbifItem.getTaxon().taxonRank, false, false);
			Linker.printPropertyValue(w, 3, "dwc:nameAccordingTo",
					gbifItem.getTaxon().nameAccordingTo, false, false);
			Linker.printPropertyValue(w, 3, "dwc:namePublishedIn",
					gbifItem.getTaxon().namePublishedIn, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonomicStatus",
					gbifItem.getTaxon().taxonomicStatus, false, false);
			Linker.printPropertyValue(w, 3, "dwc:nomenclaturalStatus",
					gbifItem.getTaxon().nomenclaturalStatus, false, false);
			Linker.printPropertyValue(w, 3, "dwc:kingdom",
					gbifItem.getTaxon().kingdom, false, false);
			Linker.printPropertyValue(w, 3, "dwc:phylum", gbifItem.getTaxon().phylum,
					false, false);
			Linker.printPropertyValue(w, 3, "dwc:class",
					gbifItem.getTaxon().taxonClass, false, false);
			Linker.printPropertyValue(w, 3, "dwc:order", gbifItem.getTaxon().order,
					false, false);
			Linker.printPropertyValue(w, 3, "dwc:family", gbifItem.getTaxon().family,
					false, false);
			Linker.printPropertyValue(w, 3, "dwc:genus", gbifItem.getTaxon().genus,
					false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRemarks",
					gbifItem.getTaxon().taxonRemarks, false, false);
			Linker.printPropertyValue(w, 3, "dwc:taxonRank",
					gbifItem.getTaxon().taxonRank, false, false);
			Linker.printPropertyValue(w, 3, "dwc:parentNameUsageID ",
					gbifItem.getTaxon().parentNameUsageID, true, false);

			char endChar = (gbifItem.getVernacularNames() == null) ? '.' : ';';
			w.println("\t\t] " + endChar);

			endChar = (gbifItem.getDistributions() == null) ? '.' : ';';
			int num = 0;

			// Vernacular names node
			if (gbifItem.getVernacularNames() != null) {
				for (VernacularName vn : gbifItem.getVernacularNames()) {
					num++;
					w.println("\t\tgbif:vernacularName [");
					Linker.printPropertyValue(w, 3, "dwc:lifeStage", vn.lifeStage,
							false, false);
					Linker.printPropertyValue(w, 3, "dwc:sex", vn.sex, false, false);
					Linker.printPropertyValue(w, 3, "dwc:vernacularName",
							vn.vernacularName, false, false);
					Linker.printPropertyValue(w, 3, "dc:language", vn.language,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:source", vn.source, false,
							false);
					Linker.printPropertyValue(w, 3, "dwc:countryCode",
							vn.countryCode, false, false);
					Linker.printPropertyValue(w, 3, "dwc:country", vn.country, true,
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
							false, false);
					Linker.printPropertyValue(w, 3, "dwc:lifeStage", d.lifeStage,
							false, false);
					Linker.printPropertyValue(w, 3, "dwc:country", d.country, false,
							false);
					Linker.printPropertyValue(w, 3, "dwc:locationID", d.locationID,
							false, false);
					Linker.printPropertyValue(w, 3, "dwc:establishmentMeans",
							d.establishmentMeans, false, false);
					Linker.printPropertyValue(w, 3, "dwc:locality", d.locality,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:source", d.source, false,
							false);
					Linker.printPropertyValue(w, 3, "iucn:threatStatus",
							d.threatStatus, false, false);
					Linker.printPropertyValue(w, 3, "dwc:locationRemarks",
							d.locationRemarks, false, false);
					Linker.printPropertyValue(w, 3, "dwc:occurrenceStatus",
							d.occurrenceStatus, true, false);

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
					Linker.printPropertyValue(w, 3, "dc:title", m.title, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:license", m.license, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:creator", m.creator, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:references", m.references,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:contributor", m.contributor,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:description", m.description,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:source", m.source, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:identifier", m.identifier,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:created", m.created, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:publisher", m.publisher,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:rightsHolder",
							m.rightsHolder, true, false);

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
							r.bibliographicCitation, false, false);
					Linker.printPropertyValue(w, 3, "dc:references", r.references,
							false, false);
					Linker.printPropertyValue(w, 3, "dc:source", r.source, false,
							false);
					Linker.printPropertyValue(w, 3, "dc:identifier", r.identifier,
							true, false);

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
