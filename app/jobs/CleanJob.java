package jobs;

import java.io.IOException;

import build.Builder;
import play.Logger;
import play.cache.Cache;
import play.jobs.Job;
import uk.ac.ebi.brain.error.BrainException;

public class CleanJob extends Job {
	
	public void doJob() throws BrainException, IOException, ClassNotFoundException {
		Cache.set("jobRunning", true);
		Logger.info("Create the builder...");
		Builder builder = new Builder();
		Logger.info("Cleans temporary folder...");
		builder.clean();
		Logger.info("Job done!");
		Cache.set("jobRunning", null);
	}


}
