package utils;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import au.csiro.snorocket.owlapi3.SnorocketReasonerFactory;

public class SnowRocketReasonerRun extends ReasonerRun {

	public SnowRocketReasonerRun() throws OWLOntologyCreationException {
		super(new SnorocketReasonerFactory());
	}
	
	

}
