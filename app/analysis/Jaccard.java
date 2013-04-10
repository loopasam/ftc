package analysis;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;

public class Jaccard {

	public static void main(String[] args) throws BrainException {
		Brain brain = new Brain();
		brain.learn("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		
		//Get the drugbank compounds
		List<String> all = brain.getSubClasses("FTC_C2", false);

		for (int i = 0; i < all.size(); i++) {
			for (int j = i + 1; j < all.size(); j++) {
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

				double index = intersection/union;
				System.out.println(class1 + "," + class2 + "," + index);
				//System.out.println("U: " + union + " - I:" + intersection + " - Index: " + index);
				//System.out.println(superClasses1 + " - " + superClasses2);
			}

		}

		brain.sleep();
	}

}
