package build;
import static org.junit.Assert.assertEquals;

import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import play.test.*;
import models.*;

public class BuilderTest extends UnitTest {

	@Test
	public void serializeDrugBank() throws FileNotFoundException, IOException {
		Builder builder = new Builder();
		builder.serializeDrugBank();
		File file = new File("data/drugbank.ser");
		assertTrue(file.exists());
	}





}
