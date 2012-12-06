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
		ArrayList<Agent> agents = new ArrayList<Agent>();
		Agent agent1 = new Agent("DB0001");
		agents.add(agent1);
		
		Agent agent2 = new Agent("DB0002");
		agents.add(agent2);
		
		FtcClass ftcClass1 = new FtcClass("FTC1", "", "", new ArrayList<String>(), new ArrayList<String>(), agents, new ArrayList<String>());
		ftcClass1.save();
		
		FtcClass ftcClass2 = new FtcClass("FTC2", "", "", new ArrayList<String>(), new ArrayList<String>(), agents, new ArrayList<String>());
		ftcClass2.save();
	}

}
