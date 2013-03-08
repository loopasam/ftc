package utils;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class HermitReasonerRun extends ReasonerRun {

	public HermitReasonerRun() throws OWLOntologyCreationException {
		super(new Reasoner.ReasonerFactory());
	}

}
