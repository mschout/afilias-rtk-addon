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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

public class LaunchInfo extends EPPXMLBase implements epp_Extension
{
	private static final long serialVersionUID = 3872687725533251114L;
	
	private boolean includeMark = false;
    private String phase;
    private String applicationID;
    private String launchStatus;
    private Collection<String> marks; //mark:mark 

    public LaunchInfo () {}

    public LaunchInfo (String phase) { 
        this.phase = phase; 
    }

    public LaunchInfo (String phase, String applicationID) { 
        this.phase = phase; 
        this.applicationID = applicationID;
    }

    public LaunchInfo (String phase, String applicationID, boolean includeMark) { 
        this.phase = phase;
        this.applicationID = applicationID;
        this.includeMark = includeMark;
    }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; } 

    public String getApplicationID() { return applicationID; }
    public void setApplicationID(String value) { this.applicationID = value; }

    public boolean getIncludeMark() { return includeMark; }
    public void setIncludeMark(boolean value) { this.includeMark = value; }

    public String getLaunchStatus() { return launchStatus; }

    public Collection<String> getMarks() { return marks; } 

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if (phase == null || phase.isEmpty())  throw new epp_XMLException("launch:phase invalid ("+phase+")");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("launch:info");
        e.setAttribute("xmlns:launch", "urn:ietf:params:xml:ns:launch-1.0");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:launch-1.0 launch-1.0.xsd");

        if (includeMark) {
        	e.setAttribute("includeMark", "true");
        } else {
        	e.setAttribute("includeMark", "false");
        }

        ExtUtils.addXMLElement(doc, e, "launch:phase", phase);

        if (applicationID != null && !applicationID.isEmpty()) {
           ExtUtils.addXMLElement(doc, e, "launch:applicationID", applicationID);
        }

        doc.appendChild( e );
        
        String xml;
        
        try
        {
            xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building launch:info XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The extension XML is: ["+xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return xml;
    }

    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";

        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = getInnerXML(xml);

        try
        {
            if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element node = getDocumentElement();

            if ( node == null )
            {
                return;
            }

            NodeList node_list = node.getChildNodes();

            if ( node_list.getLength() == 0 )
            {
                throw new epp_XMLException("missing info results");
            }

            debug(DEBUG_LEVEL_TWO,method_name,"node_list's node count ["+node_list.getLength()+"]");
            
            phase = null;
            marks = new ArrayList<String>();  
            for (int count = 0; count < node_list.getLength(); count++)
            {
                Node a_node = node_list.item(count);

                if ( a_node.getNodeName().equals("launch:phase") )
                {
                    phase = a_node.getFirstChild().getNodeValue();
                }

                if ( a_node.getNodeName().equals("launch:applicationID") )
                {
                    applicationID = a_node.getFirstChild().getNodeValue();
                }

                if ( a_node.getNodeName().equals("launch:status") )
                {
                    launchStatus = ((Element)a_node).getAttribute("s");
                }

                if ( a_node.getNodeName().equals("mark:mark") )
                {
                    marks.add(a_node.getFirstChild().getNodeValue());
                }
            }

        }
        catch (SAXException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }
        catch (IOException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
    }

    protected String getInnerXML(String xml)
    {
        if ( xml == null || xml.length() == 0 )
                        return xml;

        int indexOfStart = xml.indexOf("<launch:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</launch:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

        return xml;
    }
}
