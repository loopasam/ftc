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

	public Evaluation(String pathToMappings) throws IOException, BrainException {
		atc = new Brain();
		Logger.info("Learning ATC...");
		atc.learn("data/tmp/atc.owl");
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
		
		Metrics metrics = new Metrics();
		metrics.date = new Date();
		metrics.numberOfAxioms = ftc.getOntology().getAxiomCount();
		metrics.numberOfDrugBankCompounds = Agent.count();
		metrics.numberOfFtcClasses = FtcClass.count();
		metrics.save();

		int counter = 1;
		int total = mappings.size();

		for (EvaluationMapping mapping : mappings) {
			Logger.info("Saving evalution mapping: " + counter + "/" + total);
			counter++;
			mapping.atcDrugs = getAtcDrugs(mapping.atcClasses);
			mapping.ftcDrugs = getFtcDrugs(mapping.ftcClass);
			mapping.setTruePostives();
			mapping.setFalseNegatives();
			mapping.setFalsePositives();
			mapping.save();
		}
	}
	
	private List<Agent> getFtcDrugs(String ftcClass) throws BrainException {
		List<Agent> ftcDrugs = new ArrayList<Agent>();
		List<String> subClasses = ftc.getSubClasses(ftcClass + " and FTC_C2", false);
		for (String subClass : subClasses) {
			Agent agent = Agent.find("byDrugBankId", subClass).first();
			ftcDrugs.add(agent);
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
			//Get the super classes in order to retrieve only drugbank compounds
			for (String atcSubclass : atcSubclasses) {
				List<String> atcSuperClasses = atc.getSuperClasses(atcSubclass, true);
				for (String atcSuperClass : atcSuperClasses) {
					if(atcSuperClass.startsWith("DB") && !knownCompounds.contains(atcSuperClass)){
						Agent agent = Agent.find("byDrugBankId", atcSuperClass).first();
						knownCompounds.add(atcSuperClass);
						atcDrugs.add(agent);
					}
				}
			}
		}
		return atcDrugs;
	}
}
