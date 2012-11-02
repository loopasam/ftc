/**
 * 
 */
package build;

import java.io.Serializable;

/**
 * @author Samuel Croset
 *
 */
public class Species implements Serializable {

	private static final long serialVersionUID = 1L;
	private String category;
	private String name;
	private int taxonId;

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTaxonId() {
		return taxonId;
	}
	public void setTaxonId(int taxonId) {
		this.taxonId = taxonId;
	}

}
