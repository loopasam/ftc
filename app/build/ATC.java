package build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATC implements Serializable {

	private static final long serialVersionUID = -7881747449067909230L;
	private ArrayList<ATCTerm> terms;

	public void setTerms(ArrayList<ATCTerm> terms) {
		this.terms = terms;
	}
	public ArrayList<ATCTerm> getTerms() {
		return terms;
	}

	public ATC() {
		this.setTerms(new ArrayList<ATCTerm>());
	}

	public ATC(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(path);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		ATC atc = (ATC) in.readObject();
		this.setTerms(atc.getTerms());
		in.close();
	}

	public void addTerm(ATCTerm term) {
		this.getTerms().add(term);
	}

	public ATCTerm getTerm(String termCode) {
		for (ATCTerm atcTerm : this.getTerms()) {
			if(atcTerm.getCode().equals(termCode)){
				return atcTerm;
			}
		}
		return null;
	}

}
