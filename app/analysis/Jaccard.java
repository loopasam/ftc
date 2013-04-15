package analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import build.ATC;

import network.Attribute;
import network.Edge;
import network.IntegerAttributeFactory;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;

//TODO delete class
public class Jaccard {

	public static void main(String[] args) throws IOException, BrainException {
		Brain brain = new Brain();
		System.out.println("learning FTC...");
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		Brain atc = new Brain();
		System.out.println("Learning ATC...");
		atc.learn("/home/samuel/git/ftc/data/atc.owl");

		//Get the drugbank compounds
		List<String> all = brain.getSubClasses("FTC_C2", false);

		//int iterations = 300;
		int iterations = all.size();

		File file = new File("data/analysis/moa_similarities.csv");
		int noMoa = 0;
		PrintWriter writer = new PrintWriter(file);
		List<String> drugsWithMoa = new ArrayList<String>();

		for (int i = 0; i < iterations; i++) {
			String class1 = all.get(i);
			if(brain.getSuperClasses(class1, false).size() > 4){
				drugsWithMoa.add(class1);
				if(i != 0){
					writer.append(",");
				}
				writer.append(all.get(i));
			}else{
				System.out.println("Drug without Moa: " + class1);
				noMoa++;
			}
		}
		writer.append(",category\n");
		System.out.println("No MoAs total: " + noMoa + "/" + iterations);

		for (int i = 0; i < drugsWithMoa.size(); i++) {
			System.out.println(i + "/" + drugsWithMoa.size());
			String category1 = null;
			for (int j = 0; j < drugsWithMoa.size(); j++) {
				String class1 = drugsWithMoa.get(i);
				String class2 = drugsWithMoa.get(j);

				//Get the subClasses of the drugbank compound 1
				List<String> atcSubClasses1;
				try {
					atcSubClasses1 = atc.getSubClasses(class1, false);
					category1 = getCategory(atcSubClasses1);
				} catch (ClassExpressionException exception) {
					category1 = "NoCategory";
				}

				//System.out.println(atcSubClasses1);

				List<String> superClasses1 = brain.getSuperClasses(class1, false);
				List<String> superClasses2 = brain.getSuperClasses(class2, false);

				double intersection = 0;
				double sizeSet1 = 0;
				double sizeSet2 = superClasses2.size();
				for (String superClass1 : superClasses1) {
					if(superClasses2.contains(superClass1)){
						intersection++;
						sizeSet2--;
					}else{
						sizeSet1++;
					}
				}
				double union = intersection + sizeSet2 + sizeSet1;

				double index = intersection/union*100;

				if(j != 0){
					writer.append(",");
				}
				writer.append(Double.toString(index));

			}
			writer.append("," + category1 + "\n");
		}

		brain.sleep();
		atc.sleep();
		writer.close();
	}

	private static String getCategory(List<String> atcSubClasses) {
		if(atcSubClasses.size() > 1){
			return "Multiple";
		}
		String category = atcSubClasses.get(0);
		return category.substring(0, 1);
	}

}
