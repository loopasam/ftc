package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jobs.CleanJob;
import jobs.ConvertionJob;
import jobs.EraseJob;
import jobs.EvaluationJob;
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

	public static void build() {
		if(Cache.get("jobRunning") != null){
			flash.error("A job is already running!");
		}else{
			flash.success("Full build into OWL launched - Follow progresses on the log file or console");
			new FullBuildJob().now();
		}
		index();
	}

	public static void createDBFromKB() {
		if(Cache.get("jobRunning") != null){
			flash.error("A job is already running!");
		}else{
			flash.success("Convertion into database started - Follow progresses on the log file or console");
			new ConvertionJob().now();
		}
		index();
	}

	public static void deleteDabatase() {
		if(Cache.get("jobRunning") != null){
			flash.error("A job is already running!");
		}else{
			flash.success("Database is getting erased - Follow progresses on the log file or console");
			new EraseJob().now();
		}
		index();
	}

	public static void cleanTmpResources() {
		if(Cache.get("jobRunning") != null){
			flash.error("A job is already running!");
		}else{
			flash.success("Temporary folder is getting erased - Follow progresses on the log file or console");
			new CleanJob().now();
		}
		index();
	}
	
	public static void evaluation() {
		if(Cache.get("jobRunning") != null){
			flash.error("A job is already running!");
		}else{
			flash.success("Evaluation report being build - Follow progresses on the log file or console");
			new EvaluationJob().now();
		}
		index();
	}

}
