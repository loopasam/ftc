package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Security.class)
public class Administration extends Controller {
	
    public static void index() {
        render();
    }
	
}
