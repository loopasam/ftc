package utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ElkReasonerRun extends ReasonerRun {

	public ElkReasonerRun() throws OWLOntologyCreationException {
		super(new ElkReasonerFactory());
	}
	
	@Override
	public void stop() {
		this.reasoner.dispose();
	}

}
