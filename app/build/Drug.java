/**
 * 
 */
package build;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class Drug implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1837611788051168494L;
	private String id;
	private String name;
	private String smiles;
	private String description;
	private ArrayList<String> groups;
	private ArrayList<TargetRelation> targetRelations;
	private String type;
	private String indication;
	private String pharmacology;
	private String mechanism;
	private ArrayList<String> categories;
	private ArrayList<String> atcCodes;


	public Drug() {
		this.setGroups(new ArrayList<String>());
		this.setTargetRelations(new ArrayList<TargetRelation>());
		this.setAtcCodes(new ArrayList<String>());
		this.setCategories(new ArrayList<String>());
	}

	public void setTargetRelations(ArrayList<TargetRelation> targetRelations) {
		this.targetRelations = targetRelations;
	}
	public void setAtcCodes(ArrayList<String> atcCodes) {
		this.atcCodes = atcCodes;
	}

	public ArrayList<String> getAtcCodes() {
		return atcCodes;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public ArrayList<TargetRelation> getTargetRelations() {
		return targetRelations;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}
	public ArrayList<String> getGroups() {
		return groups;
	}

	public boolean isExperimental() {
		for (String group : this.getGroups()) {
			if(group.equals("experimental")){
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIndication() {
		return indication;
	}

	public void setIndication(String indication) {
		this.indication = indication;
	}

	public String getPharmacology() {
		return pharmacology;
	}

	public void setPharmacology(String pharmacology) {
		this.pharmacology = pharmacology;
	}

	public String getMechanism() {
		return mechanism;
	}

	public void setMechanism(String mechanism) {
		this.mechanism = mechanism;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

	public String getSmiles() {
		return smiles;
	}

	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}

}
