package org.fxyz;

import fxsampler.SampleBase;

public abstract class FXyzSample extends SampleBase {

	
	@Override
	public String getProjectName() {
		return "FXyz-Samples";
	}

	@Override
	public String getProjectVersion() {
		return "1.0";
	}
	
	@Override public String getSampleSourceURL() {
	    return getClass().getName().replace('.','/') + ".java";
	}
	
	@Override
	public String getControlStylesheetURL() {
		return null;
	}
    
}
