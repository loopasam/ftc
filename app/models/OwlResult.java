package models;

import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * Class caching the result of an OWL query
 * @author samuel
 *
 */
@Entity
public class OwlResult extends Model {
	
	public String query;
	
	public List<String> subClasses;
	
	public List<String> equivalentClasses;
	
	

}
