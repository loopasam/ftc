/**
 * 
 */
package build;

import java.io.Serializable;

/**
 * @author Samuel Croset
 *
 */
public class GoRelation implements Serializable {
    
    private static final long serialVersionUID = -2298494910167871011L;
    /**
     * @param string
     * @param target
     */
    public GoRelation(String type, String target) {
	this.setTarget(target);
	this.setType(type);
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    private String target;
    private String type;
    
    

}
