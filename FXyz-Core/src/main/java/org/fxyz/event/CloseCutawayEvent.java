/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.event;

import javafx.event.Event;
import static javafx.event.Event.ANY;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author sphillips
 */
public class CloseCutawayEvent extends Event {
    public Object eventObject;
    public static final EventType<CloseCutawayEvent> CLOSE_CUTAWAY = new EventType(ANY, "CLOSE_CUTAWAY");

    public CloseCutawayEvent(Object t) {
        this(CLOSE_CUTAWAY);
        eventObject = t;
    }

    public CloseCutawayEvent(EventType<? extends Event> arg0) {
        super(arg0);
    }

    public CloseCutawayEvent(Object arg0, EventTarget arg1, EventType<? extends Event> arg2) {
        super(arg0, arg1, arg2);
        eventObject = arg0;
    }        
}