package models;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.Max;

import org.hibernate.annotations.Type;

import build.DatabaseFiller;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class FtcClass extends Model {

	public String label;
	public String ftcId;
	public int widthSvg;
	public int heightSvg;

	@Lob
	public String comment;

	@Lob
	public String svgGraph;


	@ElementCollection
	public List<String> superClasses;

	@ElementCollection
	public List<String> subClasses;
	
	@ElementCollection
	public List<String> directAgents;
	
	@ElementCollection
	public List<String> indirectAgents;

	public FtcClass(String ftcId, String label, String comment, List<String> subClasses, List<String> superClasses, List<String> directAgents) {
		this.ftcId = ftcId;
		this.label = label;
		this.subClasses = subClasses;
		this.superClasses = superClasses;
		this.comment = comment;
		this.directAgents = directAgents;
	}

	//Hack to remove the viewBox in order to profit from the zoom/pan library
	public String svgMap() {
		String svgMap = this.svgGraph.replaceAll("viewBox=\".*\" xmlns=\"http://www.w3.org/2000/svg\"",
				"xmlns=\"http://www.w3.org/2000/svg\"");
		return svgMap;
	}

}
