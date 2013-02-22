package controllers;

import play.*;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;
import play.libs.F.Promise;
import play.mvc.*;
import play.mvc.Http.Response;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingClassException;
import uk.ac.ebi.brain.error.NonExistingEntityException;
import utils.OWLClassResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jobs.FullBuildJob;
import jobs.OwlQueryJob;


import build.DatabaseFiller;

import models.*;

public class Application extends Controller {

	//Static brain object there to hold the ontology in memory
	public static Brain brain;
	public static Gson gson;
	final public static int PAGINATION = 20;

	public static void index() {
		render();
	}

	public static void data() {
		render();
	}

	public static void classVisu(String classId){

		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		if(ftcClass == null){
			error(404, "Named class '" + classId + "' does not exist");
		}

		//Compute the ratio
		//Arbitrary value, seems to be the good ratio in terms of UX
		int ratio = ftcClass.widthSvg*100/600;
		//If the image is bigger than minimal size, then it will be scaled down automatically by the browser
		//Larger maps dcan be explored with the map functionality
		if(ratio > 100){
			ratio = 100;
		}
		String ratioSvg = ratio + "%";

		//Get a chunck of the direct agents
		List<Agent> indirectAgents = new ArrayList<Agent>();
		for (Agent indirectAgent : range(ftcClass.indirectAgents, 0, PAGINATION)) {
			indirectAgents.add(indirectAgent);
		}

		//Get a chunck of the direct agents
		List<Agent> directAgents = new ArrayList<Agent>();
		for (Agent directAgent : range(ftcClass.directAgents, 0, PAGINATION)) {
			directAgents.add(directAgent);
		}

		//Get a chunck of the superClasses
		List<FtcClass> superClasses = new ArrayList<FtcClass>();
		for (FtcClass superClass : range(ftcClass.superClasses, 0, PAGINATION)) {
			superClasses.add(superClass);
		}

		//Get a chunck of the subClasses
		List<FtcClass> subClasses = new ArrayList<FtcClass>();
		for (FtcClass subClass : range(ftcClass.subClasses, 0, PAGINATION)) {
			subClasses.add(subClass);
		}

		render(ftcClass, ratioSvg, subClasses, superClasses, directAgents, indirectAgents);
	}


	private static <E> List<E> range(List<E> array, int start, int end) {
		List<E> rangedArray = new ArrayList<E>();
		if(start < 0 || array.size() < 1){
			return rangedArray;
		}
		for (int i = start; i < end; i++) {
			if(i >= array.size()){
				return rangedArray;
			}
			rangedArray.add(array.get(i));			
		}
		return rangedArray;
	}


	public static void moreIndirectAgents(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<Agent> indirectAgents = new ArrayList<Agent>();
		//Get the indirect agents object
		for (Agent indirectAgent : range(ftcClass.indirectAgents, currentNumber, currentNumber + PAGINATION)) {
			indirectAgents.add(indirectAgent);
		}
		String json = gson.toJson(indirectAgents);
		renderJSON(json);
	}

	public static void moreDirectAgents(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<Agent> directAgents = new ArrayList<Agent>();
		//Get the indirect agents object
		for (Agent directAgent : range(ftcClass.directAgents, currentNumber, currentNumber + PAGINATION)) {
			directAgents.add(directAgent);
		}
		String json = gson.toJson(directAgents);
		renderJSON(json);
	}

	public static void moreSuperclasses(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<FtcClass> superClasses = new ArrayList<FtcClass>();
		//Get the super classes objects
		for (FtcClass superClass : range(ftcClass.superClasses, currentNumber, currentNumber + PAGINATION)) {
			superClasses.add(superClass);
		}
		String json = gson.toJson(superClasses);
		renderJSON(json);
	}

	public static void moreSubclasses(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<FtcClass> subClasses = new ArrayList<FtcClass>();
		//Get the subclasses objects
		for (FtcClass subClass : range(ftcClass.subClasses, currentNumber, currentNumber + PAGINATION)) {
			subClasses.add(subClass);
		}
		String json = gson.toJson(subClasses);
		renderJSON(json);
	}

	public static void map(String classId) {
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		render(ftcClass);
	}

	public static void agent(String drugbankId){
		Agent agent = Agent.find("byDrugBankId", drugbankId).first();
		if(agent == null){
			error(404, "There is not agent called '" + drugbankId + "' in the FTC.");
		}
		render(agent);
	}

	public static void postSearch(@Required String query) {
		if(validation.hasErrors()){
			redirect("/search/");
		}
		search(query);
	}

