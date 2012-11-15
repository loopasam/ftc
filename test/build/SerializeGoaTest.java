package build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import play.test.UnitTest;

public class SerializeGoaTest extends UnitTest {

	@Test
	public void addGoAnnotationTest() throws FileNotFoundException, IOException, ClassNotFoundException {
		Builder builder = new Builder();
		builder.addGoAnnotations();
		File file = new File("data/tmp/drugbank-goa.ser");
		assertTrue(file.exists());
	}

}
