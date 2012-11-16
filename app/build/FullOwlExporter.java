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


		//TODO adding proteins
		//TODO adding FTC classes

		//TODO: Do the BP first

		Logger.info("Creating the FTC categories...");

		for (GoTerm goTerm : go.getBioProcesses()) {
			String goTermId = goTerm.getId().replace(":", "_");
			for (GoRelation relation : goTerm.getRelations()) {
				if(relation.getType().equals("positively_regulates")){

					GoTerm positivelyRegulatedparentTerm = go.getTerm(relation.getTarget());
					String stemParentTerm = positivelyRegulatedparentTerm.getId().replace("GO:", "");

					//Anti-pattern
					String antiClassId = "FTC_A" + stemParentTerm;
					if(!this.getBrain().knowsClass(antiClassId)){
						this.getBrain().addClass(antiClassId);
					}
					this.getBrain().label(antiClassId, "Anti-" + positivelyRegulatedparentTerm.getName() + " agent");
					this.getBrain().subClassOf(antiClassId, "FTC_C1");
					//TODO: a mettre le probleme en dessous en test une fois que la FTC est classififed
					//TODO better written justification for patterns on wiki
					
					//The expression uses the class name (ex: 'negative regulation of blood coagulation') and
					//not the corresponding class expression ('negatively-regulates some blood-coagulation')
					//as only subclasses are considered in class expressions. By using the full class name
					//('negative regulation of blood coagulation') we get the subclasses and not the descendant classes
					//Some classes got better classified this way, (ex: pro-fibrinolysis becomes a subclass
					//of anti-blood coagulation when using the full class name.)
					String antiExpression = "CHEBI_23888 and FTC_R4 some (Protein and FTC_R1 some (GO_0008150 and " +
							goTermId + "))";


					this.getBrain().equivalentClasses(antiClassId, antiExpression);

					//Pro-pattern
					String proClassId = "FTC_P" + stemParentTerm;
					if(!this.getBrain().knowsClass(proClassId)){
						this.getBrain().addClass(proClassId);
					}

					this.getBrain().label(proClassId, "Pro-" + positivelyRegulatedparentTerm.getName() + " agent");
					this.getBrain().subClassOf(proClassId, "FTC_C1");
					String proExpression = "CHEBI_23888 and FTC_R5 some (Protein and FTC_R1 some (GO_0008150 and " +
							goTermId + "))";

					this.getBrain().equivalentClasses(proClassId, proExpression);


				}else if(relation.getType().equals("negatively_regulates")){

					GoTerm negativelyRegulatedparentTerm = go.getTerm(relation.getTarget());
					String stemParentTerm = negativelyRegulatedparentTerm.getId().replace("GO:", "");

					//Anti-pattern
					String antiClassId = "FTC_A" + stemParentTerm;
					if(!this.getBrain().knowsClass(antiClassId)){
						this.getBrain().addClass(antiClassId);
					}
					this.getBrain().label(antiClassId, "Anti-" + negativelyRegulatedparentTerm.getName() + " agent");
					this.getBrain().subClassOf(antiClassId, "FTC_C1");

					String antiExpression = "CHEBI_23888 and FTC_R5 some (Protein and FTC_R1 some (GO_0008150 and " +
							goTermId + "))";


					this.getBrain().equivalentClasses(antiClassId, antiExpression);

					//Pro-pattern
					String proClassId = "FTC_P" + stemParentTerm;
					if(!this.getBrain().knowsClass(proClassId)){
						this.getBrain().addClass(proClassId);
					}

					this.getBrain().label(proClassId, "Pro-" + negativelyRegulatedparentTerm.getName() + " agent");
					this.getBrain().subClassOf(proClassId, "FTC_C1");
					String proExpression = "CHEBI_23888 and FTC_R4 some (Protein and FTC_R1 some (GO_0008150 and " +
							goTermId + "))";


					this.getBrain().equivalentClasses(proClassId, proExpression);


				}
			}

		}


		Logger.info("done");
	}


}
