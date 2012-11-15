package build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import play.Logger;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadNameException;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.StorageException;

public class FullOwlExporter extends OwlExporter {

	public FullOwlExporter(String pathOut, String ontologyName) throws BrainException {
		super(pathOut, ontologyName);
	}

	@Override
	public void start() throws FileNotFoundException, IOException, ClassNotFoundException, BrainException {
		
		//Converts GO into OWL
		
		Logger.info("Converting GO into OWL...");
		GeneOntology go = new GeneOntology("data/tmp/go.ser");
		Logger.info("Adding the Go terms to the Brain...");
		//Adds just the terms first
		for (GoTerm goTerm : go.getBioProcessesAndMolecularFunctions()) {
			String classId = goTerm.getId().replaceAll(":", "_");
			if(!this.getBrain().knowsClass(classId)){
				this.getBrain().addClass("http://purl.obolibrary.org/obo/" + classId);
				this.getBrain().label(classId, goTerm.getName());
			}else{
				Logger.warn("The class '"+classId+"' ("+goTerm.getName()+") already exists");
			}
		}

		//Not hyper-optimised but kept separated for the sake of clarity.
		Logger.info("Adding the GO axioms to the Brain...");

		//Stored the relations not supported by the FTC at the time being
		Set<String> unsupportedRelations = new HashSet<String>();

		for (GoTerm goTerm : go.getBioProcessesAndMolecularFunctions()) {
			String classId = goTerm.getId().replaceAll(":", "_");

			for (GoRelation relation : goTerm.getRelations()) {
				if(relationSupported(relation) || relation.getType().equals("is_a")){
					String parentId = relation.getTarget().replaceAll(":", "_");
					String expression = this.getGoRelationsMapper().get(relation.getType()) + " some " + parentId;

					if(relation.getType().equals("is_a")){
						this.getBrain().subClassOf(classId, parentId);
					}else{
						this.getBrain().subClassOf(classId, expression);
					}

				}else{
					unsupportedRelations.add(relation.getType());
				}
			}
		}

		//List the unsupported relations - information
		for (String unsupportedRelation : unsupportedRelations) {
			Logger.info("The relation '"+unsupportedRelation+"' present in the GO is not supported in the FTC");
		}
		
		//End conversion GO
		
		
	}


}
