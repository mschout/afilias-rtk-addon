/*
**
** EPP RTK Java
** Copyright (C) 2003, Liberty Registry Management Services, Inc.
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

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

public class LaunchUpdate extends EPPXMLBase implements epp_Extension
{
	private static final long serialVersionUID = -91132921037801826L;
	
	private String phase;
    private String applicationID;

    public LaunchUpdate() {}

    public LaunchUpdate (String phase, String applicationID) {
       this.phase = phase;
       this.applicationID = applicationID;
    }

    public void setPhase(String value) { phase = value; }
    public String getPhase() { return phase; }

    public void setApplicationID(String value) { applicationID = value; }
    public String getApplicationID() { return applicationID; }


    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if (phase == null || phase.length() == 0) throw new epp_XMLException("launch:phase invalid ("+phase+")");
        if (applicationID == null || applicationID.length() == 0) throw new epp_XMLException("launch:applicationID invalid ("+applicationID+")");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("launch:update");
        e.setAttribute("xmlns:launch", "urn:ietf:params:xml:ns:launch-1.0");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:launch-1.0 launch-1.0.xsd");

        ExtUtils.addXMLElement(doc, e, "launch:phase", phase);    
        ExtUtils.addXMLElement(doc, e, "launch:applicationID", applicationID);

        doc.appendChild( e );
        
        String xml;
        
        try
        {
            xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building launch:check XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The extension XML is: ["+xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return xml;
    }

    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");
        debug(DEBUG_LEVEL_THREE,method_name,"Nothing to do");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
    }
}
