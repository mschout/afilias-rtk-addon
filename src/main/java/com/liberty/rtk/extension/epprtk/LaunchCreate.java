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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

public class LaunchCreate extends EPPXMLBase implements epp_Extension
{
	private static final long serialVersionUID = -3695198395116490405L;
	
	private String type;
    private String phase;
    private LaunchNotice launchNotice;
    private String signedMark;
    private String applicationID; 

    public LaunchCreate () {}

    public LaunchCreate (String phase) { this.phase = phase; }

    public LaunchCreate (String phase, LaunchNotice launchNotice) { 
       this.phase = phase; 
       this.launchNotice = launchNotice;
    }

    public LaunchCreate (String phase, String signedMark) { 
       this.phase = phase; 
       this.signedMark = signedMark;
    }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; } 

    public void setType(String value) { this.type = value; }
    public String getType() { return type; }

    public void setLaunchNotice(LaunchNotice notice) { this.launchNotice = notice; }
    public LaunchNotice getLaunchNotice() { return launchNotice; }

    public void setSignedMark(String signedMark) { this.signedMark = signedMark; }
    public String getSignedMark() { return signedMark; }

    public String getApplicationID() { return applicationID; }

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if (phase == null || phase.isEmpty())  throw new epp_XMLException("launch:phase invalid ("+phase+")");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("launch:create");
        e.setAttribute("xmlns:launch", "urn:ietf:params:xml:ns:launch-1.0");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:launch-1.0 launch-1.0.xsd");

        if (type != null && !type.isEmpty()) {
           e.setAttribute("type", type);
        }

        ExtUtils.addXMLElement(doc, e, "launch:phase", phase);

        if (launchNotice != null) {
             e.appendChild(launchNotice.getElement(doc));
        } 
 
        if (signedMark != null && !signedMark.isEmpty()) {
        	 Element signedMarkElement = doc.createElement("smd:encodedSignedMark");
        	 signedMarkElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        	 signedMarkElement.setAttribute("xmlns:smd", "urn:ietf:params:xml:ns:signedMark-1.0");
        	 signedMarkElement.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:signedMark-1.0 signedMark-1.0");
        	 
        	 signedMarkElement.appendChild(doc.createTextNode(signedMark));
        	 
        	 e.appendChild(signedMarkElement);
        }        

        doc.appendChild( e );
        
        String xml;
        
        try
        {
            xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building launch:create XML ["+xcp.getMessage()+"]");
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
                return;
            }
            debug(DEBUG_LEVEL_TWO,method_name,"node_list's node count ["+node_list.getLength()+"]");
            
            phase = null;
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
