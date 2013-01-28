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

//TODO remove the commented code - clean up
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

//	public ArrayList<ATCTerm> getFourLettersTerms() {
//
//		ArrayList<ATCTerm> fourLettersTerms = new ArrayList<ATCTerm>();
//
//		for (ATCTerm term : this.getTerms()) {
//			Pattern patternCompound = Pattern.compile("^(\\w\\d\\d\\w\\w)$");
//			Matcher matcherTerm = patternCompound.matcher(term.getCode());
//			if (matcherTerm.find()){
//				fourLettersTerms.add(term);
//			}
//		}
//		return fourLettersTerms;
//
//	}

//	public ArrayList<ATCTerm> getThreeLettersTerms() {
//
//		ArrayList<ATCTerm> threeLettersTerms = new ArrayList<ATCTerm>();
//
//		for (ATCTerm term : this.getTerms()) {
//			Pattern patternCompound = Pattern.compile("^(\\w\\d\\d\\w)$");
//			Matcher matcherTerm = patternCompound.matcher(term.getCode());
//			if (matcherTerm.find()){
//				threeLettersTerms.add(term);
//			}
//		}
//		return threeLettersTerms;
//
//	}

//	public ArrayList<ATCTerm> getTwoLettersTerms() {
//
//		ArrayList<ATCTerm> twoLettersTerms = new ArrayList<ATCTerm>();
//
//		for (ATCTerm term : this.getTerms()) {
//			Pattern patternCompound = Pattern.compile("^(\\w\\d\\d)$");
//			Matcher matcherTerm = patternCompound.matcher(term.getCode());
//			if (matcherTerm.find()){
//				twoLettersTerms.add(term);
//			}
//		}
//		return twoLettersTerms;
//
//	}

//	public ArrayList<ATCTerm> getTherapeutics() {
//
//		ArrayList<ATCTerm> therapeutics = new ArrayList<ATCTerm>();
//		for (ATCTerm term : this.getTerms()) {
//			if(term.isATherapeutic()){
//				therapeutics.add(term);
//			}
//		}
//		return therapeutics;
//	}

//	public String getTherapeuticIndications(String therapeutic) {
//		String indications = "";
//		for (ATCTerm term : this.getTerms()) {
//			ArrayList<String> refs = term.getDrugBankReferences();
//			for (String ref : refs) {
//				if(therapeutic.equals(ref)){
//					String parent = term.getParentCode();
//					indications += therapeutic + ": Known in the ATC as " + term.getCode() + " (" + term.getLabel() + "). Parent in the ATC: " + parent + " = " + getTerm(parent).getLabel() +"\n";
//				}
//			}
//		}
//		return indications;
//	}


//	public ArrayList<String> getParentCodesForTherapeutic(String therapeutic) {
//		ArrayList<String> parentCodes = new ArrayList<String>();
//		for (ATCTerm term : this.getTerms()) {
//			ArrayList<String> refs = term.getDrugBankReferences();
//			for (String ref : refs) {
//				if(therapeutic.equals(ref)){
//					String parent = term.getParentCode();
//					parentCodes.add(parent);
//				}
//			}
//		}
//		return parentCodes;
//	}


}
