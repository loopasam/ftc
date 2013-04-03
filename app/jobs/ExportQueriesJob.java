package jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import models.Metrics;
import models.OwlResult;
import play.jobs.Job;
import play.vfs.VirtualFile;
import utils.GraphViz;

public class ExportQueriesJob extends Job {

	public void doJob() throws IOException {
		List<OwlResult> queries= OwlResult.findAll();
		Metrics metrics = (Metrics) Metrics.findAll().get(0);

		VirtualFile vf = VirtualFile.fromRelativePath("data/archives/queries-" + metrics.date + ".txt");
		FileWriter fstream = new FileWriter(vf.getRealFile());
		BufferedWriter out = new BufferedWriter(fstream);

		for (OwlResult owlResult : queries) {
			out.write(owlResult.query + " --- " + owlResult.numberOfTimes + "\n");
		}
		out.close();
	}
}
