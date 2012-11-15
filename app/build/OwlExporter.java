package build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import play.Logger;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadNameException;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ExistingObjectPropertyException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.StorageException;

public abstract class OwlExporter {

	private String pathOut;
	private Brain brain;
	public static String PREFIX = "http://www.ebi.ac.uk/ftc/";
	private String ontologyName;
	private HashMap<String, String> goRelationsMapper;

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

	public HashMap<String, String> getGoRelationsMapper() {
		return goRelationsMapper;
	}

	public void setGoRelationsMapper(HashMap<String, String> goRelationsMapper) {
		this.goRelationsMapper = goRelationsMapper;
	}

	public Brain getBrain() {
		return brain;
	}

	public void setBrain(Brain brain) {
		this.brain = brain;
	}

	//TODO comment on what this is doing 
	public OwlExporter(String pathOut, String ontologyName) throws BrainException {
		this.setPathOut(pathOut);
		this.setOntologyName(ontologyName);

		Brain brain = new Brain(PREFIX, this.getOntologyName());
		brain.prefix("http://purl.uniprot.org/core/", "uniprot");
		brain.prefix("http://purl.obolibrary.org/obo/", "obo");


		//TBox specifications - Core external classes

		brain.addClass("http://purl.obolibrary.org/obo/GO_0003674");
		brain.label("GO_0003674", "molecular function");

		brain.addClass("http://purl.obolibrary.org/obo/GO_0008150");
		brain.label("GO_0008150", "biological process");
		
		brain.addClass("http://purl.uniprot.org/core/Protein");
		brain.label("Protein", "protein");
		
		brain.addClass("http://purl.obolibrary.org/obo/CHEBI_24431");
		brain.label("CHEBI_24431", "chemical entity");

		brain.addClass("http://purl.obolibrary.org/obo/CHEBI_50906");
		brain.label("CHEBI_50906", "role");

		//FTC specification

		brain.addClass("FTC_C1");
		brain.label("FTC_C1", "agent");
		brain.subClassOf("FTC_C1", "CHEBI_50906");

		//RBox specifications

		//GO RBox logic - See http://www.geneontology.org/GO.ontology-ext.relations.shtml
		brain.addObjectProperty("http://purl.obolibrary.org/obo/BFO_0000050");
		brain.label("BFO_0000050", "part-of");
		brain.transitive("BFO_0000050");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/RO_0002211");
		brain.label("RO_0002211", "regulates");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/RO_0002213");
		brain.label("RO_0002213", "positively-regulates");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/RO_0002212");
		brain.label("RO_0002212", "negatively-regulates");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/BFO_0000051");
		brain.label("BFO_0000051", "has-part");
		brain.transitive("BFO_0000051");

		brain.subPropertyOf("RO_0002212", "RO_0002211");
		brain.subPropertyOf("RO_0002213", "RO_0002211");
		brain.chain("RO_0002211 o BFO_0000050", "RO_0002211");

		//FTC RBox logic
		brain.addObjectProperty("FTC_R1");
		brain.label("FTC_R1", "involved-in");

		brain.addObjectProperty("FTC_R2");
		brain.label("FTC_R2", "has-function");

		this.setBrain(brain);

		//Mapper to convert strings from the GO.ser into OWL properties

		HashMap<String, String> goRelationMapper = new HashMap<String, String>();
		goRelationMapper.put("part_of", "BFO_0000050");
		goRelationMapper.put("regulates", "RO_0002211");
		goRelationMapper.put("positively_regulates", "RO_0002213");
		goRelationMapper.put("negatively_regulates", "RO_0002212");
		goRelationMapper.put("has_part", "BFO_0000051");
		this.setGoRelationsMapper(goRelationMapper);
	}

	public abstract void start() throws FileNotFoundException, IOException, ClassNotFoundException, BrainException;

	public void save() throws StorageException {
		Logger.info("Saving the OWL file...");
		this.getBrain().save(this.getPathOut());
	}

	protected boolean relationSupported(GoRelation relation) {

		if(this.getGoRelationsMapper().containsKey(relation.getType())){
			return true;
		}else{
			return false;
		}
	}


}
