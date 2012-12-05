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
	}

}
