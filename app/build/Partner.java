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
public class Partner implements Serializable {

	private static final long serialVersionUID = -5913113416467404250L;
	private String uniprotIdentifer;
	private int id;
	private String name;
	private ArrayList<GoAnnotation> annotations;
	private Species species;

	public void setSpecies(Species species) {
		this.species = species;
	}
	public Species getSpecies() {
		return species;
	}
	public String getUniprotIdentifer() {
		return uniprotIdentifer;
	}
	public void setUniprotIdentifer(String uniprotIdentifer) {
		this.uniprotIdentifer = uniprotIdentifer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAnnotations(ArrayList<GoAnnotation> annotations) {
		this.annotations = annotations;
	}
	public ArrayList<GoAnnotation> getAnnotations() {
		return annotations;
	}

	public ArrayList<GoAnnotation> getNonIEAAnnotations() {
		ArrayList<GoAnnotation> annotations = new ArrayList<GoAnnotation>();

		if(this.getAnnotations() != null){
			for (GoAnnotation goAnnotation : this.getAnnotations()) {
				if(!goAnnotation.getEvidence().equals("IEA")){
					annotations.add(goAnnotation);
				}
			}
		}

		return annotations;
	}

	public ArrayList<GoAnnotation> getNonIEAAnnotationsNonCC() {
		ArrayList<GoAnnotation> annotations = this.getNonIEAAnnotations();
		ArrayList<GoAnnotation> nonCCAnnotations = new ArrayList<GoAnnotation>();

		for (GoAnnotation goAnnotation : annotations) {
			if(!goAnnotation.getAspect().equals("cellular_component")){
				nonCCAnnotations.add(goAnnotation);
			}
		}
		return nonCCAnnotations;
	}    

}
