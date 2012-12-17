package controllers;

import play.*;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.libs.F.Promise;
import play.modules.search.Query;
import play.modules.search.Query.SearchException;
import play.modules.search.Search;
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

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;


import build.DatabaseFiller;

import models.*;

public class Application extends Controller {

	//Static brain object there to hold the ontology in memory
	//TODO
	public static Brain brain;

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
		for (String subClassId : ftcClass.subClasses) {
			FtcClass subClass = FtcClass.find("byFtcId", subClassId).first();
			subClasses.add(subClass);
		}

		List<FtcClass> superClasses = new ArrayList<FtcClass>();
		//Get the super classes object
		for (String superClassId : ftcClass.superClasses) {
			FtcClass superClass = FtcClass.find("byFtcId", superClassId).first();
			superClasses.add(superClass);
		}

		List<Agent> indirectAgents = new ArrayList<Agent>();
		//Get the indirect agents object
		for (String indirectAgentId : ftcClass.indirectAgentsId) {
			Agent indirectAgent = Agent.find("byDrugBankId", indirectAgentId).first();
			indirectAgents.add(indirectAgent);
		}

		render(ftcClass, ratioSvg, subClasses, superClasses, indirectAgents);
	}


	public static void map(String classId) {
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		render(ftcClass);
	}

	public static void agent(String drugbankId){
		Agent agent = Agent.find("byDrugBankId", drugbankId).first();
		render(agent);
	}

	public static void postSearch(@Required String query) {
		if(validation.hasErrors()){
			//TODO put in the flash
			redirect("/search/");
		}
		search(query);
	}

	//TODO finir le CSS and HTML
	public static void search(String query) {
		List<Agent> agents = null;
		List<Agent> ftcClasses = null;

		if(isValidQuery(query)){

			agents = Search.search("drugBankId:" + query, Agent.class).fetch();
			if(agents.size() == 0){
				agents = Search.search("label:" + query + "~", Agent.class).fetch();
				if(agents.size() == 0){
					agents = Search.search("label:" + query + "*~", Agent.class).fetch();
				}
			}

			ftcClasses = Search.search("ftcId:" + query, FtcClass.class).fetch();
			if(ftcClasses.size() == 0){
				String appendFTC = "FTC_" + query;
				ftcClasses = Search.search("ftcId:" + appendFTC, FtcClass.class).fetch();
				if(ftcClasses.size() == 0){
					ftcClasses = Search.search("label:\"" + query + "\"~", FtcClass.class).fetch();
				}
			}
		}
		render(query, agents, ftcClasses);
	}


	//TODO doc
	private static boolean isValidQuery(String query) {
		if(query == null){
			return false;
		}
		try {
			new QueryParser(Search.getLuceneVersion(), "_docID", Search.getAnalyser()).parse(query);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}


	public static void query(){
		render();
	}

	//TODO action executing the query
	public static void owlQuery(String query){
		try {
			brain.parseLabelClassExpression(query);
		} catch (ClassExpressionException e) {
			//TODO generate an error for the HTML to display
			params.flash();
			String errorMessage = e.getMessage()
					.replaceAll("org.semanticweb.owlapi.expression.ParserException: ", "")
					.replaceAll("\n", "<br />")
					.replaceAll("^Encountered ", "Encountered <span class='parse_error'>")
					.replaceAll(" at line ", "</span> at line ");
			
			
			flash.error(errorMessage);
			query();
		}

		//TODO before starting the job verifying if in DB/named class, etc...
		Promise<List<String>> result = new OwlQueryJob(query).now();
		List<String> subClasses = await(result);
		//store in DB for caching
		Logger.info("Ready To render");
		showResults(subClasses);
	}
	
	public static void showResults(List<String> subClasses){
		Logger.info("inside rendering method...");
		render(subClasses);
	}
	
}