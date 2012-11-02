package build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import play.test.UnitTest;

public class DrugBankTest extends UnitTest {

	@Test
	public void checkDrugDrugBank() throws FileNotFoundException, IOException, ClassNotFoundException {
		DrugBank drugBank = new DrugBank("data/drugbank.ser");
		Drug drug = drugBank.getDrug("DB00001");
		assertEquals("biotech", drug.getType());
		Drug drug1 = drugBank.getDrug("DB00203");
		assertEquals("small molecule", drug1.getType());
		assertEquals("Lepirudin", drug.getName());
		assertEquals(1, drug.getGroups().size());
		assertEquals("approved", drug.getGroups().get(0));
		assertEquals(1, drug.getTargetRelations().size());
		assertEquals(54, drug.getTargetRelations().get(0).getPartnerId());
		assertEquals("yes", drug.getTargetRelations().get(0).getKnowAction());
		assertEquals("inhibitor", drug.getTargetRelations().get(0).getActions().get(0));
		assertEquals("Sildenafil", drug1.getName());
		assertEquals(1, drug1.getGroups().size());
		assertEquals("approved", drug1.getGroups().get(0));
		assertEquals(3, drug1.getTargetRelations().size());
		assertEquals(6038, drug1.getTargetRelations().get(2).getPartnerId());
		assertEquals("no", drug1.getTargetRelations().get(2).getKnowAction());
		assertEquals("inhibitor", drug1.getTargetRelations().get(0).getActions().get(0));
	}
	@Test
	public void checkPartnerDrugBank() throws FileNotFoundException, IOException, ClassNotFoundException {
		DrugBank drugBank = new DrugBank("data/drugbank.ser");
		Partner partner = drugBank.getPartner(3188);
		assertEquals("IspD/ispF bifunctional enzyme [Includes: 2-C-methyl-D-erythritol 4- phosphate cytidylyltransferase", partner.getName());
		assertEquals("Q9PM68", partner.getUniprotIdentifer());
		ArrayList<Partner> partners = drugBank.getPartners("DB00224");
		assertEquals(1, partners.size());
		assertEquals("O90777", partners.get(0).getUniprotIdentifer());
	}

	@Test
	public void checkUnknownRelationDrugBank() throws FileNotFoundException, IOException, ClassNotFoundException{
		DrugBank drugBank = new DrugBank("data/drugbank.ser");
		Drug drug = drugBank.getDrug("DB00002");	
		assertEquals("Cetuximab", drug.getName());
		assertEquals(12, drug.getTargetRelations().size());
		TargetRelation relation = drug.getTargetRelations().get(2);
		assertEquals(1, relation.getActions().size());
		assertEquals("unknown", relation.getActions().get(0));
	}

	@Test
	public void checkSpeciesDrugBank() throws FileNotFoundException, IOException, ClassNotFoundException{
		DrugBank drugBank = new DrugBank("data/drugbank.ser");
		Partner partner = drugBank.getPartner(1);
		Species species = partner.getSpecies();
		assertEquals("bacterial", species.getCategory());
		assertEquals("Haemophilus influenzae", species.getName());
		assertEquals(71421, species.getTaxonId());

		Partner partner1 = drugBank.getPartner(200);
		Species species1 = partner1.getSpecies();
		assertEquals("human", species1.getCategory());
		assertEquals("Homo sapiens", species1.getName());
		assertEquals(9606, species1.getTaxonId());

		Partner partner2 = drugBank.getPartner(54);
		Species species2 = partner2.getSpecies();
		assertEquals("human", species2.getCategory());

	}



}
