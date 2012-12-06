package build;

import java.io.FileNotFoundException;
import java.io.IOException;


import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser.Parser;

import play.Logger;
import play.test.Fixtures;
import uk.ac.ebi.brain.error.BrainException;

public class Builder {

	//TODO do a clean method that removes the tmp folder content
	//TODO method to drop the DB

	public DrugBank serializeDrugBank() throws FileNotFoundException, IOException {
		Logger.info("Parsing DrugBank...");
		DrugBankParser drugBankParser = new DrugBankParser("data/tmp/drugbank.ser");
		drugBankParser.start();
		return drugBankParser.save();
	}

	public void addGoAnnotations() throws FileNotFoundException, IOException, ClassNotFoundException {
		Logger.info("Connecting to GOA web services to update DrugBank human proteins annotations...");
		GoaConnector connector = new GoaConnector();
		connector.start();
		connector.save();
	}

	public void serializeGo() throws IOException {
		Logger.info("Parsing GO...");
		GOParser goParser = new GOParser("data/tmp/go.ser");
		goParser.start();
		goParser.save();
	}

	public void exportFullStructureToOwl() throws BrainException, FileNotFoundException, IOException, ClassNotFoundException {
		FullOwlExporter exporter = new FullOwlExporter("data/ftc-kb-full.owl", "ftc-kb-full.owl");
		exporter.start();
		exporter.save();
	}

	public void deleteDatabase() {
		Logger.info("Deleting database...");
		Fixtures.deleteDatabase();
		Logger.info("Database deleted");
	}

	public void createAndPopulateDatabase() throws BrainException, IOException, ClassNotFoundException {
		DatabaseFiller filler = new DatabaseFiller("data/ftc-kb-full.owl");
		filler.start();
//		filler.test();
	}

}
