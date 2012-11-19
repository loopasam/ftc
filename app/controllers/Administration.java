package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import play.vfs.VirtualFile;

@With(Security.class)
public class Administration extends Controller {

	public static void index() {
		Logger.info("entering method");
		render();
	}

}
