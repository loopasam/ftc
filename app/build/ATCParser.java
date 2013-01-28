package build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.StorageException;

public class ATCParser extends Parser {

	private ATC atc;
	private String atcFilePath;

	public String getAtcFilePath() {
		return atcFilePath;
	}

	public void setAtcFilePath(String atcFilePath) {
		this.atcFilePath = atcFilePath;
	}

	public void setAtc(ATC atc) {
		this.atc = atc;
	}

	public ATC getAtc() {
		return atc;
	}

	public ATCParser(String pathIn, String pathOut) {
		super(pathOut);
		setAtcFilePath(pathIn);
		this.setAtc(new ATC());
	}

	/* (non-Javadoc)
	 * @see parser.Parser#start()
	 */
	@Override
	public void start() throws FileNotFoundException, IOException {
		FileInputStream fstream = null;
		fstream = new FileInputStream(this.getAtcFilePath());
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line =null;
		while ((line = br.readLine()) != null)   {

			Pattern patternCategory = Pattern.compile("^(\\w.*)\\s\\s+(\\w.*)");
			Pattern patternTherapeutic = Pattern.compile("^\\s+(\\w\\d\\d.*)\\s\\s+(\\w.*)");

			Matcher matcherCategory = patternCategory.matcher(line);
			Matcher matcherTherapeutic = patternTherapeutic.matcher(line);

			if (matcherCategory.find()){
				ATCTerm term = new ATCTerm();
				term.setCode(matcherCategory.group(1).replaceAll(" ", ""));
				term.setLabel(matcherCategory.group(2));
				term.setATherapeutic(false);
				this.getAtc().addTerm(term);
			}else if(matcherTherapeutic.find()){
				ATCTerm term = new ATCTerm();
				term.setCode(matcherTherapeutic.group(1).replaceAll(" ", ""));
				term.setLabel(matcherTherapeutic.group(2));
				term.setATherapeutic(true);
				this.getAtc().addTerm(term);
			}

		}

		br.close();

		for (ATCTerm term : this.getAtc().getTerms()) {
			Pattern patternParent = getRegexParent(term);
			if(patternParent != null){
				for (ATCTerm termToCheck : this.getAtc().getTerms()) {
					Matcher matcherParent = patternParent.matcher(termToCheck.getCode());
					if (matcherParent.find()){
						term.setParentCode(termToCheck.getCode());
					}
				}
			}
		}
	}

	private Pattern getRegexParent(ATCTerm term) {
		ArrayList<String> patterns = new ArrayList<String>();
		patterns.add("^(\\w\\d\\d\\w\\w)\\d\\d$");
		patterns.add("^(\\w\\d\\d\\w)\\w$");
		patterns.add("^(\\w\\d\\d)\\w$");
		patterns.add("^(\\w)\\d\\d$");

		for (String pattern : patterns) {
			Pattern patternTermStructure = Pattern.compile(pattern);
			Matcher matcherTerm = patternTermStructure.matcher(term.getCode());
			if (matcherTerm.find()){
				return Pattern.compile("^" + matcherTerm.group(1) + "$");
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.Parser#save()
	 */
	@Override
	public Object save() throws FileNotFoundException, IOException {	
		ObjectOutput out = null;
		out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
		out.writeObject(this.getAtc());
		out.close();
		return this.getAtc();
	}

	public void convertInOwl(String path) throws BrainException, FileNotFoundException, IOException, ClassNotFoundException {
		Brain brain = new Brain("http://www.ebi.ac.uk/Rebholz-srv/atc/", 
				"http://www.ebi.ac.uk/Rebholz-srv/atc/public/ontologies/atc.owl", 1);

		brain.addClass("DrugBankCompound");

		for (ATCTerm term : this.getAtc().getTerms()) {

			try {
				//Add the term as OWL class
				brain.addClass(term.getCode());
				brain.label(term.getCode(), term.getLabel());
			}catch(ExistingClassException e) {
				Logger.info(e.getMessage());
			}

			if(term.getAllDrugBankReferences().size() > 0){

				for (String dbid : term.getAllDrugBankReferences()) {

					if(!brain.knowsClass(dbid)){
						brain.addClass(dbid);
					}

					brain.subClassOf(term.getCode(), dbid);
					brain.subClassOf(dbid, "DrugBankCompound");
				}
			}
		}

		for (ATCTerm term : this.getAtc().getTerms()) {
			if(term.getParentCode() != null){
				brain.subClassOf(term.getCode(), term.getParentCode());
			}
		}

		brain.save(path);
	}

	public void addDrugBankInfo(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
		DrugBank drugBank = new DrugBank(path);
		//Check DB and add the term to the curated categories.
		for (Drug drug : drugBank.getDrugs()) {
			if(drug.getAtcCodes().size() > 0){
				for (String code : drug.getAtcCodes()) {
					ATCTerm term = this.getAtc().getTerm(code);
					if(term == null){
						Logger.info("Category doesn't exists in the ATC: " + code + ". " +
								"Drug "+drug.getId()+" has been mapped to this category.");
					}else{
						term.getDrugBankReferences().add(drug.getId());
					}
				}
			}
		}
	}


	/**
	 * @param string
	 * @return 
	 */
	//TODO to be removed if still commented out
	//	public ATCTerm getCategory(String category) {
	//		return this.getAtc().getTerm(category);
	//	}

}
