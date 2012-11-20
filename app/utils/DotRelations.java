package utils;

import java.util.ArrayList;
import java.util.List;

public class DotRelations {

	private List<DotRelation> relations;

	public DotRelations() {
		this.setRelations(new ArrayList<DotRelation>());
	}

	public List<DotRelation> getRelations() {
		return relations;
	}
	public void setRelations(List<DotRelation> relations) {
		this.relations = relations;
	}

	public void addRelation(String ftcClass, String directSuperClass) {
		DotRelation relation = new DotRelation(ftcClass, directSuperClass);
		this.getRelations().add(relation);
	}

	public boolean contains(String start, String end) {

		for (DotRelation relation : this.getRelations()) {
			if(relation.getStart().equals(start)){
				if(relation.getEnd().equals(end)){
					return true;
				}
			}
		}

		return false;
	}

	public List<String> getAllNodesOnce() {
		List<String> nodes = new ArrayList<String>();
		for (DotRelation relation : this.getRelations()) {

			if(!nodes.contains(relation.getStart())){
				nodes.add(relation.getStart());
			}
			if(!nodes.contains(relation.getEnd())){
				nodes.add(relation.getEnd());
			}

		}

		return nodes;
	}

}
