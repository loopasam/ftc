package jobs;

import java.io.FileNotFoundException;
import java.io.IOException;

import build.Builder;
import play.Logger;
import play.jobs.Job;
import uk.ac.ebi.brain.error.BrainException;

public class ConvertionJob extends Job {

	public void doJob() throws BrainException, IOException {
		Logger.info("Create the builder...");
		Builder builder = new Builder();
		builder.deleteDatabase();
		builder.createAndPopulateDatabase();
		Logger.info("Job done!");
	}
}
