package models;

import java.util.HashMap;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;

import play.db.jpa.Model;

/**
 * Class caching the result of an OWL query
 * @author samuel
 *
 */
@Entity
public class OwlResult extends Model {

	@Lob
	public String query;

	@ElementCollection
	public List<String> subClasses;
	
	@ElementCollection
	public List<String> equivalentClasses;
	
	@MapKeyColumn
	@Lob
	public HashMap<String, String> labelMap;
	
	@MapKeyColumn
	@Lob
	public HashMap<String, String> typeMap;


	public OwlResult(String query, List<String> subClasses, List<String> equivalentClasses, 
			HashMap<String, String> labelMap, HashMap<String, String> typeMap) {
		this.query = query;
		this.subClasses = subClasses;
		this.equivalentClasses = equivalentClasses;
		this.labelMap = labelMap;
		this.typeMap = typeMap;
	}

}
