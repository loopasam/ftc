package build;

import models.FtcClass;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ModelTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}

	@Test
	public void populate(){
		new FtcClass("FTC_A0001", "Anti-whatever Agent").save();
		assertEquals(1, FtcClass.count());
	}

}
