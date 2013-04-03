package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Metrics extends Model {
	
	public Date date;
	
	public long numberOfDrugBankCompounds;
	
	public long numberOfFtcClasses;
	
	public int numberOfAxioms;

	public int numberOfProteins;

	//Set of compounds that are present in the FTC and in the ATC and that are appearing in the evaluation 
	public int numberOfUniquelyEvaluatedCompounds;

	//The number of compounds that are present both in the ATC and in the FTC. These compounds are the only ones evaluated.
	public int numberOfCompoundsInBothClassifications;
	

}
