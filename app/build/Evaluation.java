package build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.EvaluationMapping;

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
				mapping.definition = line;
				mapping.atcClasses = Arrays.asList(matcher.group(1).split("or"));
				mapping.ftcClass = matcher.group(2);
				mappings.add(mapping);
			}else{
				Logger.warn("Error while parsing the mapping file: " + line);
			}
		}

		br.close();
	}

	public void start() throws BrainException {

		int counter = 1;
		int total = mappings.size();

		for (EvaluationMapping mapping : mappings) {
			Logger.info("Saving evalution mapping: " + counter + "/" + total);
			counter++;
			mapping.atcDrugs = getAtcDrugs(mapping.atcClasses);
			mapping.ftcDrugs = getFtcDrugs(mapping.ftcClass);

			mapping.truePositives = getTruePositives(mapping);
			mapping.falseNegatives = getFalseNegatives(mapping);
			mapping.falsePositives = getFalsePositives(mapping);
			mapping.save();
		}
	}


	private List<String> getFalsePositives(EvaluationMapping mapping) {
		List<String> fp = new ArrayList<String>();
		for (String ftcDrug : mapping.ftcDrugs) {
			if(!mapping.atcDrugs.contains(ftcDrug)){
				fp.add(ftcDrug);
			}
		}
		return fp;
	}

	private List<String> getFalseNegatives(EvaluationMapping mapping) {
		List<String> fn = new ArrayList<String>();
		for (String atcDrug : mapping.atcDrugs) {
			if(!mapping.ftcDrugs.contains(atcDrug)){
				fn.add(atcDrug);
			}
		}
		return fn;
	}

	private List<String> getTruePositives(EvaluationMapping mapping) {
		List<String> tp = new ArrayList<String>();
		for (String atcDrug : mapping.atcDrugs) {
			if(mapping.ftcDrugs.contains(atcDrug)){
				tp.add(atcDrug);
			}
		}
		return tp;
	}

	private List<String> getFtcDrugs(String ftcClass) throws BrainException {
		return ftc.getSubClasses(ftcClass + " and FTC_C2", false);
	}

	//Hack to get the drugbank compounds - kind of dirty
	private List<String> getAtcDrugs(List<String> atcClasses) throws BrainException {
		List<String> atcDrugs = new ArrayList<String>();
		//Iterates over the list of ATC classes
		for (String atcClass : atcClasses) {
			//Get all the subclasses
			List<String> atcSubclasses = atc.getSubClasses(atcClass + " and DrugBankCompound", false);
			//Get the super classes in order to retrieve only drugbank compounds
			for (String atcSubclass : atcSubclasses) {
				List<String> atcSuperClasses = atc.getSuperClasses(atcSubclass, true);
				for (String atcSuperClass : atcSuperClasses) {
					if(atcSuperClass.startsWith("DB") && !atcDrugs.contains(atcSuperClass)){
						atcDrugs.add(atcSuperClass);
					}
				}
			}
		}
		return atcDrugs;
	}



}
