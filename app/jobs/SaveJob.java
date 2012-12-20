package jobs;

import models.FtcClass;
import play.jobs.Job;

public class SaveJob extends Job {

	public SaveJob(FtcClass ftcClass) {
		// TODO Auto-generated constructor stub
		this.ftcClass = ftcClass;
	}

	public FtcClass ftcClass;
	
	public void doJob(){
		this.ftcClass.save();
	}
	
}
