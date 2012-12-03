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

		render(ftcClass, ratioSvg, subClasses, superClasses);
	}

	public static void svg(FtcClass ftcClass) {
		renderBinary(new File(DatabaseFiller.LOCATION_GRAPHS + ftcClass.ftcId + ".svg"));
	}


	public static void map(String classId) {
		FtcClass ftcClass = FtcClass.find("byFtcId", classId).first();
		int x = 0;
		int y = 0;
		//TODO: changer ymax par widdth etc...
		//TODO: send also the ratio for the width
		int xmax = ftcClass.widthSvg;
		int ymax = 284;
		render(ftcClass, x, y, xmax, ymax);
	}

//		public static void scaledSvg(FtcClass ftcClass) {
			
//			String svgContent = play.vfs.VirtualFile.fromRelativePath(DatabaseFiller.LOCATION_GRAPHS + ftcClass.ftcId + ".svg").contentAsString();
	//		String svgWithTunedViewBox = svgContent.replaceAll("viewBox=\".*\" xmlns=", "viewBox=\""+x + " " + y + " " + xmax + " " + ymax +"\" xmlns=");
	//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//		byte buf[] = svgWithTunedViewBox.getBytes(); 
	//		try {
	//			baos.write(buf);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		} 
	//		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	//		Response.current().contentType = "image/svg+xml";
	//		renderBinary(bais);
//		}

}