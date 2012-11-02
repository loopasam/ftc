package build;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser.Parser;

public class Builder {

	//TODO do a clean method that removes the tmp folder content

	public DrugBank serializeDrugBank() throws FileNotFoundException, IOException {
		DrugBankParser drugBankParser = new DrugBankParser("data/drugbank.ser");
		drugBankParser.start();
		return drugBankParser.save();
	}


}
