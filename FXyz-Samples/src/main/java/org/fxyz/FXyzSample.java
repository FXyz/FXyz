package org.fxyz;

import fxsampler.SampleBase;
import javafx.scene.Node;

public abstract class FXyzSample extends SampleBase {

    protected double mousePosX;
    protected double mousePosY;
    protected double mouseOldX;
    protected double mouseOldY;
    protected double mouseDeltaX;
    protected double mouseDeltaY;

    @Override
    public String getProjectName() {
        return "FXyz-Samples";
    }

    @Override
    public String getProjectVersion() {
        return "1.0";
    }

    @Override
    public String getSampleSourceURL() {
        return getClass().getName().replace('.', '/') + ".java";
    }

    @Override
    public String getControlStylesheetURL() {
        return null;
    }

    public abstract Node getSample();
}
