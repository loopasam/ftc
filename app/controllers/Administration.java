package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jobs.ConvertionJob;
import jobs.FullBuildJob;

import play.Logger;
import play.cache.Cache;
import play.jobs.Job;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.With;
import play.vfs.VirtualFile;

@With(Security.class)
public class Administration extends Controller {

	public static void index() {
		render();
	}

	public static void update() {
		flash.success("Update launched - Follow progresses on the log file or console");
		index();
	}

	public static void build() {
		flash.success("Full build launched - Follow progresses on the log file or console");
		new FullBuildJob().now();		
		index();
	}
	
	public static void createDBFromKB() {
		flash.success("Convertion into DB started - Follow progresses on the log file or console");
		new ConvertionJob().now();
		index();
	}



}
