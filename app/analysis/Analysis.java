package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesParser;

import build.Drug;
import build.DrugBank;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.ExistingEntityException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingEntityException;

public class Analysis {

	Brain brain;

	public Analysis() throws BrainException {
		brain = new Brain();
		System.out.println("Learning...");
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		System.out.println("learning done!");
	}

	//TODO doc - explain everything is started from there
	public static void main(String[] args) throws Exception {
		Analysis analysis = new Analysis();
		//		analysis.exportDistributionMoa("data/analysis/direct-distribution-moas.csv", true);
		//		analysis.exportDistributionMoa("data/analysis/undirect-distribution-moas.csv", false);
		//analysis.exportMoaSimilarities("data/analysis/moa_similarities.csv");

		//		analysis.exportSimsStructVsMoA("data/analysis/struct_moa_sim.csv");

		//analysis.exportSimAtcVsMoa("data/analysis/atc_moa_sim.csv");

		analysis.exportSimStrucMoaAsHtml(0.0f, 0.4f, 0.965f, 0.9690f, "data/analysis/struct_moa_sim.html");

		analysis.done();
	}

	private void exportSimStrucMoaAsHtml(float lowSimStruc, float upSimStruc, float lowSimMoa, float upSimMoa, String path) throws Exception {

		SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		//TODO sort that in a neat way
		DrugBank db = new DrugBank("data/tmp/drugbank.ser");

		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("data/atc.owl");
		System.out.println("Learning done!");

		//DrugBanks compounds in the FTC
		List<String> drugs = brain.getSubClasses("FTC_C2", false);
		List<String> drugBankIds = new ArrayList<String>();

		//int iterations = 200;
		int iterations = drugs.size();

		//Gets only the compounds wit a SMILES attached to them
		//Get only the drugs that have a MoA (super classes > 4)
		for (int i = 0; i < iterations; i++) {
			String drugId = drugs.get(i);
			Drug drug = db.getDrug(drugId);
			if(drug.getSmiles() != null){
				if(brain.getSuperClasses(drugId, false).size() > 4){
					drugBankIds.add(drugId);
				}
			}
		}

		System.out.println("number of compounds considered: " + drugBankIds.size());

		List<SimilarityComparison> sims = new ArrayList<SimilarityComparison>();

		for (int i = 0; i < drugBankIds.size(); i++) {
			String id1 = drugBankIds.get(i);
			System.out.println(i + "/" + drugBankIds.size());
			for (int j = i + 1; j < drugBankIds.size(); j++) {
				String id2 = drugBankIds.get(j);
				String smiles1 = db.getDrug(id1).getSmiles();
				String smiles2 = db.getDrug(id2).getSmiles();
				IMolecule mol1 = smilesParser.parseSmiles(smiles1);
				IMolecule mol2 = smilesParser.parseSmiles(smiles2);
				HybridizationFingerprinter fingerprinter = new HybridizationFingerprinter();
				BitSet bitset1 = fingerprinter.getFingerprint(mol1);
				BitSet bitset2 = fingerprinter.getFingerprint(mol2);
				float tanimoto = Tanimoto.calculate(bitset1, bitset2);
				float jaccard = brain.getJaccardSimilarityIndex(id1, id2);
				if(tanimoto > lowSimStruc && tanimoto < upSimStruc && jaccard < upSimMoa && jaccard > lowSimMoa){
					SimilarityComparison sim = new SimilarityComparison();
					sim.firstSim = jaccard;
					sim.secondSim = tanimoto;
					sim.id1 = id1;
					sim.id2 = id2;
					sims.add(sim);
				}
			}
		}

		PrintWriter writer = new PrintWriter(new File(path));
		writer.print("<!DOCTYPE html><html><head>" +
				"<title>Sims</title><link rel='stylesheet' href='file:///home/samuel/git/ftc/data/analysis/main.css'>" +
				"</head><body>");
		for (SimilarityComparison sim : sims) {
			List<String> atcSubClasses1;
			String category1;
			try {
				atcSubClasses1 = atc.getSubClasses(sim.id1, false);
				category1 = getCategory(atcSubClasses1);
			} catch (ClassExpressionException exception) {
				category1 = "NoCategory";
			}

			List<String> atcSubClasses2;
			String category2;
			try {
				atcSubClasses2 = atc.getSubClasses(sim.id2, false);
				category2 = getCategory(atcSubClasses2);
			} catch (ClassExpressionException exception) {
				category2 = "NoCategory";
			}

			writer.print("<div>" +
					"<img src='http://structures.wishartlab.com/molecules/" + sim.id1 + "/image.png'>" +
					"<span>" + sim.id1 + "</span> | " +
					"<span style='background-color:" + color(category1) + "'>" + category1 + "</span>" +
					" | <span style='background-color:" + color(category2) + "'>" + category2 + "</span>" +
					" | <span>" + sim.id2 + "</span>" +
					"<img src='http://structures.wishartlab.com/molecules/" + sim.id2 + "/image.png'>" +
					"</div>");

		}
		writer.print("</body></html>");
		writer.close();
		atc.sleep();
	}

