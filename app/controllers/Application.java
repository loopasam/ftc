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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import jobs.FullBuildJob;
import jobs.OwlQueryJob;


import build.DatabaseFiller;

import models.*;

public class Application extends Controller {

	//Static brain object there to hold the ontology in memory
	public static Brain brain;
	final public static int PAGINATION = 20; 

	public static void index() {
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

		List<FtcClass> subClasses = new ArrayList<FtcClass>();
		//Get the subclasses object
		for (String subClassId : range(ftcClass.subClasses, 0, PAGINATION)) {
			FtcClass subClass = FtcClass.find("byFtcId", subClassId).first();
			subClasses.add(subClass);
		}

		List<FtcClass> superClasses = new ArrayList<FtcClass>();
		//Get the super classes object
		for (String superClassId : range(ftcClass.superClasses, 0, PAGINATION)) {
			FtcClass superClass = FtcClass.find("byFtcId", superClassId).first();
			superClasses.add(superClass);
		}

		List<Agent> indirectAgents = new ArrayList<Agent>();
		//Get the indirect agents object
		for (String indirectAgentId : range(ftcClass.indirectAgents, 0, PAGINATION)) {
			Agent indirectAgent = Agent.find("byDrugBankId", indirectAgentId).first();
			indirectAgents.add(indirectAgent);
		}

		List<Agent> directAgents = new ArrayList<Agent>();
		//Get the direct agents object
		for (String directAgentId : range(ftcClass.directAgents, 0, PAGINATION)) {
			Agent directAgent = Agent.find("byDrugBankId", directAgentId).first();
			directAgents.add(directAgent);
		}

		render(ftcClass, ratioSvg, subClasses, superClasses, indirectAgents, directAgents);
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
		for (String indirectAgentId : range(ftcClass.indirectAgents, currentNumber, currentNumber + PAGINATION)) {
			Agent indirectAgent = Agent.find("byDrugBankId", indirectAgentId).first();
			indirectAgents.add(indirectAgent);
		}
		renderJSON(indirectAgents);
	}
	
	public static void moreDirectAgents(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<Agent> directAgents = new ArrayList<Agent>();
		//Get the indirect agents object
		for (String directAgentId : range(ftcClass.directAgents, currentNumber, currentNumber + PAGINATION)) {
			Agent directAgent = Agent.find("byDrugBankId", directAgentId).first();
			directAgents.add(directAgent);
		}
		renderJSON(directAgents);
	}
	
	public static void moreSuperclasses(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<FtcClass> superClasses = new ArrayList<FtcClass>();
		//Get the super classes objects
		for (String superClassId : range(ftcClass.superClasses, currentNumber, currentNumber + PAGINATION)) {
			FtcClass superClass = FtcClass.find("byFtcId", superClassId).first();
			superClasses.add(superClass);
		}
		
		renderJSON(superClasses);
	}

	public static void moreSubclasses(String ftcClassId, int currentNumber){
		FtcClass ftcClass = FtcClass.find("byFtcId", ftcClassId).first();
		List<FtcClass> subClasses = new ArrayList<FtcClass>();
		//Get the subclasses objects
		for (String subClassId : range(ftcClass.subClasses, currentNumber, currentNumber + PAGINATION)) {
			FtcClass subClass = FtcClass.find("byFtcId", subClassId).first();
			subClasses.add(subClass);
		}
		
		renderJSON(subClasses);
	}

	public static void map(String classId) {
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		render(ftcClass);
	}

	public static void agent(String drugbankId){

		Agent agent = Agent.find("byDrugBankId", drugbankId).first();

		List<FtcClass> directFtcClasses = new ArrayList<FtcClass>();
		//Get the direct FtcClass object
		for (String directFtcClassId : agent.directFtcClasses) {
			FtcClass directFtcClass = FtcClass.find("byFtcId", directFtcClassId).first();
			directFtcClasses.add(directFtcClass);
		}

		render(agent, directFtcClasses);
	}

	public static void postSearch(@Required String query) {
		if(validation.hasErrors()){
			//TODO put in the flash
			redirect("/search/");
		}
		//		search(query);
	}

	//TODO finir le CSS and HTML
	//	public static void search(String query) {
	//		List<Agent> agents = null;
	//		List<Agent> ftcClasses = null;
	//
	//		if(isValidQuery(query)){
	//
	//			agents = Search.search("drugBankId:" + query, Agent.class).fetch();
	//			if(agents.size() == 0){
	//				agents = Search.search("label:" + query + "~", Agent.class).fetch();
	//				if(agents.size() == 0){
	//					agents = Search.search("label:" + query + "*~", Agent.class).fetch();
	//				}
	//			}
	//
	//			ftcClasses = Search.search("ftcId:" + query, FtcClass.class).fetch();
	//			if(ftcClasses.size() == 0){
	//				String appendFTC = "FTC_" + query;
	//				ftcClasses = Search.search("ftcId:" + appendFTC, FtcClass.class).fetch();
	//				if(ftcClasses.size() == 0){
	//					ftcClasses = Search.search("label:\"" + query + "\"~", FtcClass.class).fetch();
	//				}
	//			}
	//		}
	//		render(query, agents, ftcClasses);
	//	}


	//TODO doc
	//	private static boolean isValidQuery(String query) {
	//		if(query == null){
	//			return false;
	//		}
	//		try {
	//			new QueryParser(Search.getLuceneVersion(), "_docID", Search.getAnalyser()).parse(query);
	//			return true;
	//		} catch (ParseException e) {
	//			return false;
	//		}
	//	}

	//Starting page for semantic query
	public static void initQuery(){
		render();
	}

	//Performs the query
	public static void query(String query){

		try {
			//Checks whether the query is parsable
			brain.parseLabelClassExpression(query);
		} catch (ClassExpressionException e) {
			//If not display an error
			params.flash();
			String errorMessage = e.getMessage()
					.replaceAll("org.semanticweb.owlapi.expression.ParserException: ", "")
					.replaceAll("\n", "<br />")
					.replaceAll("^Encountered ", "Encountered <span class='parse_error'>")
					.replaceAll(" at line ", "</span> at line ");
			flash.error(errorMessage);

			render();
		}

		//The query is parsable

		//TODO check if forbidden query because too expensive (Thing at least)

		//Check if the query is already cached in the database
		OwlResult result = OwlResult.find("byQuery", query).first();

		if(result != null){
			Logger.info("cached inside DB");
			render(result);
		}

		//The query is not cached and is then going to be executed
		Promise<OwlResult> promise = new OwlQueryJob(query).now();
		Logger.info("Awaits for the results...");
		//TODO just retrieving the id from the job and do a query over the db with a limited number of records (range)
		//TODO atm, just not authorize the queries with too many classes as result
		result = await(promise);
		Logger.info("Ready to render");
		render(result);
	}

	//Redirection of the query in order for it to be displayed in the
	//address bar
	public static void owlQuery(String query, String formWidth){
		query(query);
	}
	
	public static void tree() {
		render();
	}
	
	public static void subClasses(String id) throws ClassExpressionException{
		System.out.println("id: " + id);
		//TODO the logic
		List<String> subClasses = brain.getSubClasses(id, true);
		renderJSON(subClasses);
	}


}