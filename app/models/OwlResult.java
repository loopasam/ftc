package models;

import java.util.HashMap;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Type;

import play.db.jpa.Model;

/**
 * Class caching the result of an OWL query
 * @author samuel
 *
 */
@Entity
public class OwlResult extends Model {

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String query;
	
	public int numberOfTimes;
	
	public boolean tooManyResults;

	@ElementCollection
	public List<String> subClasses;
	

	public OwlResult(String query, boolean tooManyResults) {
		this.query = query;
		this.tooManyResults = tooManyResults;
		this.numberOfTimes = 1;
	}

}
