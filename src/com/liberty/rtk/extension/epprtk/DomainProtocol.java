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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/DomainProtocol.java,v 1.1 2004/12/20 22:45:44 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/20 22:45:44 $
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
 * This extension is the .org domain's protocol in which it was registered: RRP or EPP.
 * The .org EPP server will optionally return the extension XML for this class in the &lt;domain:info&gt;
 * response.  If the extension is not present and this handler is called, no exception will be
 * thrown and the protocol will be set to "epp".  The .org registry specifies that the default 
 * protocol value is "epp".
 * @see com.liberty.rtk.extension.epprtk.example.DomainProtocolExample
 */
public class DomainProtocol extends EPPXMLBase implements epp_Extension
{

    private String domain_protocol_;

    /**
     * Default constructor
     */
    public DomainProtocol () {}

    /**
     * Constructor with Protocol Version Extension XML string (from .org's <domain:info>)
     * to automatically parse.
     * @param xml The Protocol Version Extension response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public DomainProtocol (String xml) throws epp_XMLException, epp_Exception
    {
        String method_name = "OxrsTransfer(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    /**
     * Accessor method for the domain protocol
     * @param value The new value for the domain protocol
     */
    public void setProtocol(String value) { domain_protocol_ = value; }
    /**
     * Accessor method for the domain protocol
     * @return value The current value of the domain protocol
     */
    public String getProtocol() { return domain_protocol_; }

    /**
     * Converts the test number data into XML to be put into the unspec
     * section of the request.
     * Implemented method from org.openrrc.rtk.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Unspec
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        debug(DEBUG_LEVEL_THREE,method_name,"Nothing to do");

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        // It would be impolite to return for this,
        // so let's just return an empty string.
        return "";
    }

    /**
     * This would parse a response from the server with the <oxrs:transfer> extension.
     * but <oxrs:transfer> is only used in the <domain:transfer> request.<br>
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @param A new oxrs transfer Extension XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = xml;

        try
        {

            domain_protocol_ = null;

            if ( xml_ == null ||
                 xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element infData_node = getDocumentElement();

            if ( infData_node == null )
            {
                // .org registry says that "epp" is the default if
                // the extension is not present
                domain_protocol_ = "epp";
                return;
            }

            NodeList infData_node_list = infData_node.getElementsByTagName("oxrs:infData");

            if ( infData_node_list.getLength() == 0 )
            {
                // .org registry says that "epp" is the default if
                // the extension is not present
                domain_protocol_ = "epp";
                return;
            }

            debug(DEBUG_LEVEL_TWO,method_name,"protocol_node_list's node count ["+infData_node_list.getLength()+"]");

            for (int count = 0; count < infData_node_list.getLength(); count++)
            {
                Node a_node = infData_node_list.item(count);

                // The protocol is contained inside the oxrs:infData, so let's dig inside there
                if ( a_node.getNodeName().equals("oxrs:infData") )
                {
                    NodeList protocol_node_list = ((Element)a_node).getElementsByTagName("oxrs:protocol");
                    if ( protocol_node_list.getLength() == 0 )
                    {
                        // no protocol child elements
                        throw new epp_XMLException("unparsable or missing domain protocol (oxrs:protocol)");
                    }
                    a_node = protocol_node_list.item(0);
                    domain_protocol_ = a_node.getFirstChild().getNodeValue();
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

}
