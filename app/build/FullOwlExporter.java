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
import uk.ac.ebi.brain.error.NonExistingEntityException;
import uk.ac.ebi.brain.error.StorageException;

public class FullOwlExporter extends OwlExporter {

	public FullOwlExporter(String pathOut, String ontologyName) throws BrainException, IOException {
		super(pathOut, ontologyName);
	}

	@Override
	public void start() throws FileNotFoundException, IOException, ClassNotFoundException, BrainException {


		GeneOntology go = new GeneOntology("data/tmp/go.ser");

		//Converts GO into OWL
		convertGoInOwl(go);

		//Add the FTC classes for biological processes
		addFtcClassesForBiologicalProcesses(go);

		//Add the FTC classes for molecular functions
		addFtcClassesForMolecularFunctions(go);

		Logger.info("Done with the FTC classes");

		//Adds the axioms related to the mecanism of action
		addDrugAndProteinAxioms(go);
	}

	private void addDrugAndProteinAxioms(GeneOntology go) throws FileNotFoundException, IOException, ClassNotFoundException, BrainException {
		Logger.info("Loading DrugBank axioms for Human proteins with a least one formal relation...");
		DrugBank drugBank = new DrugBank("data/tmp/drugbank-goa.ser");

		//Retrieves all the non-experimental drugs
		for (Drug drug : drugBank.getNonExperimentalDrugs()) {
			//Iterates over the partners of the drug
			for (TargetRelation relation : drug.getTargetRelations()) {
				//Retrieves the partner object
				Partner partner = drugBank.getPartner(relation.getPartnerId());
				//Retrieve only the human proteins
				if(partner.getSpecies().getCategory() != null && partner.getSpecies().getCategory().equals("human")){
					//Check if the partner as some non-IEA annotations and not CC
					//It could be only a Uniprot protein as others proteins have no annotations (null) due to the
					//GOAConnector
					if(partner.getNonIEAAnnotationsNonCC() != null && partner.getNonIEAAnnotationsNonCC().size() > 0){

						//Iterates over the actions linking the drug to the partner to see 
						//if the action is mapped to an OWL property (meaningfull)
						for (String action : relation.getActions()) {

							if(this.getDrugBankRelationsMapper().get(action) != null){


								//If in this block, it means that the drug pertubs (the type of perturbation being normalized)
								//and the target is a human protein: The axiom must be added

								String drugBankFullClassName = "http://www.drugbank.ca/drugs/" + drug.getId();
								String targetFullClassName = "http://purl.uniprot.org/uniprot/" + partner.getUniprotIdentifer();
								String drugBankClassName = drug.getId();
								String targetClassName = partner.getUniprotIdentifer();

								if(!this.getBrain().knowsClass(drugBankClassName)){
									//Add the class for the drug if not present
									this.getBrain().addClass(drugBankFullClassName);
									this.getBrain().label(drugBankClassName, "'" + drug.getName() + "'");
									this.getBrain().subClassOf(drugBankClassName, "CHEBI_23888");
									this.getBrain().subClassOf(drugBankClassName, "FTC_C2");
								}

								if(!this.getBrain().knowsClass(targetClassName)){
									//Add the class for the target if not present
									this.getBrain().addClass(targetFullClassName);
									this.getBrain().label(targetClassName, "'" + partner.getName() + "'");
									this.getBrain().subClassOf(targetClassName, "Protein");

									//Add the annotations (non IEA/ non CC for the protein)
									for (GoAnnotation goAnnotation : partner.getNonIEAAnnotationsNonCC()) {
										String goClass = goAnnotation.getGoId().replace(":", "_");

										if(go.isTermABioProcess(goAnnotation.getGoId())){

											this.getBrain().subClassOf(targetClassName, "FTC_R1 some " + goClass);

										}else if(go.isTermAMolecularFunction(goAnnotation.getGoId())){

											this.getBrain().subClassOf(targetClassName, "FTC_R2 some " + goClass);
										}


									}
								}

								//Asserts the axiom between the drug and its target
								this.getBrain().subClassOf(drugBankClassName, this.getDrugBankRelationsMapper().get(action) + " some " +
										targetClassName);

							}
						}
					}
				}
			}
		}

	}

