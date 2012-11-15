package build;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;


public class GoContentTest extends UnitTest {


	@Test
	public void testGetParents() throws FileNotFoundException, IOException, ClassNotFoundException{
		GeneOntology go = new GeneOntology("data/tmp/go.ser");
		ArrayList<GoTerm> parents = go.getParentsOfTerm(go.getTerm("GO:0050817"));
		assertEquals(4, parents.size());
	}

	@Test
	public void testTerms() throws FileNotFoundException, IOException, ClassNotFoundException {
		GeneOntology go = new GeneOntology("data/tmp/go.ser");
		GoTerm term = go.getTerm("GO:0050435");
		assertEquals("beta-amyloid metabolic process", term.getName());
		assertEquals("biological_process", term.getNamespace());
		assertEquals("\"The chemical reactions and pathways involving beta-amyloid, a glycoprotein associated with Alzheimer's disease, and its precursor, amyloid precursor protein (APP).\" [GOC:ai]", term.getDefinition());
	}

	@Test
	public void testRelationsTerms() throws FileNotFoundException, IOException, ClassNotFoundException {
		GeneOntology go = new GeneOntology("data/tmp/go.ser");
		GoTerm term = go.getTerm("GO:0043267");
		assertEquals("negative regulation of potassium ion transport", term.getName());
		assertEquals(3, term.getRelations().size());
		assertEquals("negatively_regulates", term.getRelations().get(2).getType());
		assertEquals("GO:0006813", term.getRelations().get(2).getTarget());
	}

}
