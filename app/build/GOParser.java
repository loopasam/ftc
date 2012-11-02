/**
 * 
 */
package build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;

/**
 * @author Samuel Croset
 *
 */
public class GOParser extends Parser {

	private GeneOntology go;

	final static String GO_DOWNLOAD_URL = "http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology_ext.obo";

	public void setGo(GeneOntology go) {
		this.go = go;
	}

	public GeneOntology getGo() {
		return go;
	}

	/**
	 * @param pathIn
	 * @param pathOut
	 */
	public GOParser(String pathOut) {
		super(pathOut);
		this.setGo(new GeneOntology());
	}

	/* (non-Javadoc)
	 * @see parser.Parser#parse()
	 */
	@Override
	public void start() throws IOException {

		Logger.info("Downloading the obo file...");
		URL website = new URL(GO_DOWNLOAD_URL);
		File tempOboFile = new File("data/tmp/gene_ontology_ext.obo");
		org.apache.commons.io.FileUtils.copyURLToFile(website, tempOboFile);
		Logger.info("Parsing the GO...");

		FileInputStream fstream = null;
		fstream = new FileInputStream(tempOboFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line =null;
		while ((line = br.readLine()) != null)   {
			if(line.equals("[Term]")){
				String id = null;
				String name= null;
				String namespace = null;
				String def = null;
				ArrayList<GoRelation> relations = new ArrayList<GoRelation>();
				String isObsolete = "false";


				while(line != null && line.length() > 0){
					if(line.startsWith("id:")){
						id = getStringFromPattern("id: (.*)", line);
					}

					if(line.startsWith("name:")){
						name = getStringFromPattern("name: (.*)", line);
					}

					if(line.startsWith("namespace:")){
						namespace = getStringFromPattern("namespace: (.*)", line);
					}

					if(line.startsWith("def:")){
						def = getStringFromPattern("def: (.*)", line);
					}

					if(line.startsWith("is_a:")){
						String target = getStringFromPattern("is_a: (.*)\\s!\\s.*", line);
						GoRelation goRel = new GoRelation("is_a", target);
						relations.add(goRel);
					}

					if(line.startsWith("relationship:")){
						ArrayList<String> relation = getArrayFromPattern("relationship: (.*) (.*) ! .*", line);
						GoRelation newRel = new GoRelation(relation.get(0), relation.get(1));
						relations.add(newRel);
					}

					if(line.startsWith("is_obsolete")){
						isObsolete = getStringFromPattern("is_obsolete: (.*)", line);
					}
					line = br.readLine();
				}

				if(!isObsolete.equals("true")){
					GoTerm newTerm = new GoTerm();
					newTerm.setId(id);
					newTerm.setName(name);
					newTerm.setNamespace(namespace);
					newTerm.setRelations(relations);
					newTerm.setDefinition(def);
					this.getGo().getTerms().add(newTerm);
				}
			}
		}
		br.close();
	}

	/**
	 * @param string
	 * @param line
	 * @return
	 */
	private ArrayList<String> getArrayFromPattern(String patternString, String line) {

		ArrayList<String> array = new ArrayList<String>();
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()){
			array.add(matcher.group(1));
			array.add(matcher.group(2));
		}

		return array;
	}

	/**
	 * @param string
	 * @param line
	 * @return
	 */
	private String getStringFromPattern(String patternString, String line) {
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()){
			return matcher.group(1);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.Parser#save()
	 */
	@Override
	public GeneOntology save() throws FileNotFoundException, IOException {
		Logger.info("Saving the GO serialized...");
		ObjectOutput out = null;
		out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
		out.writeObject(this.getGo());
		out.close();
		return this.getGo();
	}

	//	public void normalize() {
	//		//Manual curation on a few relations that are absent from GO but should nonetheless be there.
	//		//The new relations are the following:
	//
	//		//(1) positive regulation of salivary gland formation by mesenchymal-epithelial signaling is_a positive regulation of biological process
	//		GoTerm term1 = this.getGo().getTerm("GO:0060639");
	//		GoRelation relation1 = new GoRelation("is_a", "GO:0048518");
	//		term1.getRelations().add(relation1);
	//
	//		//(2) positive regulation of protein processing is_a positive regulation of biological process
	//		GoTerm term2 = this.getGo().getTerm("GO:0010954");
	//		GoRelation relation2 = new GoRelation("is_a", "GO:0048518");
	//		term2.getRelations().add(relation2);
	//
	//		//(3) negative regulation of protein processing	is_a negative regulation of biological process
	//		GoTerm term3 = this.getGo().getTerm("GO:0010955");
	//		GoRelation relation3 = new GoRelation("is_a", "GO:0048519");
	//		term3.getRelations().add(relation3);
	//
	//	}

}
