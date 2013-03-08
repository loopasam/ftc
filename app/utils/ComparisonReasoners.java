package utils;

import java.util.concurrent.TimeUnit;

import jj.play.ns.com.jhlabs.image.WaterFilter;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.google.common.base.Stopwatch;

public class ComparisonReasoners {

	public static void main(String[] args) throws OWLOntologyCreationException {

		
		ReasonerRun elkRun = new ElkReasonerRun();
		Stopwatch elkWatch = new Stopwatch();
		elkWatch.start();
		elkRun.run();
		elkRun.stop();
		elkWatch.stop();
		System.out.println("time ELK: " + elkWatch.elapsed(TimeUnit.MILLISECONDS));
		
		ReasonerRun srRun = new SnowRocketReasonerRun();
		Stopwatch srWatch = new Stopwatch();
		srWatch.start();
		srRun.run();
		srRun.stop();
		srWatch.stop();
		System.out.println("time SR: " + srWatch.elapsed(TimeUnit.MILLISECONDS));
		
		ReasonerRun hermitRun = new HermitReasonerRun();
		hermitRun.run();
		hermitRun.stop();
	}

}
