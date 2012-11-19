package build;

import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.ebi.brain.error.BrainException;

public class MinOwlExporter extends OwlExporter {

	public MinOwlExporter(String pathOut, String ontologyName) throws BrainException, IOException {
		super(pathOut, ontologyName);
	}

	@Override
	public void start() throws FileNotFoundException, IOException, ClassNotFoundException, BrainException {
		// TODO Auto-generated method stub

	}

}
