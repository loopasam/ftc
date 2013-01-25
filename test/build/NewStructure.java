package build;

import java.util.List;


import models.FtcClass;

import org.junit.*;

import controllers.Application;

import play.test.Fixtures;
import play.test.UnitTest;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;


//TODO delete this class - was useful to debug, not useful anymore
public class NewStructure extends UnitTest {

	@Test
	public void load() throws BrainException {
		
		Fixtures.deleteDatabase();
		
		FtcClass class1 = new FtcClass("ID1", "class1", "").save();
		FtcClass class2 = new FtcClass("ID2", "class2", "").save();
		FtcClass class3 = new FtcClass("ID3", "class3", "").save();
		FtcClass class4 = new FtcClass("ID4", "class4", "").save();
		
		class3.subClasses.add(class2);
		class3.save();
//		
		
		class1.subClasses.add(class2);
		class1.save();
		
		class2.superClasses.add(class3);
		class2.superClasses.add(class1);
		class2.subClasses.add(class4);
		class2.save();
				
		System.out.println(class2.subClasses);
		for (FtcClass subClass : class2.subClasses) {
			System.out.println(subClass.label);
		}
		System.out.println(class2.superClasses);
		for (FtcClass subClass : class2.superClasses) {
			System.out.println(subClass.label);
		}
	}

}
