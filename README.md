# README #

Aggregation of biological datasets and database to create a Linked Data store in RDF (using Turtle syntax).

### Introduction ###

This projects aims at aggregating selected biological resources on the Web and centralizing their data as Linked Open Data, that is as RDF files (here written as named graphs in TriG syntax).

Supported resources so far are EOL (TraitBank), NCBI, GBIF, WIkiData, and Dyntaxa. Fauna Europeana was considered but is not included.

This software was developed as an internship project for the Knowledge-as-a-media Team of the National Institute of Informatics of Tokyo (Japan). It was supervised by Hideaki Takeda, designed and coded by myself.


### Setup ###

TODO: Complete the detailed setup process (file location, properties file)

#### Required resources ####
This project relies a lot on downloaded backups or databases from above-mentioned resources. In particular, to get data from each external dataset, you will require:

* For WikiData, a JSON data archive, such as the full dumps provided here: https://www.wikidata.org/wiki/Wikidata:Database_download#JSON_dumps_.28recommended.29. The project uses specific Java librairies iterating on this file.
* For NCBI, the Taxonomy dump provided here: ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/ (taxdmp)
* For ITIS, the database installed on an SQL server of your liking: http://www.itis.gov/downloads/
* For GBIF, the full dump available on GBIF's FTP server once you are registered on the website http://www.gbif.org/user/register
* For Dyntaxa, the full archive provided as an Excel table file: https://www.dyntaxa.se/ (Go to the english version, then "Export" -> "Straight Taxon List"


#### Configuration and .properties file ####


#### Dependencies ####


### Deployment and use ###
* Deployment instructions

* NOT a visualizer for Linked Data

### Contact ###

For future prospects, contact Hideaki Takeda at the National Institute of Informatics (Tokyo, Japan).
