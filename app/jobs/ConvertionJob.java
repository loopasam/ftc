package jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import controllers.Administration;

import build.Builder;
import play.Logger;
import play.cache.Cache;
import play.jobs.Job;
import uk.ac.ebi.brain.error.BrainException;

public class ConvertionJob extends Job implements Serializable {

	public void doJob() throws BrainException, IOException, ClassNotFoundException {
		Cache.set("jobRunning", true);
		Logger.info("Create the builder...");
		Builder builder = new Builder();
		builder.createAndPopulateDatabase();
		Logger.info("Job done!");
		Cache.set("jobRunning", null);
	}

}
