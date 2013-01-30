package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import play.Logger;
import play.db.jpa.Model;

@Entity
public class EvaluationMapping extends Model {

	//The actual mapping
	@Lob
	public String htmlDefinition;
	
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

	public void setDefinitionHtml() {
		
		StringBuffer output = new StringBuffer();
		output.append("(");
		boolean isFirst = true;
		
		for (String atcClass : this.atcClasses) {
			if(!isFirst){
				output.append("&nbsp;or&nbsp;");
			}else{
				isFirst = false;
			}
			output.append("<a href='http://www.whocc.no/atc_ddd_index/?code=" + atcClass + "'>" + atcClass + "</a>");
		}
		
		output.append(") = (<a href='/" + this.ftcClass + "'>" + this.ftcClass + "</a>" + ")");
		this.htmlDefinition = output.toString();
	}


}
