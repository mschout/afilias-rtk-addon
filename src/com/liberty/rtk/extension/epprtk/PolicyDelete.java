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

/*
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/Fee.java,v 1.5 2010/08/13 19:43:03 dongjinkim Exp $
 * $Revision: 1.5 $
 * $Date: 2010/08/13 19:43:03 $
 */

package com.liberty.rtk.extension.epprtk;

import java.io.IOException;
import java.sql.Timestamp;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * This class is used to exchange Fee data.
 *
 * @see com.liberty.rtk.extension.epprtk.FeeData
 * @see com.liberty.rtk.extension.epprtk.example.FeeSessionExample
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class PolicyDelete extends EPPXMLBase implements epp_Extension
{
    private static final long serialVersionUID = 7303436587391795381L;

    private static final String prefix = "policy:";

    private String reason;

    
    /**
     * Default constructor
     */
    public PolicyDelete () {}

    /**
     * Constructor with Domain Fee Unspec XML string to automatically parse.
     * @param xml The EPP Domain Info response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public PolicyDelete (String xml) throws epp_XMLException
    {
        String method_name = "PolicyDelete(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    public void setReason(String data) { reason = data; }
    /**
     * Accessor method for the fee app_date data member.
     * @param value java.util.String
     */
    public String getReason() { return reason; }

   
    /**
     * Converts the fee data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
   	
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        Document doc = new DocumentImpl();

        Element delete = doc.createElement(prefix + "delete");
        setNamespace(delete);
        ExtUtils.addXMLElement(doc, delete, prefix + "reason", getReason());
        doc.appendChild( delete );
        String delete_xml;
        
        try
        {
        	delete_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        
        return delete_xml;    	

    }
    
    
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = getInnerXML(xml);

        try
        {
            reason = null;

            if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element delete_node = getDocumentElement();

            if ( delete_node == null )
            {
                return;
            }

            NodeList delete_node_list = delete_node.getChildNodes();
			
            if ( delete_node_list.getLength() == 0 )
            {
                return;
			}

            debug(DEBUG_LEVEL_TWO,method_name,"aero_node_list's node count ["+delete_node_list.getLength()+"]");

            for (int count = 0; count < delete_node_list.getLength(); count++)
            {
                Node a_node = delete_node_list.item(count);

                if ( a_node.getNodeName().equals("policy:reason") ) 
				{
                	reason = a_node.getFirstChild().getNodeValue();
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

        int indexOfStart = xml.indexOf("<policy:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</policy:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);
        
		return xml;
	}
    
    private static void setNamespace(Element element) throws epp_XMLException
    {      
        element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        element.setAttribute("xmlns:policy", "urn:afilias:params:xml:ns:policy-1.0");
        element.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:policy-1.0 policy-1.0.xsd");
    }
}