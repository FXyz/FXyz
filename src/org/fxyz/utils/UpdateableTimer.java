/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fxyz.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;

/**
 * 
 * @author Dub
 */
public abstract class UpdateableTimer extends AnimationTimer{
    private final List<Callable<Void>> updates = new ArrayList<>();
    public List<Callable<Void>> getUpdateList(){return updates;}
    private final int maxCalls;   

    public UpdateableTimer(int maxCallables) {
        this(maxCallables, false);
    }    
    
    public UpdateableTimer(int maxCallables, boolean start) {
        assert maxCallables > 0;
        
        this.maxCalls = maxCallables;
        if(start){
            start();
        }
    } 
    
    public void addUpdate(Callable<Void> c){
        if(updates.size() >= 0 && updates.size() < maxCalls){
            updates.add(c);
        }else{
            //return
        }
    }
    
    public void removeUpdate(Callable<Void> c){
        int idx = !updates.isEmpty() ? updates.indexOf(c) : -1;
        if(idx != -1){
            updates.remove(idx);
        }        
    }
    
    public void update(){
        if(!updates.isEmpty()){
            updates.forEach(u ->{ 
                try {
                    u.call();
                } catch (Exception ex) {
                    Logger.getLogger(UpdateableTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
            });            
        }
    }
    
    @Override
    public void handle(long now) {
        update();
    }
}
