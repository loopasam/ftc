package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import com.google.gson.annotations.Expose;

import play.db.jpa.Model;

@Entity
public class Agent extends Model {

	@Expose
	public String label;

	@Expose
	public String drugBankId;

	@Lob
	public String description;

	@Lob
	public String indication;

	@Lob
	public String pharmacology;

	@Lob
	public String mechanism;

	@ElementCollection
	public List<String> categories;

	@ElementCollection
	public List<String> atcCodes;
	
	@JoinTable(name = "FtcClass_DirectFtcClasses")
	@ManyToMany(cascade=CascadeType.PERSIST)
	public List<FtcClass> directFtcClasses;

	public Agent(String drugBankId) {
		this.drugBankId = drugBankId;
		this.directFtcClasses = new ArrayList<FtcClass>();
	}

	public void addFtcClasses(List<String> superClassIds) {
		for (String superClassId : superClassIds) {
			FtcClass ftcSuperClass = FtcClass.find("byFtcId", superClassId).first();
//			if(ftcSuperClass != null){
				this.directFtcClasses.add(ftcSuperClass);
//				this.save();
//			}
		}
		this.save();
	}

}
