/**
* ControlBase.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fxyz.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.Property;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 * @param <P>
 */
public abstract class ControlBase<P extends Property> extends StackPane{
    protected P controlledProperty;
    public ControlBase(final String fxml, final P prop) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setRoot(ControlBase.this);
            loader.setController(ControlBase.this);
            loader.load();
        } catch (IOException ex) {
            
            Logger.getLogger(CheckBoxControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.controlledProperty = prop;
        this.setPrefSize(USE_PREF_SIZE, StackPane.BASELINE_OFFSET_SAME_AS_HEIGHT );
        
        //this.getStyleClass().add("fxyz-control");
        
    }

    private ControlBase() {
        throw new UnsupportedOperationException("Cannot assign");
    }

    private ControlBase(Node... children) {
        throw new UnsupportedOperationException("Cannot assign");
    }
    
}
