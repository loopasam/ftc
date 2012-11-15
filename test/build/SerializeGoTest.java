package build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import play.test.UnitTest;

public class SerializeGoTest extends UnitTest {

	@Test
	public void serializeGOTest() throws FileNotFoundException, IOException, ClassNotFoundException {
		Builder builder = new Builder();
		builder.serializeGo();
		File file = new File("data/tmp/go.ser");
		assertTrue(file.exists());
	}

}
