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

public class OxrsDomain extends EPPXMLBase implements epp_Extension
{
    private static final long serialVersionUID = -1009639840393377748L;
    
    private String command_;
    private String maintainerURL_;
    private String domainRoid_;
    private boolean chg_;

    public OxrsDomain () { }

    public void setCommand(String value) { command_ = value; }
    public String getCommand() { return command_; }

    public void setMaintainerURL(String value) { maintainerURL_ = value; }
    public String getMaintainerURL() { return maintainerURL_; }

    public void setDomainRoid(String value) { domainRoid_ = value; }
    public String getDomainRoid() { return domainRoid_; }

    public void setChg(boolean value) { chg_ = value; }
    public boolean getChg() { return chg_; }

    /**
     * Renders the OxrsDomain extension "extension" for EPP RFC.<br>
     * This qualifies the maintainerUrl, domainRoid in 
     * domain:create request and domain:info response.
     * Implemented method from org.openrrc.rtk.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

	String command_ = getCommand();

        if ( command_ == null || command_.equals("") )
        {
            throw new epp_XMLException("missing epp command for oxrs extension");
        }

        Document doc = new DocumentImpl();
        
        Element oxrs = doc.createElement("oxrs:"+command_);

        setAttribute(oxrs);
   
        Element chg_element = null; 
 
        if (command_.equals("update")) {
             if (chg_) {  
                chg_element = doc.createElement("oxrs:chg");

                if (maintainerURL_ != null && maintainerURL_.length() != 0) {
                  ExtUtils.addXMLElement(doc, chg_element, "oxrs:maintainerUrl", maintainerURL_);
                }
            }
        } else if (command_.equals("create")) {
             if (maintainerURL_ != null && maintainerURL_.length() != 0) {
                ExtUtils.addXMLElement(doc, oxrs, "oxrs:maintainerUrl", maintainerURL_);
             }
        }
 
        // create <oxrs:domainRoid>
        if ((command_.equals("info") || command_.equals("update")) && domainRoid_ != null && domainRoid_.length() != 0) {
            ExtUtils.addXMLElement(doc, oxrs, "oxrs:domainRoid", domainRoid_);
        } 

        if (chg_element != null) oxrs.appendChild( chg_element );

        doc.appendChild( oxrs );
        
        String oxrs_xml;
        
        try
        {
            oxrs_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building oxrs:transfer XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The OxrsDomain extension XML is: ["+oxrs_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return oxrs_xml;
    }

    public void fromXML(String xml) throws epp_XMLException
	{
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = getInnerXML(xml);
        
        try
        {
            command_ = null;
            maintainerURL_ = null;

            if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element oxrs_node = getDocumentElement();

            if ( oxrs_node == null )
            {
                return;
            }

            NodeList oxrs_node_list = oxrs_node.getChildNodes();
			
            if ( oxrs_node_list.getLength() == 0 )
            {
                return;
        	}

            debug(DEBUG_LEVEL_TWO,method_name,"oxrs_node_list's node count ["+oxrs_node_list.getLength()+"]");

            for (int count = 0; count < oxrs_node_list.getLength(); count++)
            {
                Node a_node = oxrs_node_list.item(count);

                if ( a_node.getNodeName().equals("oxrs:domainRoid") )
                {
                    domainRoid_ = a_node.getFirstChild().getNodeValue();
                }

                if ( a_node.getNodeName().equals("oxrs:maintainerUrl") )
                {
                    maintainerURL_ = a_node.getFirstChild().getNodeValue();
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

	protected void setAttribute(Element oxrs)
	{
           oxrs.setAttribute("xmlns:oxrs", "urn:afilias:params:xml:ns:oxrs-1.1");
           oxrs.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
           oxrs.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:oxrs-1.1 oxrs-1.1.xsd");
	}

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<oxrs:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</oxrs:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

	return xml;
	}
}
