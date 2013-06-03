package analysis;

import java.util.List;

import network.Attribute;
import network.Edge;
import network.IntegerAttributeFactory;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.NewOntologyException;

public class AlzheimerCase {

	public static void main(String[] args) throws Exception {
		System.out.println("learning...");
		Brain brain = new Brain();
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		System.out.println("done!");

		List<String> drugs = brain.getSubClasses("FTC_P0001540", true);
		Network network = new Network();
		StringAttributeFactory nameFactory = network.getNewStringAttributeFactory("name");
		IntegerAttributeFactory similarityFactory = network.getNewIntegerAttributeFactory("similarity");
		network.setIdentifierNodes("name");
		network.setIdentifierEdges("similarity");


		for (int i = 0; i < drugs.size(); i++) {

			String drug1 = drugs.get(i);
			Node drug1node = new Node();
			Attribute drug1name = nameFactory.getNewAttribute(brain.getLabel(drug1));
			drug1node.addAttribute(drug1name);

			for (int j = i + 1; j < drugs.size(); j++) {

				String drug2 = drugs.get(j);
				Node drug2node = new Node();
				Attribute drug2name = nameFactory.getNewAttribute(brain.getLabel(drug2));
				drug2node.addAttribute(drug2name);

				int sim = Math.round(brain.getJaccardSimilarityIndex(drug1, drug2) * 100);				
				Edge simEdge = new Edge();
				Attribute edgeSimValue = similarityFactory.getNewAttribute(sim);
				simEdge.addAttribute(edgeSimValue);
				Relation relationA = new Relation(drug1node, simEdge, drug2node);

				if(sim > 0){
					network.addRelation(relationA);
				}

				System.out.println(drug1 + " - " + drug2);
			}
		}
		network.saveAll("/home/samuel/Desktop", "demo");
		brain.sleep();
	}

}
