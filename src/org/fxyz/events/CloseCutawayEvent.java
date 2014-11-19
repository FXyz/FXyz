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

package org.fxyz.events;

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