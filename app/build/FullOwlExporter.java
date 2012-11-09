package build;

import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.NewOntologyException;

public class FullOwlExporter extends OwlExporter {

	public FullOwlExporter(String pathOut, String ontologyName) throws BrainException {
		super(pathOut, ontologyName);
	}

	@Override
	public void start() throws FileNotFoundException, IOException, ClassNotFoundException {
		GeneOntology go = new GeneOntology("data/tmp/go.ser");
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}



}
