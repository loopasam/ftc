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

public class ATCTerm implements Serializable {

	private static final long serialVersionUID = -4098465368689765632L;
	private String code;
	private String label;
	private String parentCode;
	private ArrayList<String> drugBankReferences;
	private boolean isATherapeutic;

	public void setATherapeutic(boolean isATherapeutic) {
		this.isATherapeutic = isATherapeutic;
	}

	public boolean isATherapeutic() {
		return isATherapeutic;
	}

	public ATCTerm() {
		this.setDrugBankReferences(new ArrayList<String>());
	}

	public void setDrugBankReferences(ArrayList<String> drugBankReferences) {
		this.drugBankReferences = drugBankReferences;
	}
	public ArrayList<String> getDrugBankReferences() {
		return drugBankReferences;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getParentCode() {
		return parentCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<String> getAllDrugBankReferences() {
		ArrayList<String> dbrefs = new ArrayList<String>();
		for (String dbRef : this.getDrugBankReferences()) {
			if(!dbrefs.contains(dbRef)){
				dbrefs.add(dbRef);
			}
		}

		return dbrefs;
	}


}
