package utils;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;


public abstract class ReasonerRun {

	OWLOntologyManager manager;
	File file;
	OWLOntology ontology;
	OWLReasonerFactory reasonerFactory;
	OWLReasoner reasoner;

	public ReasonerRun(OWLReasonerFactory factory) throws OWLOntologyCreationException {
		this.manager = OWLManager.createOWLOntologyManager();
		this.file = new File("/home/samuel/git/ftc/data/ftc-kb-full.owl");
		System.out.println("starting loading...");
		this.ontology = manager.loadOntologyFromOntologyDocument(file);
		System.out.println("loading finished!");
		this.reasonerFactory = factory;
		this.reasoner = reasonerFactory.createReasoner(ontology);			
	}

	public void run(){
		System.out.println("Classification...");
		this.reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		System.out.println("Classifition done!");
	};

	public void stop(){
		System.out.println("reasoner stopped");
	}

}
