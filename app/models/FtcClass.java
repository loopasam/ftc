package models;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class FtcClass extends Model {

	public String label;
	public String ftcId;
	public int widthSvg;
	//	public List<String> superClasses;

	@ElementCollection
	public List<String> subClasses;

	public FtcClass(String ftcId, String label, List<String> subClasses, int width) {
		this.ftcId = ftcId;
		this.label = label;
		this.subClasses = subClasses;
		this.widthSvg = width;
		//		this.superClasses = superClasses;
	}


}
