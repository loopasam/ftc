package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;

@Entity
@Indexed
public class Agent extends Model {

	@Field
	public String label;

	@Field
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

	public Agent(String drugBankId) {
		this.drugBankId = drugBankId;
	}

	@ManyToMany(mappedBy="directAgents")
	public List<FtcClass> directFtcClasses;

}
