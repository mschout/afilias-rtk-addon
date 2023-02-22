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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/OxrsTransfer.java,v 1.1 2004/12/20 22:45:44 ewang2004 Exp $
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
 * When transfering a domain from an RRP-based registrar to an EPP-based on,
 * the .org registry requires new EPP contact data to be supplied which
 * replaces the placeholder contact data when the domain is transferred.
 * @see com.liberty.rtk.extension.epprtk.example.OxrsTransferExample
 */
public class OxrsTransfer extends EPPXMLBase implements epp_Extension
{

    private String registrant_contact_;
    private epp_DomainContact[] other_contacts_;

    /**
     * Default constructor
     */
    public OxrsTransfer () {}

    /**
     * Accessor method for the registrant contact
     * @param value The new single registrant contact.
     */
    public void setRegistrant(String value) { registrant_contact_ = value; }
    /**
     * Accessor method for the registrant contact
     * @return value The current registrant contact
     */
    public String getRegistrant() { return registrant_contact_; }

    /**
     * Accessor method for the array of contacts to be associated with the domain object
     * @param value The array of domain contacts
     */
    public void setContacts(epp_DomainContact[] value) { other_contacts_ = value; }
    /**
     * Accessor method for the array of contacts to be associated with the domain object
     * @return The array of domain contacts
     */
    public epp_DomainContact[] getContacts() { return other_contacts_; }

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

        if ( registrant_contact_ == null )
        {
            throw new epp_XMLException("missing registrant contact in oxrs:transfer");
        }
        if ( other_contacts_ == null ||
             other_contacts_.length == 0 )
        {
            throw new epp_XMLException("missing other contacts in oxrs:transfer");
        }

        Document doc = new DocumentImpl();
        
        Element oxrs_transfer = doc.createElement("oxrs:transfer");
        oxrs_transfer.setAttribute("xmlns:oxrs", "urn:ietf:params:xml:ns:oxrs-1.0");
        oxrs_transfer.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        oxrs_transfer.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:oxrs-1.0 oxrs-1.0.xsd");
        
        addXMLElement(doc, oxrs_transfer, "oxrs:registrant", registrant_contact_);

        if ( other_contacts_ != null && other_contacts_.length > 0 )
        {
            List contact_list = Arrays.asList(other_contacts_);
            for (Iterator it = contact_list.iterator(); it.hasNext();)
            {
                epp_DomainContact domain_contact = (epp_DomainContact)it.next();
                Element contact_element = addXMLElement(doc, oxrs_transfer, "oxrs:contact", domain_contact.m_id);
                contact_element.setAttribute("type", domain_contact.m_type.toString());
            }
        }

        doc.appendChild( oxrs_transfer );
        
        String oxrs_transfer_xml;
        
        try
        {
            oxrs_transfer_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building oxrs:transfer XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The transfer extension XML is: ["+oxrs_transfer_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return oxrs_transfer_xml;
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

        debug(DEBUG_LEVEL_THREE,method_name,"Nothing to do");

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

    }

}
