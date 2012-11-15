package build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import play.test.UnitTest;

public class SerializeDrugBankTest extends UnitTest {

	@Test
	public void serializeDrugBankTest() throws FileNotFoundException, IOException {
		Builder builder = new Builder();
		builder.serializeDrugBank();
		File file = new File("data/tmp/drugbank.ser");
		assertTrue(file.exists());
	}

}
