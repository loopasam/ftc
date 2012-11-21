package build;

import groovy.lang.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.FtcClass;

import play.Logger;
import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingEntityException;
import utils.DotRelation;
import utils.DotRelations;
import utils.GraphViz;

public class DatabaseFiller {

	private String pathToKb;


	public DatabaseFiller(String pathToOwlFile) {
		this.setPathToKb(pathToOwlFile);
	}

	public String getPathToKb() {
		return pathToKb;
	}

	public void setPathToKb(String pathToKb) {
		this.pathToKb = pathToKb;
	}

	public void start() throws BrainException, IOException {
		Logger.info("Learning the KB...");
		Brain brain = new Brain();
		brain.learn(this.getPathToKb());
		Logger.info("Getting the therapeutic agents...");

		//FTC_C1 - only the one I've created are interesting :-P
		List<String> ftcAndDrugBankClasses = brain.getSubClasses("FTC_A0050817", false);
		List<String> drugBankClasses = brain.getSubClasses("FTC_C2", false);
		List<String> ftcClasses = new ArrayList<String>();

		for (String ftcOrDrugBankClass : ftcAndDrugBankClasses) {
			if(!drugBankClasses.contains(ftcOrDrugBankClass)){
				ftcClasses.add(ftcOrDrugBankClass);
			}
		}

		Logger.info("There are a total of: " + ftcClasses.size() + " agents");
		int total = ftcClasses.size();
		int counter = 1;

		for (String ftcClass : ftcClasses) {
			Logger.info("Storing class " + ftcClass + " - " + counter+ "/" + total);
			counter++;
			String label = brain.getLabel(ftcClass);
			String ftcId = ftcClass;
			//						List<String> superClasses = brain.getSuperClasses(ftcClass, true);
			List<String> subClasses = brain.getSubClasses(ftcClass, true);
			subClasses.removeAll(drugBankClasses);

			saveGraph(brain, ftcClass);

			new FtcClass(ftcId, label, subClasses, "public/images/graphs/" + ftcClass + ".svg").save();

		}


		brain.sleep();
		//load the brain object
		//fills and save the play objects

	}

	private void saveGraph(Brain brain, String ftcClass) throws BrainException, IOException {

		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		gv.addln("node [shape=box style = filled color=salmon2];");
		gv.addln("graph [splines=true size=150,15! overlap=false rankdir = \"BT\"];");
		DotRelations alreadyVisited = new DotRelations();
		//Classes not informative, not to be displayed
		List<String> undesirableClasses = brain.getSuperClasses("FTC_C1", false);
		addSuperClasses(ftcClass, gv, brain, alreadyVisited, undesirableClasses);

		for (String node : alreadyVisited.getAllNodesOnce()) {
			//TODO: put the good URL
			gv.addln(node + " [URL=<http://localhost:9000/"+ node +">];");
			gv.addln(node + " [label=\"\\N\\n" + brain.getLabel(node) + "\"];");
		}

		gv.addln(gv.end_graph());
		String type = "svg";
		File out = new File("public/images/graphs/" + ftcClass + "." + type);
		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
		String svgContent = play.vfs.VirtualFile.fromRelativePath("public/images/graphs/" + ftcClass + "." + type).contentAsString();
		String withoutXlinkSvgContent = svgContent.replaceAll("xlink:href", "target='_top' xlink:href")
				.replaceAll("<svg.*\n", "<svg");
		FileWriter fout = new FileWriter(out);
		
//		fout.write(svgContent);
		fout.write(withoutXlinkSvgContent);
		fout.close();


	}

	private void addSuperClasses(String ftcClass, GraphViz gv, Brain brain, DotRelations alreadyVisited, List<String> undesirableClasses) throws BrainException {
		List<String> directSuperClasses = brain.getSuperClasses(ftcClass, true);
		directSuperClasses.removeAll(undesirableClasses);
		for (String directSuperClass : directSuperClasses) {

			if(!alreadyVisited.contains(ftcClass, directSuperClass)){
				gv.addln(ftcClass + " -> " + directSuperClass + ";");
				alreadyVisited.addRelation(ftcClass, directSuperClass);
				addSuperClasses(directSuperClass, gv, brain, alreadyVisited, undesirableClasses);
			}
		}
	}


}
