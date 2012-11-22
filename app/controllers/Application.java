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

		String ratioSvg = ftcClass.widthSvg + "%";
		render(ftcClass, ratioSvg);
	}

	public static void svg(FtcClass ftcClass) {
		renderBinary(new File(DatabaseFiller.LOCATION_GRAPHS + ftcClass.ftcId + ".svg"));
	}

}