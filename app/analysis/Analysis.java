package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

		//analysis.exportMoaSimilarities("data/analysis/moa_similarities_2lvl.csv", 3);

		//analysis.exportSimsStructVsMoA("data/analysis/struct_moa_sim_2lvl.csv");

		//analysis.exportFramedDiffCatsSimStructVsMoa("data/analysis/diff_cats_3lvl.csv", 4);

		//analysis.exportFramedSameCatsSimStructVsMoa("data/analysis/same_cats_4lvl.csv", 5);

		//analysis.exportFramedDiffCatsSimStructVsMoa("data/analysis/diff_cats_1lvl.csv", 1);

		//analysis.exportSimsStructVsMoA("data/analysis/struct_moa_sim_anti_histaminic.csv");

		//analysis.exportSimAtcVsMoa("data/analysis/atc_moa_sim.csv");

		//analysis.exportSimStrucMoaAsHtml(0.09f, 0.15f, 0.88f, 0.92f, "data/analysis/struct_moa_sim_top.html", 1);

		//analysis.exportValuesForCategorie("V", "data/analysis/pval_V_same.csv", "data/analysis/pval_V_diff.csv");

		analysis.numberOfClassesWithDrug();

		analysis.done();
	}


	private void numberOfClassesWithDrug() throws Exception {
		List<String> all = brain.getSubClasses("FTC_C1", false);
		int numberOfClasses = 0;
		for (String ftcClass : all) {
			if(ftcClass.startsWith("FTC_")){
				List<String> subClasses = brain.getSubClasses(ftcClass, false);
				boolean hasDrug = false;
				for (String subClass : subClasses) {
					if(subClass.startsWith("DB")){
						if(!hasDrug){
							numberOfClasses++;
							hasDrug = true;
							System.out.println(ftcClass);
						}
					}
				}
			}
		}
		System.out.println("Number of class with drugs inside: " + numberOfClasses);
	}

	private void exportValuesForCategorie(String category, String path, String path2) throws Exception {
		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("/home/samuel/git/ftc/data/atc.owl");
		System.out.println("Learning done!");

		//Get the drugbank compounds
		List<String> all = brain.getSubClasses("FTC_C2", false);

		//int iterations = 70;
		int iterations = all.size();

		int noMoa = 0;
		//PrintWriter writer = new PrintWriter(file);
		List<String> drugsWithMoa = new ArrayList<String>();

		//Retrieves the drug with an MoA only
		for (int i = 0; i < iterations; i++) {
			String class1 = all.get(i);
			//Excludes the classes with less than 5 super classes
			//Such classes are indeed not so interesting here (no MoA).
			if(brain.getSuperClasses(class1, false).size() > 4){
				drugsWithMoa.add(class1);
			}else{
				noMoa++;
			}
		}

		System.out.println("No MoAs total: " + noMoa + "/" + iterations);

		List<Float> sameCategories = new ArrayList<Float>();
		List<Float> diffCategories = new ArrayList<Float>();
		//Calculates the similarity for each pair of drug
		for (int i = 0; i < drugsWithMoa.size(); i++) {
			System.out.println(i + "/" + drugsWithMoa.size());
			String class1 = drugsWithMoa.get(i);
			//System.out.println("class1:" + class1);
			//This class1 needs to be of the category requested, otherwise we drop it
			List<String> atcSubClasses1;
			boolean isSameCategory = false;
			try {
				atcSubClasses1 = atc.getSubClasses(class1, false);
				//System.out.println("categories 1: " + atcSubClasses1);
				//Get the ATC category
				for (String categoryOfDrug1 : atcSubClasses1) {
					if(categoryOfDrug1.substring(0,1).equals(category)){
						isSameCategory = true;
					}
				}
			} catch (ClassExpressionException exception) {}

			//Checks if the first drug is in the category of interest
			if(isSameCategory){
				//System.out.println("is same category 1");
				//If yes then it iterates over all the over drugs and compares the drug category in order to assign to the right list
				//Similarity values should be removed 
				for (int j = 0; j < drugsWithMoa.size(); j++) {
					String class2 = drugsWithMoa.get(j);
					//System.out.println("class2: " + class2);
					//This class2 category is checked. If same as requested category then added
					List<String> atcSubClasses2;
					boolean isSameCategory2 = false;
					try {
						atcSubClasses2 = atc.getSubClasses(class2, false);
						//System.out.println("categories: " + atcSubClasses2);
						//Get the ATC category
						for (String categoryOfDrug2 : atcSubClasses2) {
							if(categoryOfDrug2.substring(0,1).equals(category)){
								isSameCategory2 = true;
							}
						}
					} catch (ClassExpressionException exception) {
						//System.out.println("no category!");
					}

					float index = brain.getJaccardSimilarityIndex(class1, class2);
					//System.out.println("index: " + index);
					if(isSameCategory2) {
						//System.out.println("same both drugs 2");
						if(!class1.equals(class2)){
							sameCategories.add(index);
						}
					}else{
						//System.out.println("differentes categories both drugs");
						diffCategories.add(index);
					}
				}
			}
		}
		File file = new File(path);
		PrintWriter writer = new PrintWriter(file);
		boolean isFirst = true;
		for (Float sameCat : sameCategories) {
			if(!isFirst){
				//writer.append(",");
			}else{
				isFirst = false;
			}
			writer.append(sameCat.toString() + "\n");
		}
		writer.close();

		File file2 = new File(path2);
		PrintWriter writer2 = new PrintWriter(file2);

		isFirst = true;
		for (Float diffCat : diffCategories) {
			if(!isFirst){
				//writer2.append(",");
			}else{
				isFirst = false;
			}
			writer2.append(diffCat.toString() + "\n");
		}

		writer2.close();
		atc.sleep();
	}

	private void exportFramedSameCatsSimStructVsMoa(String path, int level) throws Exception {
		SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());

		DrugBank db = new DrugBank("data/tmp/drugbank.ser");

		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("data/atc.owl");
		System.out.println("Learning done!");

		//DrugBanks compounds in the FTC
		List<String> drugs = brain.getSubClasses("FTC_C2", false);
		List<String> drugBankIds = new ArrayList<String>();

		//int iterations = 100;
		int iterations = drugs.size();

		//Gets only the compounds with a SMILES attached to them
		//Get only the drugs that have a MoA (super classes > 4)
		int notConsidered = 0;
		for (int i = 0; i < iterations; i++) {
			String drugId = drugs.get(i);
			Drug drug = db.getDrug(drugId);
			if(drug.getSmiles() != null){
				if(brain.getSuperClasses(drugId, false).size() > 4){
					drugBankIds.add(drugId);
				}else{
					notConsidered++;
				}
			}else{
				notConsidered++;
			}
		}

		System.out.println("number of compounds considered: " + drugBankIds.size() + " - Not considered: " + notConsidered);

		List<SimilarityComparison> sims = new ArrayList<SimilarityComparison>();

		for (int i = 0; i < drugBankIds.size(); i++) {
			String id1 = drugBankIds.get(i);
			System.out.println(i + "/" + drugBankIds.size());
			for (int j = i + 1; j < drugBankIds.size(); j++) {
				String id2 = drugBankIds.get(j);

				//Actual sorting
				boolean same = false;
				try {
					same = sameCategories(atc.getSubClasses(id1, true), atc.getSubClasses(id2, true), level);
				} catch (ClassExpressionException exception){
					same = false;
				}


				if(same){
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

					try {
						sim.id2 = getCategory(atc.getSubClasses(id2, true), level);
					} catch (ClassExpressionException exception) {
						sim.id2 = "NoCategory";
					}		

					try {
						sim.id1 = getCategory(atc.getSubClasses(id1, true), level);
					} catch (ClassExpressionException exception) {
						sim.id1 = "NoCategory";
					}				

					sims.add(sim);
				}
			}
		}
		CSVWriter writer = new CSVWriter(path);
		writer.write(sims);
		atc.sleep();
	}

	private boolean sameCategories(List<String> subClasses1, List<String> subClasses2, int level) {
		for (String subClass1 : subClasses1) {
			String code1 = subClass1.substring(0, level);
			for (String subClass2 : subClasses2) {
				String code2 = subClass2.substring(0, level);
				if(code1.equals(code2)){
					//Means they have at least a category in common based on the level
					return true;
				}
			}
		}

		//If no categories are in common, then it's true
		return false;
	}

	// Export of the CSV presenting the data matching these criteria:
	// sim(struct(A), struct(B)) vs sim(moa(A), moa(B))
	// Only the dots where drugs have different categories are kept
	// The idea is to see were these repurposing hypothesis are landing on the graph
	private void exportFramedDiffCatsSimStructVsMoa(String path, int level) throws Exception {
		SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());

		DrugBank db = new DrugBank("data/tmp/drugbank.ser");

		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("data/atc.owl");
		System.out.println("Learning done!");

		//DrugBanks compounds in the FTC
		List<String> drugs = brain.getSubClasses("FTC_C2", false);
		List<String> drugBankIds = new ArrayList<String>();

		//int iterations = 100;
		int iterations = drugs.size();

		//Gets only the compounds with a SMILES attached to them
		//Get only the drugs that have a MoA (super classes > 4)
		int notConsidered = 0;
		for (int i = 0; i < iterations; i++) {
			String drugId = drugs.get(i);
			Drug drug = db.getDrug(drugId);
			if(drug.getSmiles() != null){
				if(brain.getSuperClasses(drugId, false).size() > 4){
					drugBankIds.add(drugId);
				}else{
					notConsidered++;
				}
			}else{
				notConsidered++;
			}
		}

		System.out.println("number of compounds considered: " + drugBankIds.size() + " - Not considered: " + notConsidered);

		List<AtcSimilarityComparison> sims = new ArrayList<AtcSimilarityComparison>();

		for (int i = 0; i < drugBankIds.size(); i++) {
			String id1 = drugBankIds.get(i);
			System.out.println(i + "/" + drugBankIds.size());
			for (int j = i + 1; j < drugBankIds.size(); j++) {
				String id2 = drugBankIds.get(j);

				//Actual sorting
				boolean diff = false;
				try {
					diff = differentCategories(atc.getSubClasses(id1, true), atc.getSubClasses(id2, true), level);
				} catch (ClassExpressionException exception){
					diff = false;
				}


				if(diff){
					String smiles1 = db.getDrug(id1).getSmiles();
					String smiles2 = db.getDrug(id2).getSmiles();
					IMolecule mol1 = smilesParser.parseSmiles(smiles1);
					IMolecule mol2 = smilesParser.parseSmiles(smiles2);
					HybridizationFingerprinter fingerprinter = new HybridizationFingerprinter();
					BitSet bitset1 = fingerprinter.getFingerprint(mol1);
					BitSet bitset2 = fingerprinter.getFingerprint(mol2);
					float tanimoto = Tanimoto.calculate(bitset1, bitset2);
					float jaccard = brain.getJaccardSimilarityIndex(id1, id2);
					AtcSimilarityComparison sim = new AtcSimilarityComparison();
					sim.firstSim = jaccard;
					sim.secondSim = tanimoto;
					sim.id1 = id1;
					sim.id2 = id2;

					try {
						sim.atc2 = getCategory(atc.getSubClasses(id2, true), level);
					} catch (ClassExpressionException exception) {
						sim.atc2 = "NoCategory";
					}		

					try {
						sim.atc1 = getCategory(atc.getSubClasses(id1, true), level);
					} catch (ClassExpressionException exception) {
						sim.atc1 = "NoCategory";
					}				

					sims.add(sim);
				}
			}
		}
		CSVWriter writer = new CSVWriter(path);
		writer.write(sims);
		atc.sleep();
	}

	//Returns whether two drugs are sharing the same category
	private boolean differentCategories(List<String> subClasses1, List<String> subClasses2, int level) {
		for (String subClass1 : subClasses1) {
			String code1 = subClass1.substring(0, level);
			for (String subClass2 : subClasses2) {
				String code2 = subClass2.substring(0, level);
				if(code1.equals(code2)){
					//Means they have at least a category in common based on the level
					return false;
				}
			}
		}

		//If no categories are in common, then it's true
		return true;
	}

	private void exportSimStrucMoaAsHtml(float lowSimStruc, float upSimStruc, float lowSimMoa, float upSimMoa, String path, int level) throws Exception {

		SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		//TODO sort that in a neat way
		DrugBank db = new DrugBank("data/tmp/drugbank.ser");

		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("data/atc.owl");
		System.out.println("Learning done!");

		//DrugBanks compounds in the FTC
		List<String> drugs = brain.getSubClasses("FTC_C2", false);
		//TODO comment-out to deal with all drugs
		//drugs = getAntiHistaminics();


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
				"</head><body><table>");
		for (SimilarityComparison sim : sims) {
			List<String> atcSubClasses1;
			String category1;
			try {
				atcSubClasses1 = atc.getSubClasses(sim.id1, false);
				category1 = getCategory(atcSubClasses1, level);
			} catch (ClassExpressionException exception) {
				category1 = "NoCategory";
			}

			List<String> atcSubClasses2;
			String category2;
			try {
				atcSubClasses2 = atc.getSubClasses(sim.id2, false);
				category2 = getCategory(atcSubClasses2, level);
			} catch (ClassExpressionException exception) {
				category2 = "NoCategory";
			}

			writer.print("<tr>" +
					"<td><img src='http://structures.wishartlab.com/molecules/" + sim.id1 + "/image.png'></td>" +
					"<td><a href='https://www.ebi.ac.uk/chembl/ftc/agent/" + sim.id1 + "'>" + sim.id1 + "</td> " +
					"<td style='background-color:" + color(category1) + "'>" + category1 + "</td>" +
					"<td style='background-color:" + color(category2) + "'>" + category2 + "</td>" +
					"<td><a href='https://www.ebi.ac.uk/chembl/ftc/agent/" + sim.id2 + "'>" + sim.id2 + "</td>" +
					"<td><img src='http://structures.wishartlab.com/molecules/" + sim.id2 + "/image.png'></td>" +
					"</tr>");

		}
		writer.print("</table></body></html>");
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
		mapping.put("N", "#0066FF");
		mapping.put("NoCategory", "#FFFFFF");
		mapping.put("P", "#5200FF");
		mapping.put("R", "#AD00FF");
		mapping.put("S", "#FF00F5");
		mapping.put("V", "#FF0099");				
		return mapping.get(category);
	}

	//TODO doc to explain what it is exactly
	private void exportSimsStructVsMoA(String path, int level) throws Exception {
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

		//To be commented out in order to not use the anti-histaminic
		//drugs = getAntiHistaminics();
		//System.out.println("Number of anti-hiostaminics: " + drugs.size());

		//int iterations = 300;
		int iterations = drugs.size();

		//Gets only the compounds with a SMILES attached to them
		//Get only the drugs that have a MoA (super classes > 4)
		int notConsidered = 0;
		for (int i = 0; i < iterations; i++) {
			String drugId = drugs.get(i);
			Drug drug = db.getDrug(drugId);
			if(drug.getSmiles() != null){
				if(brain.getSuperClasses(drugId, false).size() > 4){
					drugBankIds.add(drugId);
				}else{
					notConsidered++;
				}
			}else{
				notConsidered++;
			}
		}

		System.out.println("number of compounds considered: " + drugBankIds.size() + " - Not considered: " + notConsidered);

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

				try {
					sim.id2 = getCategory(atc.getSubClasses(id2, true), level);
				} catch (ClassExpressionException exception) {
					sim.id2 = "NoCategory";
				}		

				try {
					sim.id1 = getCategory(atc.getSubClasses(id1, true), level);
				} catch (ClassExpressionException exception) {
					sim.id1 = "NoCategory";
				}				

				sims.add(sim);
			}
		}
		CSVWriter writer = new CSVWriter(path);
		writer.write(sims);
		atc.sleep();
	}

	private List<String> getAntiHistaminics() {
		List<String> compounds = Arrays.asList("DB00667", "DB01075", "DB00366", "DB00792", "DB01114", "DB01069",
				"DB01237", "DB00557", "DB00835", "DB00737", "DB00777", "DB00434", "DB00405", "DB06691", "DB00719",
				"DB00283", "DB00354", "DB01176", "DB01146", "DB00902", "DB01246", "DB00427", "DB08799", "DB00455",
				"DB01620", "DB00341", "DB00972", "DB00950", "DB00768", "DB01084", "DB00920", "DB00967", "DB00748",
				"DB00751", "DB01075", "DB06766", "DB00985", "DB01106", "DB04890", "DB00334", "DB00797", "DB00726",
				"DB01173", "DB01267", "DB00245");
		return compounds;
	}

	//TODO explanations
	private void exportMoaSimilarities(String path, int level) throws BrainException, FileNotFoundException {
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
					category1 = getCategory(atcSubClasses1, level);
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

	//Retrieves the top level ATC category of a drug
	private String getCategory(List<String> atcSubClasses, int level) {
		if(atcSubClasses.size() > 1){
			return "Multiple";
		}
		String category = atcSubClasses.get(0);
		return category.substring(0, level);
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
