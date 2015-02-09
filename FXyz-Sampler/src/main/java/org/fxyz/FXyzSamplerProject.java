package org.fxyz;

import org.fxyz.model.WelcomePage;

public interface FXyzSamplerProject {

    /**
     * @return 
     */
    public String getProjectName();
    
    /**
     * 
     * @return 
     */
    public String getSampleBasePackage();
    
    /**
     * Node that will be displayed in welcome tab, when project's root is
     * selected in the tree. If this method returns null, default page will 
     * be used
     * @return 
     */
    public WelcomePage getWelcomePage();
}
