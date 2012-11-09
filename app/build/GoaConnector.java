/**
 * 
 */
package build;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import play.Logger;
import play.libs.WS;
import play.libs.WS.HttpResponse;

/**
 * @author Samuel Croset
 *
 */
public class GoaConnector {

	private DrugBank drugbank;

	public DrugBank getDrugbank() {
		return drugbank;
	}

	public void setDrugbank(DrugBank drugbank) {
		this.drugbank = drugbank;
	}


	public GoaConnector() throws FileNotFoundException, IOException, ClassNotFoundException {
		DrugBank drugBank = new DrugBank("data/tmp/drugbank.ser");
		this.setDrugbank(drugBank);
	}

	public void start() throws IOException {
		//From code provided by http://www.ebi.ac.uk/QuickGO/clients/DownloadAnnotation.java

		int counter = 0;
		int counterGoodOnes = 0;
		int total = this.getDrugbank().getPartners().size();

		for (Partner partner : this.getDrugbank().getPartners()) {
			if(partner.getUniprotIdentifer() != null){
				if(partner.getSpecies().getCategory() != null && partner.getSpecies().getCategory().equals("human")){
					String uniprotId = partner.getUniprotIdentifer();
					Logger.info("calling for " + uniprotId + " --> " + counter + "/" + total);
					counter++;
					counterGoodOnes++;
					HttpResponse res = WS.url("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein=" + uniprotId +"&format=tsv").get();

					BufferedReader rd =new BufferedReader(new InputStreamReader(res.getStream()));

					String line = rd.readLine();
					ArrayList<GoAnnotation> annotations = new ArrayList<GoAnnotation>();

					while ((line=rd.readLine())!=null) {
						String[] splittedLine = line.split("\t");
						GoAnnotation goa = new GoAnnotation();
						goa.setDatabase(splittedLine[0]);
						goa.setDate(splittedLine[12]);
						goa.setEvidence(splittedLine[9]);
						goa.setEvidenceProvider(splittedLine[10]);
						goa.setGoId(splittedLine[6]);
						goa.setQualifier(splittedLine[5]);
						goa.setReference(splittedLine[8]);
						goa.setSource(splittedLine[13]);
						goa.setTaxon(splittedLine[4]);
						String normalizedNamespace = null;
						if(splittedLine[11].equals("Function")){
							normalizedNamespace = "molecular_function";
						}else if(splittedLine[11].equals("Component")){
							normalizedNamespace = "cellular_component";
						}else if(splittedLine[11].equals("Process")){
							normalizedNamespace = "biological_process";
						}
						goa.setAspect(normalizedNamespace);
						annotations.add(goa);
					}
					partner.setAnnotations(annotations);
				}else{
					Logger.warn("The partner " + partner.getName() + " is not found in humans (GOA Connector)");
				}
			}else{
				Logger.warn("The partner " + partner.getName() + " has no Uniprot identifer (GOA Connector)");
				counter++;
			}
		}
		Logger.info("Number of human proteins present in Uniprot: " + counterGoodOnes);
	}

	public DrugBank save() throws FileNotFoundException, IOException {
		Logger.info("Saving DrugBank updated with GOA...");
		ObjectOutput out = null;
		out = new ObjectOutputStream(new FileOutputStream("data/tmp/drugbank-goa.ser"));
		out.writeObject(this.getDrugbank());
		out.close();
		return this.getDrugbank();
	}

}
