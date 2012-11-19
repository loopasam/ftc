package build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLObjectProperty;

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
	private HashMap<String, String> drugBankRelationsMapper;


	public HashMap<String, String> getDrugBankRelationsMapper() {
		return drugBankRelationsMapper;
	}
	public void setDrugBankRelationsMapper(HashMap<String, String> drugBankRelationsMapper) {
		this.drugBankRelationsMapper = drugBankRelationsMapper;
	}
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
	//TODO exporter des meta infos pour l'ontology
	public OwlExporter(String pathOut, String ontologyName) throws BrainException, IOException {
		this.setPathOut(pathOut);
		this.setOntologyName(ontologyName);

		Brain brain = new Brain(PREFIX, this.getOntologyName());
		brain.prefix("http://purl.uniprot.org/core/", "core-uniprot");
		brain.prefix("http://www.drugbank.ca/drugs/", "drugbank");
		brain.prefix("http://purl.uniprot.org/uniprot/", "uniprot");
		brain.prefix("http://purl.obolibrary.org/obo/", "obo");

		//TBox specifications - Core external classes

		brain.addClass("http://purl.obolibrary.org/obo/GO_0003674");
		brain.label("GO_0003674", "molecular function");
		brain.comment("GO_0003674", "//TODO: A molecular function as defined by GO");

		brain.addClass("http://purl.obolibrary.org/obo/GO_0008150");
		brain.label("GO_0008150", "biological process");
		brain.comment("GO_0008150", "//TODO: A biological process as defined by GO");

		brain.addClass("http://purl.uniprot.org/core/Protein");
		brain.label("Protein", "protein");
		brain.comment("Protein", "//TODO: A protein as defined by Uniprot");

		brain.addClass("http://purl.obolibrary.org/obo/CHEBI_50906");
		brain.label("CHEBI_50906", "role");
		brain.comment("CHEBI_50906", "//TODO: A role as defined by chebi");

		//The compounds from DrugBank will be considered for their role
		//as drug
		brain.addClass("http://purl.obolibrary.org/obo/CHEBI_23888");
		brain.label("CHEBI_23888", "drug");
		brain.subClassOf("CHEBI_23888", "CHEBI_50906");
		brain.comment("CHEBI_23888", "//TODO: A drug as defined by chebi. The compounds " +
				"from DrugBank will be considered for their role as drug.");

		//FTC specification

		//Role of something capable of producing a therapeutic effect
		brain.addClass("FTC_C1");
		brain.label("FTC_C1", "therapeutic agent");
		brain.subClassOf("FTC_C1", "CHEBI_23888");
		brain.comment("FTC_C1", "Role of a drug capable of producing a therapeutic effect.");

		//RBox specifications

		//GO RBox logic - See http://www.geneontology.org/GO.ontology-ext.relations.shtml
		brain.addObjectProperty("http://purl.obolibrary.org/obo/BFO_0000050");
		brain.label("BFO_0000050", "part-of");
		brain.transitive("BFO_0000050");
		brain.comment("BFO_0000050", "//TODO: As defined by RO.");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/RO_0002211");
		brain.label("RO_0002211", "regulates");
		brain.comment("RO_0002211", "//TODO: As defined by RO.");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/RO_0002213");
		brain.label("RO_0002213", "positively-regulates");
		brain.comment("RO_0002213", "//TODO: As defined by RO.");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/RO_0002212");
		brain.label("RO_0002212", "negatively-regulates");
		brain.comment("RO_0002212", "//TODO: As defined by RO.");

		brain.addObjectProperty("http://purl.obolibrary.org/obo/BFO_0000051");
		brain.label("BFO_0000051", "has-part");
		brain.transitive("BFO_0000051");
		brain.comment("BFO_0000051", "//TODO: As defined by RO.");

		brain.subPropertyOf("RO_0002212", "RO_0002211");
		brain.subPropertyOf("RO_0002213", "RO_0002211");
		brain.chain("RO_0002211 o BFO_0000050", "RO_0002211");

		//FTC RBox logic
		brain.addObjectProperty("FTC_R1");
		brain.label("FTC_R1", "involved-in");
		brain.domain("FTC_R1", "Protein");
		brain.range("FTC_R1", "GO_0008150");
		brain.comment("FTC_R1", "Entails the participation of a protein in a biological process");

		brain.addObjectProperty("FTC_R2");
		brain.label("FTC_R2", "has-function");
		brain.domain("FTC_R2", "Protein");
		brain.range("FTC_R2", "GO_0003674");
		brain.comment("FTC_R2", "Describes the molecular function born by a protein");

		//http://en.wikipedia.org/wiki/Mechanism_of_action
		//Specific biochemical interaction through which a drug substance will affect
		//the activity of a protein.
		//The property refers to the specific molecular 
		//targets to which the drug binds, such as an enzyme or receptor.
		brain.addObjectProperty("FTC_R3");
		brain.label("FTC_R3", "perturbs");
		brain.domain("FTC_R3", "CHEBI_23888");
		brain.range("FTC_R3", "Protein");
		brain.comment("FTC_R3", "Specific biochemical interaction through which a drug " +
				"substance will affect the activity of a protein. The property " +
				"refers to the specific molecular targets to " +
				"which the drug binds, such as an enzyme or receptor.");

		//http://en.wikipedia.org/wiki/Mechanism_of_action
		//Specific biochemical interaction through which a drug substance will decrease
		//the activity of a protein.
		brain.addObjectProperty("FTC_R4");
		brain.label("FTC_R4", "negatively-perturbs");
		brain.subPropertyOf("FTC_R4", "FTC_R3");
		brain.comment("FTC_R4", "Specific biochemical interaction through which a drug " +
				"substance will decrease the activity of a protein. The property " +
				"refers to the specific molecular targets to " +
				"which the drug binds, such as an enzyme or receptor.");

		//http://en.wikipedia.org/wiki/Mechanism_of_action
		//Specific biochemical interaction through which a drug substance will increase
		//the activity of a protein.
		brain.addObjectProperty("FTC_R5");
		brain.label("FTC_R5", "positively-perturbs");
		brain.subPropertyOf("FTC_R5", "FTC_R3");
		brain.comment("FTC_R5", "Specific biochemical interaction through which a drug " +
				"substance will increase the activity of a protein. The property " +
				"refers to the specific molecular targets to " +
				"which the drug binds, such as an enzyme or receptor.");


		this.setBrain(brain);

		//Mapper to convert strings from the GO.ser into OWL properties

		HashMap<String, String> goRelationMapper = new HashMap<String, String>();
		goRelationMapper.put("part_of", "BFO_0000050");
		goRelationMapper.put("regulates", "RO_0002211");
		goRelationMapper.put("positively_regulates", "RO_0002213");
		goRelationMapper.put("negatively_regulates", "RO_0002212");
		goRelationMapper.put("has_part", "BFO_0000051");
		this.setGoRelationsMapper(goRelationMapper);

		Logger.info("Loading the formal mappings of DrugBank relations...");
		this.setDrugBankRelationsMapper(loadDrugBankMapper());
	}

	private HashMap<String, String> loadDrugBankMapper() throws IOException {
		FileInputStream fstream = new FileInputStream("data/drugbank-relations-mappings.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		HashMap<String, String> relationMapping = new HashMap<String, String>();
		while ((line = br.readLine()) != null)   {
			Pattern pattern = Pattern.compile("(.*) -> (.*)");
			Matcher matcher = pattern.matcher(line);
			matcher.find();
			relationMapping.put(matcher.group(1), matcher.group(2));
		}
		br.close();
		return relationMapping;
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
