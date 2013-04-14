package analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesParser;

import uk.ac.ebi.brain.core.Brain;

import build.Drug;
import build.DrugBank;

public class TanimotoCalculator {

	public static void main(String[] args) throws Exception {
		SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		Brain brain = new Brain();
		System.out.println("learning FTC...");
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		System.out.println("learnt.");
		DrugBank db = new DrugBank("data/tmp/drugbank.ser");

		List<String> drugs = brain.getSubClasses("FTC_C2", false);

		List<String> drugBankIds = new ArrayList<String>();

		for (String drugId : drugs) {
			Drug drug = db.getDrug(drugId);
			if(drug.getSmiles() != null){
				drugBankIds.add(drugId);
			}
		}

		for (String id1 : drugBankIds) {
			for (String id2 : drugBankIds) {

				String smiles1 = db.getDrug(id1).getSmiles();
				String smiles2 = db.getDrug(id2).getSmiles();
				IMolecule mol1 = smilesParser.parseSmiles(smiles1);
				IMolecule mol2 = smilesParser.parseSmiles(smiles2);
				HybridizationFingerprinter fingerprinter = new HybridizationFingerprinter();
				BitSet bitset1 = fingerprinter.getFingerprint(mol1);
				BitSet bitset2 = fingerprinter.getFingerprint(mol2);
				float tanimoto = Tanimoto.calculate(bitset1, bitset2);
				System.out.println(tanimoto);
			}
		}

	}

}
