package utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import build.DrugBank;
import build.GeneOntology;
import build.GoAnnotation;
import build.Partner;

public class Teat {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		DrugBank drugBank = new DrugBank("data/tmp/drugbank-goa.ser");
		for (Partner partner : drugBank.getPartners()) {
			
			for (GoAnnotation goAnnotation : partner.getNonIEAAnnotationsNonCC()) {

				if(goAnnotation.getGoId().equals("GO:0007596")){

					System.out.println(partner.getId());

				}
			}
		}
		
	}

}
