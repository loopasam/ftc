package other;

import java.util.ArrayList;

import models.Agent;
import models.FtcClass;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ModelTest extends UnitTest{

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}

	@Test
	public void createFtcClass() {
		Agent agent = new Agent("DB0001");
		agent.save();
		
		ArrayList<String> subClasses = new ArrayList<String>();
		subClasses.add("subClass1");
		
		ArrayList<String> superClasses = new ArrayList<String>();
		superClasses.add("superClass1");
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		agents.add(agent);
		
		FtcClass ftcClass = new FtcClass("1", "ftc class test", "comment value", subClasses, superClasses, agents);
		
		ftcClass.save();
	}

}
