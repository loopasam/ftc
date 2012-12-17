package jobs;

import java.util.List;

import models.OwlResult;

import controllers.Application;
import play.Logger;
import play.jobs.Job;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.NewOntologyException;

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
		Brain brainQuery = new Brain();
		Logger.info("Learning static brain...");
		brainQuery.learn(Application.brain);
		Logger.info("Learning done!");
		Logger.info("Does the query... " + this.query);
		List<String> subClasses = brainQuery.getSubClassesFromLabel(this.query, false);
		List<String> equivalentClasses = brainQuery.getEquivalentClassesFromLabel(this.query);
		Logger.info("Creating result object...");
		OwlResult result = new OwlResult(this.query, subClasses, equivalentClasses);
		Logger.info("Query done, returns the results");
		//TODO returns only an id or somehting
		return result;
	}


}
