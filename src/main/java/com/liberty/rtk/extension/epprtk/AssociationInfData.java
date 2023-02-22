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

/**
 * @see com.liberty.rtk.extension.epprtk.example.SecDNSExample
 */
public class AssociationInfData extends EPPXMLBase implements epp_Extension
{
    private Collection contacts_ = Collections.EMPTY_LIST; // AssociationContact

    public AssociationInfData () {}

    public void setContacts(Collection value) { contacts_ = value; }
    public Collection getContacts() { return contacts_; }

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        return("Nothing to do");
    }


    /**
     * This would parse a response from the server with the <association:infData> extension.
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @param A new oxrs transfer Extension XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Action
     */
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

            Element membership_node = getDocumentElement();

            if ( membership_node == null )
            {
                return;
            }

            NodeList infData_node_list = membership_node.getChildNodes();

            if ( infData_node_list.getLength() == 0 )
            {
                throw new epp_XMLException("missing info results");
            }

            contacts_ = new ArrayList();
            for (int count = 0; count < infData_node_list.getLength(); count++)
            {
                Node a_node = infData_node_list.item(count);
                
                if (a_node.getNodeName().equals("association:contact")) {
	            NodeList a_subNode = a_node.getChildNodes();

                    AssociationContact temp = new AssociationContact();
                    for (int i = 0; i < a_subNode.getLength(); i++)
                    {
                       Node the_node = a_subNode.item(i); 		

		       if ( the_node.getNodeName().equals("association:id")) {
                          temp.setContactValue(the_node.getFirstChild().getNodeValue());
                       }
                    }

                    temp.setType(((Element)a_node).getAttribute("type"));

                    contacts_.add(temp);                           
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

        int indexOfStart = xml.indexOf("<association:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</association:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

        return xml;
    }
}
