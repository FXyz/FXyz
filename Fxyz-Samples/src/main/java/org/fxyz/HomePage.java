/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz;

import javafx.scene.Node;
import org.fxyz.model.WelcomePage;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class HomePage extends WelcomePage{
    
    private static final String fxyzSamplesTitle = "F(X)yz Samples";
    private static final Node homePageContent = buildHomePageContent();
    
    public HomePage() {
        this(fxyzSamplesTitle, homePageContent);
    }

    private HomePage(String title, Node content) {
        super(title, content);
    }

    private static Node buildHomePageContent() {
        return null;
    }
    
}
