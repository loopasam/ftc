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
		Logger.info("Prepare the folder structure...");
		builder.createTmpStructure();
		Logger.info("Serialize DrugBank...");
		builder.serializeDrugBank();
		Logger.info("Adds the GO annotations...");
		builder.addGoAnnotations();
		Logger.info("Serialize the GO...");
		builder.serializeGo();
		Logger.info("Convert into OWL...");
		builder.exportFullStructureToOwl();
		Logger.info("Archives the knowledge base...");
		builder.archive();
		Logger.info("Job done!");
	}
}
