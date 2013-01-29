package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class EvaluationMapping extends Model {

	//The actual mapping
	public String definition;

	//Compounds that are present in the FTC but not in the ATC
	//for the current mapping.
	@JoinTable(name = "EvaluationMapping_falsePositives")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> falsePositives;

	//Compounds that are present in the FTC and in the ATC
	//for the current mapping.
	@JoinTable(name = "EvaluationMapping_truePositives")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> truePositives;

	//Compounds that are present in the ATC but not in the FTC
	//for the current mapping.
	@JoinTable(name = "EvaluationMapping_falseNegatives")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> falseNegatives;

	//ATC classes in the mapping definition
	@ElementCollection
	public List<String> atcClasses;

	//FTC class in the mapping definition
	public String ftcClass;

	//DrugBank agents found under the mapped ATC classes.
	@JoinTable(name = "EvaluationMapping_atcDrugs")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> atcDrugs;

	//DrugBank agents found under the mapped FTC classes.
	@JoinTable(name = "EvaluationMapping_ftcDrugs")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> ftcDrugs;

	//TODO check there is stuff in double
	public EvaluationMapping() {
		this.atcClasses = new ArrayList<String>();
		this.ftcDrugs = new ArrayList<Agent>();
		this.atcDrugs = new ArrayList<Agent>();
		this.falseNegatives = new ArrayList<Agent>();
		this.falsePositives = new ArrayList<Agent>();
		this.truePositives = new ArrayList<Agent>();
	}

	public void setTruePostives(){
		for (Agent atcDrug : this.atcDrugs) {
			if(this.ftcDrugs.contains(atcDrug)){
				truePositives.add(atcDrug);
			}
		}
	}

	public void setFalseNegatives() {
		for (Agent atcDrug : this.atcDrugs) {
			if(!this.ftcDrugs.contains(atcDrug)){
				falseNegatives.add(atcDrug);
			}
		}
	}

	public void setFalsePositives() {
		for (Agent ftcDrug : this.ftcDrugs) {
			if(!this.atcDrugs.contains(ftcDrug)){
				falsePositives.add(ftcDrug);
			}
		}
	}


}
