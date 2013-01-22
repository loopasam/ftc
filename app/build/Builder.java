package build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.io.FileUtils;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser.Parser;

import play.Logger;
import play.test.Fixtures;
import uk.ac.ebi.brain.error.BrainException;

public class Builder {
	
	public void createTmpStructure() {
		Logger.info("Creating the temporary directory structure...");
		new File("data/tmp/graphs").mkdirs();
		new File("data/archives").mkdirs();
	}

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
	}
	
	public void clean() throws IOException {
		FileUtils.deleteDirectory(new File("data/tmp"));
	}

	public void archive() throws IOException {
		Logger.info("Copying the knowledge base into the archives/ directory...");
		File source = new File("data/ftc-kb-full.owl");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date();
		File destination = new File("data/archives/ftc-kb-full-" + sdf.format(dt) + ".owl");
		FileUtils.copyFile(source, destination);
	}

}
