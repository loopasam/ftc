package jobs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import models.Agent;
import models.FtcClass;
import models.Metrics;
import models.OwlResult;
import play.Logger;
import play.jobs.Job;
import play.vfs.VirtualFile;

public class ExportDataJob extends Job {

	public void doJob() throws IOException {
		Logger.info("Starting export flat file...");
		VirtualFile vf = VirtualFile.fromRelativePath("public/data/ftc-kb-full.obo");
		FileWriter fstream = new FileWriter(vf.getRealFile());
		BufferedWriter out = new BufferedWriter(fstream);

		List<FtcClass> ftcClasses = FtcClass.findAll();
		Logger.info("Exporting classes...");
		for (FtcClass ftcClass : ftcClasses) {
			out.write("[term]\n");
			out.write("id: " + ftcClass.ftcId + "\n");
			out.write("name: " + ftcClass.label.replace("'", "") + "\n");
			out.write("def: \"" + ftcClass.comment.replaceAll("<a.*'>", "").replaceAll("</a>", "") + "\"\n");
			out.write("namespace: agent\n");
			for (FtcClass superClass : ftcClass.superClasses) {
				out.write("is_a: " + superClass.ftcId + " ! " + superClass.label.replace("'", "") + "\n");
			}
			out.write("\n");
		}

		List<Agent> drugs= Agent.findAll();
		Logger.info("Exporting drugs...");
		for (Agent drug : drugs) {
			out.write("[term]\n");
			out.write("id: " + drug.drugBankId + "\n");
			out.write("name: " + drug.label.replace("'", "") + "\n");
			out.write("namespace: drug\n");
			out.write("xref: DrugBank:" + drug.drugBankId + "\n");
			for (FtcClass directClass : drug.directFtcClasses) {
				out.write("is_a: " + directClass.ftcId + " ! " + directClass.label.replace("'", "") + "\n");
			}
			out.write("\n");
		}

		out.close();

		Logger.info("Export done!");
	}
}
