package jobs;

import java.io.FileNotFoundException;
import java.io.IOException;

import build.Builder;
import play.Logger;
import play.jobs.Job;
import uk.ac.ebi.brain.error.BrainException;

public class FullBuildJob extends Job {

	public void doJob() throws FileNotFoundException, BrainException, IOException, ClassNotFoundException {
		Logger.info("Create the builder...");
		Builder builder = new Builder();
		Logger.info("Serialize DrugBank...");
		builder.serializeDrugBank();
		Logger.info("Adds the GO annotations...");
		builder.addGoAnnotations();
		Logger.info("Serialize the GO...");
		builder.serializeGo();
		Logger.info("Convert into OWL...");
		builder.exportFullStructureToOwl();
		//TODO clean the folder
		Logger.info("Job done!");
	}


}
