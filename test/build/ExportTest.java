package build;
import static org.junit.Assert.assertEquals;

import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import play.test.*;
import uk.ac.ebi.brain.error.BrainException;
import models.*;

public class ExportTest extends UnitTest {

	@Test
	public void exportToOwlFull() throws BrainException, FileNotFoundException, IOException, ClassNotFoundException {
		Builder builder = new Builder();
		builder.exportFullStructureToOwl();
		File file = new File("data/ftc-kb-full.owl");
		assertTrue(file.exists());
	}
	
	
	

}
