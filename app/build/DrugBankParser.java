/**
 * 
 */
package build;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import play.Logger;

import core.XMLBurger;

/**
 * @author Samuel Croset
 *
 */
public class DrugBankParser extends Parser {

	private DrugBank drugbank;

	final static String DRUGBANK_DOWNLOAD_URL = "http://www.drugbank.ca/system/downloads/current/drugbank.xml.zip";

	public void setDrugbank(DrugBank drugbank) {
		this.drugbank = drugbank;
	}

	public DrugBank getDrugbank() {
		return drugbank;
	}

	/**
	 * @param path
	 */
	public DrugBankParser(String pathOut) {
		super(pathOut);
		this.setDrugbank(new DrugBank());
	}


	/* (non-Javadoc)
	 * @see parser.Parser#parse()
	 */
	@Override
	public void start() throws IOException {

		Logger.info("Downloading the zip file...");
		URL website = new URL(DRUGBANK_DOWNLOAD_URL);
		File tempXmlFile = new File("data/tmp/drugbank.xml.zip");
		org.apache.commons.io.FileUtils.copyURLToFile(website, tempXmlFile);

		Logger.info("Unpacking the zip file...");
		unpack(tempXmlFile, "data/tmp/");

		XMLBurger burger = new XMLBurger("data/tmp/drugbank.xml");
		while(burger.isNotOver()){
			if(burger.tag("drugs")){

				ArrayList<Drug> drugs = new ArrayList<Drug>();

				while(burger.inTag("drugs")){
					if(burger.tag("drug")){

						Drug drug = new Drug();
						String type = burger.getTagAttribute("type");
						drug.setType(type);
						String name = null;
						while(burger.inTag("drug")){
							if(burger.tag("drugbank-id")){
								drug.setId(burger.getTagText());
								name = "drug-name";
							}

							if(burger.tag("name")){
								if(name.equals("drug-name")){
									drug.setName(burger.getTagText());
								}
							}

							if(burger.tag("description")){
								if(name.equals("drug-name")){
									drug.setDescription(burger.getTagText());
								}
							}

							if(burger.tag("indication")){
								drug.setIndication(burger.getTagText());
							}

							if(burger.tag("pharmacology")){
								drug.setPharmacology(burger.getTagText());
							}

							if(burger.tag("mechanism-of-action")){
								drug.setMechanism(burger.getTagText());
							}

							if(burger.tag("groups")){
								ArrayList<String> groups = new ArrayList<String>();
								while(burger.inTag("groups")){
									if(burger.tag("group")){
										groups.add(burger.getTagText());
									}
								}
								drug.setGroups(groups);
							}

							if(burger.tag("mixtures")){while(burger.inTag("mixtures")){}}
							if(burger.tag("packagers")){while(burger.inTag("packagers")){}}
							if(burger.tag("prices")){while(burger.inTag("prices")){}}

							if(burger.tag("categories")){
								ArrayList<String> categories = new ArrayList<String>();
								while(burger.inTag("categories")){
									if(burger.tag("category")){
										categories.add(burger.getTagText());
									}
								}
								drug.setCategories(categories);
							}


							if(burger.tag("atc-codes")){
								ArrayList<String> atcCodes = new ArrayList<String>();
								while(burger.inTag("atc-codes")){
									if(burger.tag("atc-code")){
										atcCodes.add(burger.getTagText());
									}
								}
								drug.setAtcCodes(atcCodes);
							}

							if(burger.tag("calculated-properties")){
								while(burger.inTag("calculated-properties")){
									if(burger.tag("property")){
										boolean isSmile = false;
										while(burger.inTag("property")){
											if(burger.tag("kind")){
												String kind = burger.getTagText();
												if(kind.equals("SMILES")){
													isSmile = true;	
												}													
											}
											if(burger.tag("value") && isSmile){
												isSmile = false;
												drug.setSmiles(burger.getTagText());
											}
										}
									}
								}
							}


							if(burger.tag("drug-interactions")){while(burger.inTag("drug-interactions")){}}

							if(burger.tag("targets")){
								while(burger.inTag("targets")){
									if(burger.tag("target")){
										TargetRelation targetRelation = new TargetRelation();
										targetRelation.setPartnerId(Integer.parseInt(burger.getTagAttribute("partner")));
										while(burger.inTag("target")){
											if(burger.tag("actions")){
												ArrayList<String> actions = new ArrayList<String>();
												while(burger.inTag("actions")){
													if(burger.tag("action")){
														String action = burger.getTagText();
														actions.add(action);
													}
												}
												if(actions.size() == 0){
													actions.add("unknown");
												}
												targetRelation.setActions(actions);

											}
											if(burger.tag("known-action")){
												targetRelation.setKnowAction(burger.getTagText());
											}
										}
										drug.getTargetRelations().add(targetRelation);
									}
								}
							}
						}
						drugs.add(drug);
					}

					if(burger.tag("partners")){
						ArrayList<Partner> partners = new ArrayList<Partner>();
						while(burger.inTag("partners")){
							if(burger.tag("partner")){
								Partner partner = new Partner();
								partner.setId(Integer.parseInt(burger.getTagAttribute("id")));
								while(burger.inTag("partner")){
									if(burger.tag("name")){
										partner.setName(burger.getTagText());
									}

									if(burger.tag("species")){
										Species species = new Species();
										while(burger.inTag("species")){
											if(burger.tag("category")){
												species.setCategory(burger.getTagText());
											}
											if(burger.tag("name")){
												species.setName(burger.getTagText());
											}
											if(burger.tag("uniprot-taxon-id")){
												species.setTaxonId(Integer.parseInt(burger.getTagText()));
											}

										}
										partner.setSpecies(species);
									}

									if(burger.tag("external-identifiers")){
										String resourceName = null;
										while(burger.inTag("external-identifiers")){
											if(burger.tag("resource")){
												resourceName = burger.getTagText();
											}
											if(burger.tag("identifier")){
												if(resourceName != null && resourceName.equals("UniProtKB")){
													partner.setUniprotIdentifer(burger.getTagText());
													resourceName = null;
												}
											}
										}

									}

									if(burger.tag("pfams")){while(burger.inTag("pfams")){}}
								}
								partners.add(partner);
							}
						}
						this.getDrugbank().setPartners(partners);
					}
				}
				this.getDrugbank().setDrugs(drugs);
			}
		}

	}


	private void unpack(File zipFile, String rootDir) throws ZipException, IOException {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			java.io.File f = new java.io.File(rootDir, entry.getName());
			if (entry.isDirectory()) {
				continue;
			}

			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}

			BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(entry));
			BufferedOutputStream bos = new BufferedOutputStream(new java.io.FileOutputStream(f));
			while (bis.available() > 0) {
				bos.write(bis.read());
			}
			bos.close();
			bis.close();
		}
	}


	/* (non-Javadoc)
	 * @see parser.Parser#save()
	 */
	@Override
	public DrugBank save() throws FileNotFoundException, IOException {
		Logger.info("Saving DrugBank serialized...");
		ObjectOutput out = null;
		out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
		out.writeObject(this.getDrugbank());
		out.close();
		return this.getDrugbank();
	}

}
