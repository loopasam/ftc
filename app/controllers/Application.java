package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import build.DatabaseFiller;

import models.*;

public class Application extends Controller {

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

}