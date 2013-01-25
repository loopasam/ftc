package models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;

import org.hibernate.annotations.Type;

import com.google.gson.annotations.Expose;

import build.DatabaseFiller;

import play.Logger;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class FtcClass extends Model {

	@Expose
	public String label;

	@Expose
	public String ftcId;

	public int widthSvg;
	public int heightSvg;
	public boolean hasDrug;

	@Lob
	public String comment;

	@Lob
	public String svgGraph;

	public FtcClass(String ftcId, String label, String comment) {
		this.subClasses = new ArrayList<FtcClass>();
		this.superClasses = new ArrayList<FtcClass>();
		this.directAgents = new ArrayList<Agent>();
		this.indirectAgents = new ArrayList<Agent>();
		this.ftcId = ftcId;
		this.label = label;
		this.comment = comment;
	}

	@JoinTable(name = "FtcClass_DirectAgents")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> directAgents;

	@JoinTable(name = "FtcClass_IndirectAgents")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<Agent> indirectAgents;

	@JoinTable(name = "FtcClass_SuperClasses")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<FtcClass> superClasses;

	@JoinTable(name = "FtcClass_SubClasses")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<FtcClass> subClasses;


	//Hack to remove the viewBox in order to profit from the zoom/pan library
	public String svgMap() {
		String svgMap = this.svgGraph.replaceAll("viewBox=\".*\" xmlns=\"http://www.w3.org/2000/svg\"",
				"xmlns=\"http://www.w3.org/2000/svg\"");
		return svgMap;
	}

	public void addSubClasses(List<String> ftcSubClasses) {
		for (String subClassId : ftcSubClasses) {
			FtcClass ftcSubClass = FtcClass.find("byFtcId", subClassId).first();
			this.subClasses.add(ftcSubClass);
		}
		this.save();

	}

	public void addSuperClasses(List<String> ftcSuperClasses) {
		for (String superClassId : ftcSuperClasses) {
			FtcClass ftcSuperClass = FtcClass.find("byFtcId", superClassId).first();
			this.superClasses.add(ftcSuperClass);
		}
		this.save();
	}

	public void addDirectAgents(List<String> directAgentIds) {
		for (String directAgentId : directAgentIds) {
			Agent indirectAgent = Agent.find("byDrugBankId", directAgentId).first();
			this.directAgents.add(indirectAgent);
			if(this.hasDrug == false){
				this.hasDrug = true;
			}
		}
		this.save();
	}

	public void addIndirectAgents(List<String> indirectAgents) {
		for (String indirectAgentId : indirectAgents) {
			Agent indirectAgent = Agent.find("byDrugBankId", indirectAgentId).first();
			this.indirectAgents.add(indirectAgent);
			if(this.hasDrug == false){
				this.hasDrug = true;
			}
		}
		this.save();
	}

}
