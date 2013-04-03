package build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Agent;
import models.EvaluationMapping;
import models.FtcClass;
import models.Metrics;

import controllers.Application;
import play.Logger;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingEntityException;

public class Evaluation {

	List<EvaluationMapping> mappings;
	Brain atc;
	Brain ftc;
	List<String> drugBankCompoundsInAtc;

	public Evaluation(String pathToMappings) throws IOException, BrainException {
		atc = new Brain();
		Logger.info("Learning ATC...");
		atc.learn("data/tmp/atc.owl");
		
		//Gets the drugBank compounds present in the ATC. This step in necessary to make sure
		//only drugs present both in the FTC and ATC will be considered.
		drugBankCompoundsInAtc = new ArrayList<String>();
		List<String> drugBankAtcCompounds = atc.getSubClasses("DrugBankCompound", false);
		for (String drugBankAtcCompound : drugBankAtcCompounds) {
			if(drugBankAtcCompound.startsWith("DB")){
				drugBankCompoundsInAtc.add(drugBankAtcCompound);
			}
		}

		Logger.info("Learning FTC...");
		ftc = Application.brain;

		mappings = new ArrayList<EvaluationMapping>();
		FileInputStream fstream = new FileInputStream(pathToMappings);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			Pattern pattern = Pattern.compile("\\((.*)\\) = \\((.*)\\)");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()){
				EvaluationMapping mapping = new EvaluationMapping();
				mapping.atcClasses = Arrays.asList(matcher.group(1).split(" or "));
				mapping.ftcClass = matcher.group(2);
				mapping.definition = line;
				mapping.setDefinitionHtml();
				mappings.add(mapping);
			}else{
				Logger.warn("Error while parsing the mapping file: " + line);
			}
		}

		br.close();
	}

	public void start() throws BrainException {

		Logger.info("Populating metrics...");
		Metrics metrics = new Metrics();
		metrics.date = new Date();
		metrics.numberOfAxioms = ftc.getOntology().getAxiomCount();
		metrics.numberOfProteins = ftc.getSubClasses("Protein", false).size();		
		metrics.numberOfDrugBankCompounds = Agent.count();
		metrics.numberOfFtcClasses = FtcClass.count();

		List<String> ftcCompounds = ftc.getSubClasses("FTC_C2", false);
		
		Set<String> commonCompounds = new HashSet<String>();
		for (String drugBankCompoundInAtc : drugBankCompoundsInAtc) {
			if(ftcCompounds.contains(drugBankCompoundInAtc)){
				commonCompounds.add(drugBankCompoundInAtc);
			}
		}
		
		metrics.numberOfCompoundsInBothClassifications = commonCompounds.size();
		
		int counter = 1;
		int total = mappings.size();

		Set<String> evaluatedCompounds = new HashSet<String>();

		for (EvaluationMapping mapping : mappings) {
			Logger.info("Saving evalution mapping: " + counter + "/" + total);
			counter++;
			mapping.atcDrugs = getAtcDrugs(mapping.atcClasses);
			mapping.ftcDrugs = getFtcDrugs(mapping.ftcClass);

			for (Agent atcDrug : mapping.atcDrugs) {
				evaluatedCompounds.add(atcDrug.drugBankId);
			}

			for (Agent ftcDrug : mapping.ftcDrugs) {
				evaluatedCompounds.add(ftcDrug.drugBankId);
			}

			mapping.setTruePostives();
			mapping.setFalseNegatives();
			mapping.setFalsePositives();
			mapping.save();
		}
		
		metrics.numberOfUniquelyEvaluatedCompounds = evaluatedCompounds.size();
		metrics.save();
		Logger.info("Metrics done!");
	}

	private List<Agent> getFtcDrugs(String ftcClass) throws BrainException {
		List<Agent> ftcDrugs = new ArrayList<Agent>();
		List<String> subClasses = ftc.getSubClasses(ftcClass + " and FTC_C2", false);
		for (String subClass : subClasses) {
			Agent agent = Agent.find("byDrugBankId", subClass).first();
			if(drugBankCompoundsInAtc.contains(subClass)){
				ftcDrugs.add(agent);
			}
		}
		return ftcDrugs;
	}

	//Hack to get the drugbank compounds - kind of dirty
	private List<Agent> getAtcDrugs(List<String> atcClasses) throws BrainException {
		List<Agent> atcDrugs = new ArrayList<Agent>();
		Set<String> knownCompounds = new HashSet<String>();

		//Iterates over the list of ATC classes
		for (String atcClass : atcClasses) {
			//Get all the subclasses
			List<String> atcSubclasses = atc.getSubClasses(atcClass + " and DrugBankCompound", false);
			//Get the super classes in order to retrieve only drugbank compounds - drugbank compounds are superclasses of their equivalent
			//in the ATC.
			for (String atcSubclass : atcSubclasses) {
				List<String> atcSuperClasses = atc.getSuperClasses(atcSubclass, true);
				for (String atcSuperClass : atcSuperClasses) {
					if(atcSuperClass.startsWith("DB") && !knownCompounds.contains(atcSuperClass)){
						Agent agent = Agent.find("byDrugBankId", atcSuperClass).first();
						//Checks whether the compound is present in the database, meaning present in the FTC
						if(agent != null){
							knownCompounds.add(atcSuperClass);
							atcDrugs.add(agent);
						}
					}
				}
			}
		}
		return atcDrugs;
	}
}
