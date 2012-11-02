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
public class TargetRelation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1400811734386469446L;
	private int partnerId;
	private ArrayList<String> actions;
	private String knowAction;

	public TargetRelation() {
		this.setActions(new ArrayList<String>());
	}

	public int getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}
	public ArrayList<String> getActions() {
		return actions;
	}
	public void setActions(ArrayList<String> actions) {
		this.actions = actions;
	}
	public String getKnowAction() {
		return knowAction;
	}
	public void setKnowAction(String knowAction) {
		this.knowAction = knowAction;
	}    

}
