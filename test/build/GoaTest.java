package build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import play.test.UnitTest;

public class GoaTest extends UnitTest {

	@Test
	public void checkAnnotationAfterConnection() throws FileNotFoundException, IOException, ClassNotFoundException{
		DrugBank drugBank = new DrugBank("data/drugbank-goa.ser");
		Partner partner = drugBank.getPartner(54);
		assertNotNull(partner.getAnnotations());
		assertTrue(partner.getAnnotations().size() > 0);
		GoAnnotation annot = partner.getAnnotations().get(0);
		assertEquals("GO:0001934", annot.getGoId());
		assertEquals("UniProtKB", annot.getDatabase());
		assertEquals("20090305", annot.getDate());
		assertEquals("IDA", annot.getEvidence());
		assertEquals("-", annot.getEvidenceProvider());
		assertEquals("-", annot.getQualifier());
		assertEquals("PMID:7559487", annot.getReference());
		assertEquals("BHF-UCL", annot.getSource());
		assertEquals("9606", annot.getTaxon());
	}


}