	public static void search(String query) {
		List<Agent> agents = new ArrayList<Agent>();
		List<FtcClass> ftcClasses = new ArrayList<FtcClass>();

		List<Agent> agentsFromId = Agent.find("byDrugBankIdIlike", "%" + query + "%").fetch(PAGINATION);
		List<Agent> agentsFromLabel = Agent.find("byLabelIlike", "%" + query + "%").fetch(PAGINATION);
		agents.addAll(agentsFromLabel);
		agents.addAll(agentsFromId);

		List<FtcClass> ftcClassesFromId = FtcClass.find("byFtcIdIlike", "%" + query + "%").fetch(PAGINATION);
		List<FtcClass> ftcClassesFromLabel = FtcClass.find("byLabelIlike", "%" + query + "%").fetch(PAGINATION);
		ftcClasses.addAll(ftcClassesFromId);
		ftcClasses.addAll(ftcClassesFromLabel);

		boolean noResults = false;
		if(agents.size() == 0 && ftcClasses.size() == 0){
			noResults = true;
		}

		render(query, agents, ftcClasses, noResults);
	}


	//Starting page for semantic query
	public static void initQuery(){
		render();
	}

	//Performs the query
	public static void query(String query) throws NonExistingEntityException{

		try {
			//Checks whether the query is parsable
			brain.parseLabelClassExpression(query);
		} catch (ClassExpressionException e) {
			//If not display an error
			params.flash();
			String errorMessage = e.getMessage()
					.replaceAll("org.semanticweb.owlapi.expression.ParserException: ", "")
					.replaceAll("not\n", "")
					.replaceAll("inverse\n", "")
					.replaceAll("Data property name\n", "")
					.replaceAll("\n", "<br />")
					.replaceAll("^Encountered ", "Encountered <span class='parse_error'>")
					.replaceAll(" at line ", "</span> at line ");

			flash.error(errorMessage);

			initQuery();
		}

		//The query is parsable

		//Check if the query is already cached in the database
		OwlResult result = OwlResult.find("byQuery", query).first();

		if(result == null){
			//The query is not cached and is then going to be executed
			Promise<OwlResult> promise = new OwlQueryJob(query).now();
			Logger.info("Awaits for the results...");
			result = await(promise);
		}else{
			result.numberOfTimes++;
			result.save();
		}

		if(result.tooManyResults == true){
			Logger.info("Too many results...");
			params.flash();
			flash.error("There are way too many results (> 1500)");
			renderTemplate("Application/initQuery.html");
		}

		ArrayList<OWLClassResult> owlClassResults = new ArrayList<OWLClassResult>();
		for (String classResultId : range(result.subClasses, 0, PAGINATION)) {
			OWLClassResult classResult = new OWLClassResult();
			classResult.owlId = classResultId;
			classResult.label = brain.getLabel(classResultId);
			classResult.type = getTypeForResult(classResultId);
			owlClassResults.add(classResult);
		}

		int totalNumber = result.subClasses.size();
		Logger.info("Ready to render");
		render(query, owlClassResults, totalNumber);
	}

	public static void queries() {
		List<OwlResult> queries= OwlResult.find("order by numberOfTimes desc").fetch(50);
		render(queries);
	}

	private static String getTypeForResult(String classResultId) throws NonExistingClassException {
		String iri = Application.brain.getOWLClass(classResultId).getIRI().toString();
		String type;
		if(iri.contains("http://purl.uniprot.org/uniprot/")){
			type = "protein";
		}else if(iri.contains("http://www.drugbank.ca/drugs/")){
			type = "drugbank";
		}else if(iri.contains("http://purl.obolibrary.org/obo/")){
			type = "go";
		}else{
			type = "ftc";
		}
		return type;
	}

	public static void moreResults(String query, int currentNumber) throws NonExistingEntityException{
		OwlResult result = OwlResult.find("byQuery", query).first();
		ArrayList<OWLClassResult> owlClassResults = new ArrayList<OWLClassResult>();

		for (String classResultId : range(result.subClasses, currentNumber, currentNumber + PAGINATION)) {
			OWLClassResult classResult = new OWLClassResult();
			classResult.owlId = classResultId;
			classResult.label = brain.getLabel(classResultId);
			classResult.type = getTypeForResult(classResultId);
			owlClassResults.add(classResult);
		}

		renderJSON(owlClassResults);
	}


	//Redirection of the query in order for it to be displayed in the
	//address bar
	public static void owlQuery(String query) throws NonExistingEntityException{
		query(query);
	}

	public static void evaluations() {

		Metrics metrics = (Metrics) Metrics.findAll().get(0);

		List<EvaluationMapping> mappings = EvaluationMapping.findAll();
		float fp = 0;
		float fn = 0;
		float tp = 0;
		for (EvaluationMapping mapping : mappings) {
			fp += mapping.falsePositives.size();
			fn += mapping.falseNegatives.size();
			tp += mapping.truePositives.size();
		}

		float recall = tp/(tp + fn);
		float precision = tp/(tp + fp);
		renderTemplate("Application/evaluationList.html", mappings, fp, fn, tp, recall, precision, metrics);
	}

	public static void evaluation(String classId){
		EvaluationMapping mapping = EvaluationMapping.find("byFtcClass", classId).first();
		render(mapping);
	}

}