	private String color(String category) {
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("A", "#FF0000");
		mapping.put("B", "#FF5C00");
		mapping.put("C", "#FFB800");
		mapping.put("D", "#EBFF00");
		mapping.put("G", "#8FFF00");
		mapping.put("H", "#33FF00");
		mapping.put("J", "#00FF29");
		mapping.put("L", "#00FF85");
		mapping.put("M", "#00FFE0");
		mapping.put("Multiple", "#000000");
		mapping.put("N", "#0066FFFF");
		mapping.put("NoCategory", "white");
		mapping.put("P", "#5200FF");
		mapping.put("R", "#AD00FF");
		mapping.put("S", "#FF00F5");
		mapping.put("V", "#FF0099");				
		return mapping.get(category);
	}

	//TODO explanations how it's done
	private void exportSimAtcVsMoa(String path) throws Exception {
		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("data/atc.owl");
		System.out.println("Learning done!");

		List<String> drugIds = new ArrayList<String>();

		List<String> dbCompounds = brain.getSubClasses("FTC_C2", false);

		for (String dbCompound : dbCompounds) {
			if(atc.knowsClass(dbCompound)){
				drugIds.add(dbCompound);
			}
		}

		List<SimilarityComparison> sims = new ArrayList<SimilarityComparison>();
		//int iterations = drugIds.size();
		int iterations = 500;

		for (int i = 0; i < iterations; i++) {
			String id1 = drugIds.get(i);
			System.out.println(i + "/" + iterations);
			for (int j = i + 1; j < iterations; j++) {
				String id2 = drugIds.get(j);
				float indexFtc = brain.getJaccardSimilarityIndex(id1, id2);
				float indexAtc = atc.getJaccardSimilarityIndex(atc.getSubClasses(id1, true), 
						atc.getSubClasses(id2, true));
				SimilarityComparison sim = new SimilarityComparison();
				sim.firstSim = indexAtc;
				sim.secondSim = indexFtc;
				sims.add(sim);
			}
		}

		atc.sleep();
		CSVWriter writer = new CSVWriter(path);
		writer.write(sims);
	}

	//TODO doc to explain what it is exactly
	private void exportSimsStructVsMoA(String path) throws Exception {
		SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		//TODO sort that in a neat way
		DrugBank db = new DrugBank("data/tmp/drugbank.ser");

		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("data/atc.owl");
		System.out.println("Learning done!");

		//DrugBanks compounds in the FTC
		List<String> drugs = brain.getSubClasses("FTC_C2", false);
		List<String> drugBankIds = new ArrayList<String>();

		//int iterations = 500;
		int iterations = drugs.size();

		//Gets only the compounds wit a SMILES attached to them
		//Get only the drugs that have a MoA (super classes > 4)
		for (int i = 0; i < iterations; i++) {
			String drugId = drugs.get(i);
			Drug drug = db.getDrug(drugId);
			if(drug.getSmiles() != null){
				if(brain.getSuperClasses(drugId, false).size() > 4){
					drugBankIds.add(drugId);
				}
			}
		}

		System.out.println("number of compounds considered: " + drugBankIds.size());

		//Iterates over all selected compounds and look at
		//structural and MoA sims between them.

		List<SimilarityComparison> sims = new ArrayList<SimilarityComparison>();

		for (int i = 0; i < drugBankIds.size(); i++) {
			String id1 = drugBankIds.get(i);
			System.out.println(i + "/" + drugBankIds.size());
			for (int j = i + 1; j < drugBankIds.size(); j++) {
				String id2 = drugBankIds.get(j);
				String smiles1 = db.getDrug(id1).getSmiles();
				String smiles2 = db.getDrug(id2).getSmiles();
				IMolecule mol1 = smilesParser.parseSmiles(smiles1);
				IMolecule mol2 = smilesParser.parseSmiles(smiles2);
				HybridizationFingerprinter fingerprinter = new HybridizationFingerprinter();
				BitSet bitset1 = fingerprinter.getFingerprint(mol1);
				BitSet bitset2 = fingerprinter.getFingerprint(mol2);
				float tanimoto = Tanimoto.calculate(bitset1, bitset2);
				float jaccard = brain.getJaccardSimilarityIndex(id1, id2);
				SimilarityComparison sim = new SimilarityComparison();
				sim.firstSim = jaccard;
				sim.secondSim = tanimoto;
				sims.add(sim);
			}
		}
		CSVWriter writer = new CSVWriter(path);
		writer.write(sims);
	}

