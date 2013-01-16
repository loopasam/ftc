package queries;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;

public class Query {

	public static void main(String[] args) throws BrainException {
		Brain brain = new Brain("http://localhost/", "http://localhost/integrated.owl");
		System.out.println("Learning NCBI...");
		brain.learn("data/NCBI-taxonomy-mammals.owl");
		System.out.println("Learning GO...");
		brain.learn("data/gene_ontology.owl");
		System.out.println("Learning uniprot...");
		brain.learn("data/uniprot.owl");
		System.out.println("Saving integrated...");
		brain.save("data/integrated.owl");
		brain.sleep();
		
	}
}
