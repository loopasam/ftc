package build;

public abstract class OwlExporter {
	
	private String pathOut;
	
	public String getPathOut() {
		return pathOut;
	}

	public void setPathOut(String pathOut) {
		this.pathOut = pathOut;
	}

	public OwlExporter(String pathOut){
		this.setPathOut(pathOut);
	}
	
	public abstract void start();
	
	public abstract void save();

}
