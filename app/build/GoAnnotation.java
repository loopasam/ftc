/**
 * 
 */
package build;

import java.io.Serializable;

/**
 * @author Samuel Croset
 *
 */
public class GoAnnotation implements Serializable {

	private static final long serialVersionUID = 2168156249044371972L;
	//More details on the features:
	//http://www.geneontology.org/GO.format.gaf-2_0.shtml#db_reference
	//adapted for the tsv files dumped by web services
	//origin of the gene product (here should be only 'UniProtKB') - column 1
	private String database;
	//taxon of the annotated entity - column 5
	private String taxon;
	//extra qualifier not present most of the time - column 6
	private String qualifier;
	//go term identifier
	private String goId;
	//ref for the annotation
	private String reference;
	//Evidence code for the annotation
	private String evidence;
	//if electronic annotation, diaplays the provider
	private String evidenceProvider;
	//date at which the annotation as been made
	private String date;
	//team that has done the annot
	private String source;
	//Aspect of the ontology covered
	private String aspect;


	public void setAspect(String aspect) {
		this.aspect = aspect;
	}
	public String getAspect() {
		return aspect;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getTaxon() {
		return taxon;
	}
	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public String getGoId() {
		return goId;
	}
	public void setGoId(String goId) {
		this.goId = goId;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getEvidence() {
		return evidence;
	}
	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}
	public String getEvidenceProvider() {
		return evidenceProvider;
	}
	public void setEvidenceProvider(String evidenceProvider) {
		this.evidenceProvider = evidenceProvider;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

}
