package analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import network.Attribute;
import network.Edge;
import network.IntegerAttributeFactory;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;

public class Jaccard {

	public static void main(String[] args) throws BrainException, IOException {
		Brain brain = new Brain();
		System.out.println("learn...");
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		//brain.learn("/home/samuel/Desktop/test.owl");
		//brain.learn("/home/samuel/Downloads/ns.owl");
		
		Network network = new Network();
		IntegerAttributeFactory simFactory = network.getNewIntegerAttributeFactory("similarity");
		StringAttributeFactory nameFactory = network.getNewStringAttributeFactory("name");
		network.setIdentifierNodes("name");
		network.setIdentifierEdges("similarity");

		//Get the drugbank compounds
		//List<String> all = brain.getSubClasses("FTC_C2", false);
		List<String> all = brain.getSubClasses("Thing", false);

		int iterations = 50;
		//int iterations = all.size();
		
		File file = new File("data/analysis/moa_similarities.csv");
		//File file = new File("/home/samuel/Desktop/test.csv");
		
		PrintWriter writer = new PrintWriter(file);

		for (int i = 0; i < iterations; i++) {
			if(i != 0){
				writer.append(",");
			}
			writer.append(all.get(i));
		}
		writer.append("\n");

		for (int i = 0; i < iterations; i++) {
			System.out.println(i + "/" + iterations);
			for (int j = 0; j < iterations; j++) {
				String class1 = all.get(i);
				String class2 = all.get(j);

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

				Node node1 = new Node();
				Attribute node1Label = nameFactory.getNewAttribute(class1);
				node1.addAttribute(node1Label);

				Node node2 = new Node();
				Attribute node2Label = nameFactory.getNewAttribute(class2);
				node2.addAttribute(node2Label);

				Edge edge = new Edge();
				Attribute edgeASim = simFactory.getNewAttribute((int) index);
				edge.addAttribute(edgeASim);

				Relation relation = new Relation(node1, edge, node2);
				network.addRelation(relation);
				network.saveAll("data/analysis/cytoscape", "demo");

				//System.out.println(class1 + "," + class2 + "," + index);
				//System.out.println("U: " + union + " - I:" + intersection + " - Index: " + index);
				//System.out.println(superClasses1 + " - " + superClasses2);
				if(j != 0){
					writer.append(",");
				}
				writer.append(Double.toString(index));
			}
			writer.append("\n");
		}

		brain.sleep();
		writer.close();
	}

}
