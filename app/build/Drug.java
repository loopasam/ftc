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
	private ArrayList<String> groups;
	private ArrayList<TargetRelation> targetRelations;
	private String type;
	private ArrayList<String> atcCodes;


	public Drug() {
		this.setGroups(new ArrayList<String>());
		this.setTargetRelations(new ArrayList<TargetRelation>());
		this.setAtcCodes(new ArrayList<String>());
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

}
