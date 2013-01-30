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
		List<String> subClasses = Application.brain.getSubClassesFromLabel(this.query, false);

		OwlResult result = null;

		if(subClasses.size() < 1500){
			result = new OwlResult(this.query, false);
			result.subClasses = subClasses;
		}else{
			result = new OwlResult(this.query, true);
		}
		result.save();
		return result;
	}

}
