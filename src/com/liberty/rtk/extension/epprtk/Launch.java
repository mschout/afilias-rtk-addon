/*
**
** EPP RTK Java
** Copyright (C) 2001-2003, Liberty Registry Management Services, Inc.
**
**
** This library is free software; you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation; either
** version 2.1 of the License, or (at your option) any later version.
**
** This library is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with this library; if not, write to the Free Software
** Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
**
*/


package com.liberty.rtk.extension.epprtk;

import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Launch 
{
    private String command_;
    private String phase;
    private String applicationID;

    public Launch () {}

    public Launch (String phase, String applicationID) {
       this.phase = phase;
       this.applicationID = applicationID;
    }

    public void setCommand(String value) { command_ = value; }
    public String getCommand() { return command_; }

    public void setPhase(String value) { phase = value; }
    public String getPhase() { return phase; }

    public void setApplicationID(String value) { applicationID = value; }
    public String getApplicationID() { return applicationID; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        Element e = doc.createElement("launch:" + command_);

        if (phase == null || phase.length() == 0) throw new epp_XMLException("launch:phase invalid ("+phase+")");

        if ( command_ == null || command_.equals("") )
        {
            throw new epp_XMLException("missing epp command for launch extension");
        }

        ExtUtils.addXMLElement(doc, e, "launch:phase", phase);
        
        if (applicationID != null && applicationID.length() > 0) {
	    ExtUtils.addXMLElement(doc, e, "launch:applicationID", applicationID);
        }

        return e;
    }

    public String toString()
    {
        return "[phase:" + phase + "|applicationID:" + applicationID + "]";
    }
}
