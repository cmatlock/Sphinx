package controllers;

import javax.ws.rs.ProcessingException;

import com.fasterxml.jackson.databind.JsonNode;

import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.settings;

public class UserSettingsController extends Controller{
	public static Form<ChangeInterval> intervalForm = Form.form(ChangeInterval.class);
	
	public static Result updateInterval(){
		String message = "";
		

		Form<ChangeInterval> ff = intervalForm.bindFromRequest();
		
		String username = "jay-z";
		User user = User.findByName(username);
		Integer newRate = 0;
		
		try {
			user.setUpdateFrequency(newRate = ff.get().interval);
			message = "Update interval set to " + newRate;
		} catch (ProcessingException e) {
			message += "\n"+e.getMessage();
		}
		 
		
		return ok(settings.render(message, intervalForm));
	}

}