	private void addFtcClassesForMolecularFunctions(GeneOntology go) throws BrainException {		
		Logger.info("Creating FTC categories for molecular functions...");

		for (GoTerm goTerm : go.getMolecularFunctions()) {
			String goTermId = goTerm.getId().replace(":", "_");
			String stemGoTerm = goTerm.getId().replace("GO:", "");

			//Anti-pattern
			String antiClassId = "FTC_A" + stemGoTerm;
			this.getBrain().addClass(antiClassId);
			this.getBrain().label(antiClassId, "'" + "Anti-" + goTerm.getName() + " agent" + "'");
			String idForUrl = goTerm.getId().replaceAll("_", ":");
			this.getBrain().comment(antiClassId, "Agent that stops, prevents, or reduces the frequency, " +
					"rate or extent of the " +
					"<a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + idForUrl + "'>" + goTerm.getName() + "</a>.");
			this.getBrain().subClassOf(antiClassId, "FTC_C1");

			String antiExpression = "CHEBI_23888 and FTC_R4 some (Protein and FTC_R2 some (GO_0003674 and " +
					goTermId + "))";

			this.getBrain().equivalentClasses(antiClassId, antiExpression);

			//Pro-pattern
			String proClassId = "FTC_P" + stemGoTerm;
			this.getBrain().addClass(proClassId);

			this.getBrain().label(proClassId, "'" + "Pro-" + goTerm.getName() + " agent");
			this.getBrain().comment(proClassId, "Agent that activates or increases the frequency, " +
					"rate or extent of the " +
					"<a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + idForUrl + "'>" + goTerm.getName() + "</a>.");			
			this.getBrain().subClassOf(proClassId, "FTC_C1");
			String proExpression = "CHEBI_23888 and FTC_R5 some (Protein and FTC_R2 some (GO_0003674 and " +
					goTermId + "))";

			this.getBrain().equivalentClasses(proClassId, proExpression);

		}
	}

	private void addFtcClassesForBiologicalProcesses(GeneOntology go) throws BrainException {	
		Logger.info("Creating the FTC categories for biological processes...");

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
					this.getBrain().label(antiClassId, "'" + "Anti-" + positivelyRegulatedparentTerm.getName() + " agent" + "'");


					String idForUrl = positivelyRegulatedparentTerm.getId().replaceAll("_", ":");
					this.getBrain().comment(antiClassId, "Agent that stops, prevents, or reduces the frequency, " +
							"rate or extent of the " +
							"<a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + idForUrl + "'>" + 
							positivelyRegulatedparentTerm.getName() + "</a>.");					
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

					this.getBrain().label(proClassId, "'" + "Pro-" + positivelyRegulatedparentTerm.getName() + " agent" + "'");
					this.getBrain().comment(proClassId, "Agent that activates or increases the frequency, " +
							"rate or extent of the " +
							"<a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + idForUrl + "'>" + 
							positivelyRegulatedparentTerm.getName() + "</a>.");					
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
					this.getBrain().label(antiClassId, "'" + "Anti-" + negativelyRegulatedparentTerm.getName() + " agent" + "'");


					String idForUrl = negativelyRegulatedparentTerm.getId().replaceAll("_", ":");
					this.getBrain().comment(antiClassId, "Agent that stops, prevents, or reduces the frequency, " +
							"rate or extent of the " +
							"<a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + idForUrl + "'>" + 
							negativelyRegulatedparentTerm.getName() + "</a>.");					
					this.getBrain().subClassOf(antiClassId, "FTC_C1");

					String antiExpression = "CHEBI_23888 and FTC_R5 some (Protein and FTC_R1 some (GO_0008150 and " +
							goTermId + "))";

					this.getBrain().equivalentClasses(antiClassId, antiExpression);

					//Pro-pattern
					String proClassId = "FTC_P" + stemParentTerm;
					if(!this.getBrain().knowsClass(proClassId)){
						this.getBrain().addClass(proClassId);
					}

					this.getBrain().label(proClassId, "'" + "Pro-" + negativelyRegulatedparentTerm.getName() + " agent" + "'");
					this.getBrain().comment(proClassId, "Agent that activates or increases the frequency, " +
							"rate or extent of the " +
							"<a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" + idForUrl + "'>" + 
							negativelyRegulatedparentTerm.getName() + "</a>.");					
					this.getBrain().subClassOf(proClassId, "FTC_C1");
					String proExpression = "CHEBI_23888 and FTC_R4 some (Protein and FTC_R1 some (GO_0008150 and " +
							goTermId + "))";

					this.getBrain().equivalentClasses(proClassId, proExpression);
				}
			}

		}
	}

	private void convertGoInOwl(GeneOntology go) throws BrainException, FileNotFoundException, IOException, ClassNotFoundException {
		Logger.info("Converting GO into OWL...");
		Logger.info("Adding the Go terms to the Brain...");
		//Adds just the terms first
		for (GoTerm goTerm : go.getBioProcessesAndMolecularFunctions()) {
			String classId = goTerm.getId().replaceAll(":", "_");
			if(!this.getBrain().knowsClass(classId)){
				this.getBrain().addClass("http://purl.obolibrary.org/obo/" + classId);
				this.getBrain().label(classId, "'" + goTerm.getName() + "'");
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
	}


}
