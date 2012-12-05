package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class Agent extends Model {
	
	public String label;
	public String drugBankId;


	public Agent(String drugBankId) {
		this.drugBankId = drugBankId;
	}
	
	@ManyToMany(mappedBy="directAgents")
	public List<FtcClass> directFtcClasses;
	
}
