package jobs;

import java.util.HashMap;
import java.util.List;

import models.OwlResult;

import controllers.Application;
import play.Logger;
import play.jobs.Job;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingClassException;
import uk.ac.ebi.brain.error.NonExistingEntityException;

public class OwlQueryJob extends Job<OwlResult> {

	private String query;

	public OwlQueryJob(String query) {
		this.setQuery(query);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public OwlResult doJobWithResult() throws BrainException {
		Logger.info("Starting asynchronous query...");
		Logger.info("Does the query... " + this.query);
		List<String> subClasses = Application.brain.getSubClassesFromLabel(this.query, false);
		Logger.info("Does the query for equivalkent classes..." + this.query);
		List<String> equivalentClasses = Application.brain.getEquivalentClassesFromLabel(this.query);
		HashMap<String, String> labelMap = getLabelMap(subClasses, equivalentClasses);
		HashMap<String, String> typeMap = getTypeMap(subClasses, equivalentClasses);

		Logger.info("Creating result object...");
		OwlResult result = new OwlResult(this.query, subClasses, equivalentClasses, labelMap, typeMap);

		Logger.info("Storing results in DB...");		
		result.save();
		Logger.info("Query done, returns the results");
		return result;
	}

	private HashMap<String, String> getTypeMap(List<String> subClasses, List<String> equivalentClasses) throws NonExistingClassException {
		// TODO Auto-generated method stub
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for (String subClass : subClasses) {
			String iri = Application.brain.getOWLClass(subClass).getIRI().toString();

			if(iri.contains("http://purl.uniprot.org/uniprot/")){
				typeMap.put(subClass, "protein");
			}else if(iri.contains("http://www.drugbank.ca/drugs/")){
				typeMap.put(subClass, "drugbank");
			}else if(iri.contains("http://purl.obolibrary.org/obo/")){
				typeMap.put(subClass, "go");
			}else{
				typeMap.put(subClass, "ftc");
			}
		}
		return typeMap;
	}

	private HashMap<String, String> getLabelMap(List<String> subClasses, List<String> equivalentClasses) throws NonExistingEntityException {

		HashMap<String, String> labelMap = new HashMap<String, String>();
		for (String subClass : subClasses) {
			String label = Application.brain.getLabel(subClass);
			labelMap.put(subClass, label);
		}

		for (String equivalentClass : equivalentClasses) {
			String label = Application.brain.getLabel(equivalentClass);
			labelMap.put(equivalentClass, label);
		}

		return labelMap;
	}


}
