package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	public static void index() {

		List<FtcClass> FtcClasses = FtcClass.findAll();
		System.out.println("Number of classes: " + FtcClasses.size());
		render(FtcClasses);
	}

	public static void classVisu(String classId){
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		if(ftcClass == null){
			error(404, "Named class '" + classId + "' does not exist");
		}
		render(ftcClass);
	}

}