package jobs;

import java.util.List;

import controllers.Application;
import play.Logger;
import play.jobs.Job;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.NewOntologyException;

public class OwlQueryJob extends Job<List<String>> {

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

	public List<String> doJobWithResult() throws BrainException {
		Logger.info("Starting asynchronous query...");
		Brain brainQuery = new Brain();
		Logger.info("Learning static brain...");
		brainQuery.learn(Application.brain);
		Logger.info("Learning done!");
		Logger.info("Does the query... " + this.query);
		List<String> subClasses = brainQuery.getSubClassesFromLabel(this.query, false);
		Logger.info("Query done, returns the results");
		return subClasses;
	}


}
