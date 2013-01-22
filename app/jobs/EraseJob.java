package jobs;

import java.io.IOException;

import play.Logger;
import play.cache.Cache;
import play.jobs.Job;
import uk.ac.ebi.brain.error.BrainException;
import build.Builder;

public class EraseJob extends Job {
	public void doJob() throws BrainException, IOException, ClassNotFoundException {
		Cache.set("jobRunning", true);
		Logger.info("Create the builder...");
		Builder builder = new Builder();
		Logger.info("Clearing database...");
		builder.deleteDatabase();
		Logger.info("Job done!");
		Cache.set("jobRunning", null);
	}

}
