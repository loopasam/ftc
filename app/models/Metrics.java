package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Metrics extends Model {
	
	public Date date;
	
	public long numberOfDrugBankCompounds;
	
	public long numberOfFtcClasses;
	
	public int numberOfAxioms;
	

}
