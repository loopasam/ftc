/**
 * 
 */
package build;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Samuel Croset
 *
 */
public abstract class Parser {

	private String pathOut;

	public Parser(String pathOut) {
		this.setPathOut(pathOut);
	}

	public void setPathOut(String pathOut) {
		this.pathOut = pathOut;
	}

	public String getPathOut() {
		return pathOut;
	}

	public abstract void start() throws FileNotFoundException, IOException;

	public abstract Object save() throws FileNotFoundException, IOException;


}
