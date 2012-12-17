package models;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;

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

	public OwlResult(String query, List<String> subClasses, List<String> equivalentClasses) {
		this.query = query;
		this.subClasses = subClasses;
		this.equivalentClasses = equivalentClasses;
	}

}
