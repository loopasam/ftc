package build;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser.Parser;

import play.Logger;

public class Builder {

	//TODO do a clean method that removes the tmp folder content

	public DrugBank serializeDrugBank() throws FileNotFoundException, IOException {
		Logger.info("Parsing DrugBank...");
		DrugBankParser drugBankParser = new DrugBankParser("data/drugbank.ser");
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
		GOParser goParser = new GOParser("data/go.ser");
		goParser.start();
		goParser.save();
	}


}
