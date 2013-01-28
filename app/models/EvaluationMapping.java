package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class EvaluationMapping extends Model {
	
	//The actual mapping
	public String definition;
	
	//The FTC label
	public String label;
	
	//Compounds that are present in the FTC but not in the ATC
	//for the current mapping.
	@ElementCollection
	public List<String> falsePositives;
	
	//Compounds that are present in the FTC and in the ATC
	//for the current mapping.
	@ElementCollection
	public List<String> truePositives;
	
	//Compounds that are present in the ATC but not in the FTC
	//for the current mapping.
	@ElementCollection
	public List<String> falseNegatives;

	//ATC classes in the mapping definition
	@ElementCollection
	public List<String> atcClasses;

	//FTC classes in the mapping definition
	@ElementCollection
	public List<String> ftcClasses;
	
	//DrugBank agents found under the mapped ATC classes.
	@ElementCollection
	public List<String> atcDrugs;
	
	//DrugBank agents found under the mapped FTC classes.
	@ElementCollection
	public List<String> ftcDrugs;

	public EvaluationMapping() {
		this.atcClasses = new ArrayList<String>();
		this.ftcClasses = new ArrayList<String>();
		this.ftcDrugs = new ArrayList<String>();
		this.atcDrugs = new ArrayList<String>();
		this.falseNegatives = new ArrayList<String>();
		this.falsePositives = new ArrayList<String>();
		this.truePositives = new ArrayList<String>();
	}

}
