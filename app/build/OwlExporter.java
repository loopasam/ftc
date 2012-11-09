package build;

import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadNameException;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ExistingObjectPropertyException;
import uk.ac.ebi.brain.error.NewOntologyException;

public abstract class OwlExporter {

	private String pathOut;
	private Brain brain;
	public static String PREFIX = "http://www.ebi.ac.uk/ftc/";
	private String ontologyName;

	public String getOntologyName() {
		return ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = PREFIX + ontologyName;
	}

	public String getPathOut() {
		return pathOut;
	}

	public void setPathOut(String pathOut) {
		this.pathOut = pathOut;
	}

	public Brain getBrain() {
		return brain;
	}

	public void setBrain(Brain brain) {
		this.brain = brain;
	}

	public OwlExporter(String pathOut, String ontologyName) throws BrainException {
		this.setPathOut(pathOut);
		this.setOntologyName(ontologyName);
		Brain brain = new Brain(PREFIX, this.getOntologyName());
		//TODO preparer le skeleton de l'ontology avec les bonnes URI
		brain.addObjectProperty("part-of");
		
		this.setBrain(brain);
	}

	public abstract void start() throws FileNotFoundException, IOException, ClassNotFoundException;

	public abstract void save();

}
