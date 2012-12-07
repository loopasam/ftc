package build;

import groovy.lang.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Agent;
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
	public final static String LOCATION_GRAPHS = "data/tmp/graphs/";

	public DatabaseFiller(String pathToOwlFile) {
		this.setPathToKb(pathToOwlFile);
	}

	public String getPathToKb() {
		return pathToKb;
	}

	public void setPathToKb(String pathToKb) {
		this.pathToKb = pathToKb;
	}

	public void start() throws BrainException, IOException, ClassNotFoundException {
		Logger.info("Loading DrugBank...");
		DrugBank drugBank = new DrugBank("data/tmp/drugbank.ser");
		Logger.info("Learning the KB...");
		Brain brain = new Brain();
		brain.learn(this.getPathToKb());
		Logger.info("Getting the therapeutic agents...");

		//FTC_C1 - only the one I've created are interesting :-P
		//TODO: Put the FTC_C1 class instead of the current one for dev
		//				List<String> ftcAndDrugBankClasses = brain.getSubClasses("FTC_C1", false);
		//Anti-blood coaguilation - x-small
//		List<String> ftcAndDrugBankClasses = brain.getSubClasses("FTC_A0050817", false);
		//Anti molecular function --> bigger (2500 classes)
				List<String> ftcAndDrugBankClasses = brain.getSubClasses("FTC_A0008150", false);
		//		ftcAndDrugBankClasses.add("FTC_A0008150");


		List<String> drugBankClasses = brain.getSubClasses("FTC_C2", false);
		List<String> ftcClasses = new ArrayList<String>();

		//Retrieves only the FTC classes and not the drugBank ones
		for (String ftcOrDrugBankClass : ftcAndDrugBankClasses) {
			if(!drugBankClasses.contains(ftcOrDrugBankClass)){
				ftcClasses.add(ftcOrDrugBankClass);
			}
		}

		Logger.info("There are a total of: " + ftcClasses.size() + " agents");
		int total = ftcClasses.size();
		int counter = 1;

		//Foreach FTC class, get the subclasses, id, etc... and generates the SVG graph
		for (String ftcClass : ftcClasses) {
			Logger.info("Storing class " + ftcClass + " in database - " + counter+ "/" + total);
			counter++;
			String label = brain.getLabel(ftcClass);
			String ftcId = ftcClass;
			String comment = brain.getComment(ftcClass);

			List<String> superClasses = brain.getSuperClasses(ftcClass, true);

			List<String> subClasses = brain.getSubClasses(ftcClass, true);
			List<String> ftcSubClass = subClasses;
			ftcSubClass.removeAll(drugBankClasses);

			//Retrieves the direct agents and store them as object
			subClasses = brain.getSubClasses(ftcClass, true);
			List<String> directAgentIds = new ArrayList<String>();
			List<Agent> directAgents = new ArrayList<Agent>();
			for (String subClass : subClasses) {
				if(drugBankClasses.contains(subClass)){

					Agent agent = Agent.find("byDrugBankId", subClass).first();

					if(agent == null){
						//TODO don't necessarily create an agent

						agent = getNewAgent(subClass, drugBank);
					}					
					directAgentIds.add(subClass);
					String drugLabel = brain.getLabel(subClass);
					agent.label = drugLabel;
					directAgents.add(agent);
				}
			}

			//Retrieves the indirect agents and store them as object
			List<String> indirectSubClasses = brain.getSubClasses(ftcClass, false);
			List<String> indirectAgents = new ArrayList<String>();
			for (String indirectSubClass : indirectSubClasses) {
				if(drugBankClasses.contains(indirectSubClass) && !directAgentIds.contains(indirectSubClass)){
					indirectAgents.add(indirectSubClass);
				}
			}

			//Create a new JPA entity with values used for the rendering later on.
			FtcClass ftcClassObject = new FtcClass(ftcId, label, comment, ftcSubClass, superClasses, directAgents, indirectAgents);
			//Save the graph as SVG to be ready to be rendered. The string of the content of the SVG is saved
			//on the database
			saveGraph(brain, ftcClassObject);
			//Saves the object in the database
			ftcClassObject.save();
		}
		brain.sleep();
	}

	private Agent getNewAgent(String id, DrugBank drugBank) {
		Agent agent = new Agent(id);
		Drug drug = drugBank.getDrug(id);
		agent.description = drug.getDescription();
		agent.indication = drug.getIndication();
		agent.mechanism = drug.getMechanism();
		agent.pharmacology = drug.getPharmacology();
		agent.atcCodes = drug.getAtcCodes();
		agent.categories = drug.getCategories();
		return agent;
	}

	private void saveGraph(Brain brain, FtcClass ftcClass) throws BrainException, IOException {

		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		//Initialize the layout of the SVG graph
		gv.addln("graph [splines=true overlap=false rankdir=BT nodesep=0.1 ranksep=0.2 bgcolor=\"#F4F4F4\"];");
		gv.addln("node [shape=box style = filled color=\"#72d93f\" fixedsize=true width=1.25 height=0.5 fontsize=6];");
		gv.addln("edge [arrowsize=0.3 color=gray];");

		//Maintain the id of the class visited in order to not display edges multiple times.
		DotRelations alreadyVisited = new DotRelations();
		//Classes not informative, not to be displayed - Arbitrary value.
		List<String> undesirableClasses = brain.getSuperClasses("FTC_C1", false);
		//Recursive function: Fill the gv object with the relations between class.
		addSuperClasses(ftcClass.ftcId, gv, brain, alreadyVisited, undesirableClasses);
		//Once all the relations are known, adds URLs to nodes.
		for (String node : alreadyVisited.getAllNodesOnce()) {
			//TODO: put the good URL
			gv.addln(node + " [URL=<http://localhost:9000/"+ node +">];");
			String formattedLabel = getFormattedLabel(brain.getLabel(node));

			gv.addln(node + " [label=\"\\N\\n" + formattedLabel + "\"];");
			if(ftcClass.ftcId.equals(node)){
				gv.addln(node + " [fillcolor=\"#00ece4\"];");
			}
		}

		gv.addln(gv.end_graph());
		String type = "svg";
		//Save the SVG graph. Arbitrary temporary location
		//Has to be done has the dot program is used to generate the graph
		String pathSvgFile = LOCATION_GRAPHS + ftcClass.ftcId + "." + type;
		File out = new File(pathSvgFile);
		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );

		//Block to hack the SVG content and replace it in an Web friendly way
		String svgContent = play.vfs.VirtualFile.fromRelativePath(pathSvgFile).contentAsString();
		String withoutXlinkSvgContent = svgContent.replaceAll("xlink:href", "target='_top' xlink:href")
				.replaceAll("<svg.*\n", "<svg");

		//Get the width and height of the SVG. Used later to render the SVG correctly on the browser
		Pattern pattern = Pattern.compile("viewBox=\"\\d+\\.\\d\\d \\d+\\.\\d\\d (\\d+)\\.\\d\\d (\\d+)\\.\\d\\d\"");
		Matcher matcher = pattern.matcher(withoutXlinkSvgContent);
		int width = 0;
		int height = 0;
		while (matcher.find()) {
			width = Integer.parseInt(matcher.group(1));
			height = Integer.parseInt(matcher.group(2));
		}

		//Set the width and height values
		ftcClass.widthSvg = width;
		ftcClass.heightSvg = height;

		//Save the content of the SVG done in a web friendly way
		ftcClass.svgGraph = withoutXlinkSvgContent;
	}

	//Display the label on multiple lines in order to save horizontal space
	private String getFormattedLabel(String label) {

		String formattedLabel = "";
		String[] words = label.split(" ");
		final int maxCharactersOnLine = 16;
		final int maxNumberOfLine = 3;
		int numberOfLines = 1;
		int charactersCurrentLine = 0;

		for (String word : words) {
			charactersCurrentLine += word.length();
			formattedLabel += word;
			if(charactersCurrentLine > maxCharactersOnLine){

				if(numberOfLines >= maxNumberOfLine){
					return formattedLabel + " [...]";
				}

				formattedLabel += "\\n";
				numberOfLines++;
				charactersCurrentLine = 0;

			}else{
				formattedLabel += " ";
			}
		}
		return formattedLabel;
	}

	//Adds the relations between classes for the graph using the DOT syntax
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
