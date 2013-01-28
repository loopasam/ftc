package jobs;

import java.io.IOException;

import build.Builder;
import play.Logger;
import play.cache.Cache;
import play.jobs.Job;
import uk.ac.ebi.brain.error.BrainException;

public class EvaluationJob extends Job {
	
	public void doJob() {
		Cache.set("jobRunning", true);
		Logger.info("Create the builder...");
		Builder builder = new Builder();
		builder.evaluate();
		Logger.info("Job done!");
		Cache.set("jobRunning", null);
	}

}
