package analysis;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.ExistingEntityException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingEntityException;

public class Analysis {

	Brain brain;

	public Analysis() throws BrainException {
		brain = new Brain();
		System.out.println("Learning...");
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		System.out.println("learning done!");
	}

	public static void main(String[] args) throws Exception {
		Analysis analysis = new Analysis();
		System.out.println("computing and exporting distributions...");
		analysis.exportDistributionMoa("/home/samuel/git/ftc/data/analysis/direct-distribution-moas.csv", true);
		analysis.exportDistributionMoa("/home/samuel/git/ftc/data/analysis/undirect-distribution-moas.csv", false);
		System.out.println("export done!");
		analysis.done();
	}

	private void done() {
		brain.sleep();
	}

	private void exportDistributionMoa(String path, boolean direct) throws Exception {

		List<Distribution> distributions = new ArrayList<Distribution>();

		//Get all the drugBank compounds inside the FTC
		List<String> drugBankCompounds = brain.getSubClasses("FTC_C2", false);
		for (String drugBankCompound : drugBankCompounds) {
			Distribution distribution = new Distribution();

			//Get the direct super classes
			distribution.numberOfOccurences = brain.getSuperClasses(drugBankCompound, direct).size();

			distribution.label = brain.getLabel(drugBankCompound);
			distribution.classId = drugBankCompound;
			distributions.add(distribution);			

		}
		CSVWriter writer = new CSVWriter(path);
		writer.write(distributions);
	}

}
