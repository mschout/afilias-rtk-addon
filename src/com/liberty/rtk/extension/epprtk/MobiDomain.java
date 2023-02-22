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

/*
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/MobiDomain.java,v 1.1 2006/03/01 21:51:32 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2006/03/01 21:51:32 $
 */

package com.liberty.rtk.extension.epprtk;

import java.io.*;
import java.util.*;
import java.text.*;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;


//TODO refactor to a unified super class for Mobi and Aero class
public class MobiDomain extends EPPXMLBase implements epp_Extension
{
    private String command_;
    private String maintainerUrl_;

    public MobiDomain () { }

    public void setCommand(String value) { command_ = value; }
    public String getCommand() { return command_; }

    public void setMaintainerUrl(String maintainerUrl) { maintainerUrl_ = maintainerUrl; }
    public String getMaintainerUrl() { return maintainerUrl_; }


    /**
     * Renders the MobiDomain extension "extension" for EPP RFC.<br>
     * This qualifies the maintainerUrl name of the domain(s) in 
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
            throw new epp_XMLException("missing epp command for mobi extension");
        }

        Document doc = new DocumentImpl();
        
        Element mobi = doc.createElement("mobi:"+command_);

        setAttribute(mobi);
        
		// create <mobi:maintainerUrl>
		ExtUtils.addXMLElement(doc, mobi, "mobi:maintainerUrl", maintainerUrl_);

        doc.appendChild( mobi );
        
        String mobi_xml;
        
        try
        {
            mobi_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building oxrs:transfer XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The MobiDomain extension XML is: ["+mobi_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return mobi_xml;
    }

    public void fromXML(String xml) throws epp_XMLException
	{
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = getInnerXML(xml);
        
        try
        {
            command_ = null;
            maintainerUrl_ = null;

            if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element mobi_node = getDocumentElement();

            if ( mobi_node == null )
            {
                return;
            }

            NodeList mobi_node_list = mobi_node.getChildNodes();
			
            if ( mobi_node_list.getLength() == 0 )
            {
                return;
			}

            debug(DEBUG_LEVEL_TWO,method_name,"mobi_node_list's node count ["+mobi_node_list.getLength()+"]");

            for (int count = 0; count < mobi_node_list.getLength(); count++)
            {
                Node a_node = mobi_node_list.item(count);

                if ( a_node.getNodeName().equals("mobi:maintainerUrl") ) 
				{
                    maintainerUrl_ = a_node.getFirstChild().getNodeValue();
					break;
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

	protected void setAttribute(Element mobi)
	{
        mobi.setAttribute("xmlns:mobi", "urn:ietf:params:xml:ns:mobi-1.0");
        mobi.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        mobi.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:mobi-1.0 mobi-1.0.xsd");
	}

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<mobi:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</mobi:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

		return xml;
	}
}
