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

public class LaunchCheck extends EPPXMLBase implements epp_Extension
{
	private static final long serialVersionUID = 4246936961986335547L;
	
	private Collection<LaunchCd> launchCd; 
    private String phase;
    private String type;

    public LaunchCheck() {}

    public LaunchCheck(String phase, String type) {
       this.phase = phase;
       this.type = type;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public void setLaunchCd(Collection<LaunchCd> value) { launchCd = value; }
    public Collection<LaunchCd> getLaunchCd() { return launchCd; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; } 

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if (phase == null || phase.length() == 0) throw new epp_XMLException("launch:phase invalid ("+phase+")");
        if (type == null || type.isEmpty()) throw new epp_XMLException("type attribute is invalid ("+type+")");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("launch:check");
        e.setAttribute("xmlns:launch", "urn:ietf:params:xml:ns:launch-1.0");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:launch-1.0 launch-1.0.xsd");

        e.setAttribute("type", type);

        ExtUtils.addXMLElement(doc, e, "launch:phase", phase);    

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

            NodeList chkdata_node_list = node.getChildNodes();

            if ( chkdata_node_list.getLength() == 0 )
            {
                throw new epp_XMLException("missing launch check results");
            }

            launchCd = new ArrayList<LaunchCd>();
            for (int count = 0; count < chkdata_node_list.getLength(); count++)
            {
                Node a_node = chkdata_node_list.item(count);
                
                if (a_node.getNodeName().equals("launch:phase")) {
                	phase = a_node.getFirstChild().getNodeValue();
                }

                if (a_node.getNodeName().equals("launch:cd")) {
                    NodeList a_subNode = a_node.getChildNodes();

                    LaunchCd temp = new LaunchCd();
                    for (int i = 0; i < a_subNode.getLength(); i++)
                    {
                       Node the_node = a_subNode.item(i);

                       if ( the_node.getNodeName().equals("launch:name")) {
                          temp.setName(the_node.getFirstChild().getNodeValue());
                          
                          temp.setExists(((Element)the_node).getAttribute("exist"));	
                       }

                       if ( the_node.getNodeName().equals("launch:claimKey")) {
                          temp.setClaimsKey(the_node.getFirstChild().getNodeValue());
                       }
                    }

                    launchCd.add(temp);
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