	//TODO explanations
	private void exportMoaSimilarities(String path) throws BrainException, FileNotFoundException {
		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("/home/samuel/git/ftc/data/atc.owl");
		System.out.println("Learning done!");

		//Get the drugbank compounds
		List<String> all = brain.getSubClasses("FTC_C2", false);

		//int iterations = 100;
		int iterations = all.size();

		File file = new File(path);
		int noMoa = 0;
		PrintWriter writer = new PrintWriter(file);
		List<String> drugsWithMoa = new ArrayList<String>();

		//Retrieves the drug with an MoA only
		for (int i = 0; i < iterations; i++) {
			String class1 = all.get(i);
			//Excludes the classes with less than 5 super classes
			//Such classes are indeed not so interesting here (no MoA).
			if(brain.getSuperClasses(class1, false).size() > 4){
				drugsWithMoa.add(class1);
				if(i != 0){
					writer.append(",");
				}
				writer.append(all.get(i));
			}else{
				noMoa++;
			}
		}
		writer.append(",category\n");
		System.out.println("No MoAs total: " + noMoa + "/" + iterations);

		//Calculates the similarity for each pair of drug
		for (int i = 0; i < drugsWithMoa.size(); i++) {
			System.out.println(i + "/" + drugsWithMoa.size());
			String category1 = null;
			for (int j = 0; j < drugsWithMoa.size(); j++) {
				String class1 = drugsWithMoa.get(i);
				String class2 = drugsWithMoa.get(j);

				//Get the subClasses of the drugbank compound 1
				List<String> atcSubClasses1;
				try {
					atcSubClasses1 = atc.getSubClasses(class1, false);
					//Get the ATC category
					category1 = getCategory(atcSubClasses1);
				} catch (ClassExpressionException exception) {
					category1 = "NoCategory";
				}

				//Get the index - similarity
				float index = brain.getJaccardSimilarityIndex(class1, class2);

				if(j != 0){
					writer.append(",");
				}
				writer.append(Float.toString(index));
			}
			writer.append("," + category1 + "\n");
		}
		atc.sleep();
		writer.close();
	}

	//Retrives the top level ATC category of a drug
	private String getCategory(List<String> atcSubClasses) {
		if(atcSubClasses.size() > 1){
			return "Multiple";
		}
		String category = atcSubClasses.get(0);
		return category.substring(0, 1);
	}

	private void done() {
		brain.sleep();
	}

	//TODO explanation what dat is
	private void exportDistributionMoa(String path, boolean direct) throws Exception {

		List<Distribution> distributions = new ArrayList<Distribution>();

		//Get all the drugBank compounds inside the FTC
		List<String> drugBankCompounds = brain.getSubClasses("FTC_C2", false);
		for (String drugBankCompound : drugBankCompounds) {
			Distribution distribution = new Distribution();

			//Get the direct super classes
			distribution.numberOfOccurences = brain.getSuperClasses(drugBankCompound, direct).size();

			distribution.label = brain.getLabel(drugBankCompound);
			distribution.classId = drugBankCompound;
			distributions.add(distribution);			

		}
		CSVWriter writer = new CSVWriter(path);
		writer.write(distributions);
	}

}
