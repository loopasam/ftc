package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Response;

import java.io.File;
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
		String ratioSvg = ftcClass.widthSvg + "%";

		List<FtcClass> subClasses = new ArrayList<FtcClass>();
		//Get the subclasses object
		for (String subClassId : ftcClass.subClasses) {
			FtcClass subClass = FtcClass.find("byFtcId", subClassId).first();
			subClasses.add(subClass);
		}

		render(ftcClass, ratioSvg, subClasses);
	}

	public static void map(String classId) {
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		render(ftcClass);
	}
	
	public static void svg(FtcClass ftcClass) {
		renderBinary(new File(DatabaseFiller.LOCATION_GRAPHS + ftcClass.ftcId + ".svg"));
	}

}