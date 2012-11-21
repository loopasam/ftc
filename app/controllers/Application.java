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

		List<FtcClass> FtcClasses = FtcClass.findAll();
		render(FtcClasses);
	}

	public static void classVisu(String classId){
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		if(ftcClass == null){
			error(404, "Named class '" + classId + "' does not exist");
		}

		//Normalize the width for the CSS of the parent element of the SVG. 
		int ratio = ftcClass.widthSvg*100/DatabaseFiller.MAX_WIDTH;

		//If the image is bigger than minimal size, then it will be scaled down automatically by the browser
		if(ratio > 100){
			ratio = 100;
		}
		String ratioSvg = ratio + "%";
		render(ftcClass, ratioSvg);
	}

	public static void svg(FtcClass ftcClass) {
		renderBinary(new File(DatabaseFiller.LOCATION_GRAPHS + ftcClass.ftcId + ".svg"));
	}

}