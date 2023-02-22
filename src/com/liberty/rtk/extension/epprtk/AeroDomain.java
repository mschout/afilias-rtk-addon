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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/AeroDomain.java,v 1.2 2006/08/23 20:26:47 ewang2004 Exp $
 * $Revision: 1.2 $
 * $Date: 2006/08/23 20:26:47 $
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

public class AeroDomain extends AeroBase
{
    private String ensAuthID_;
    private String ensAuthKey_;
    private EnsInfo ens_info_;

    public AeroDomain () { }

    public void setEnsAuthID(String ensAuthID) { ensAuthID_ = ensAuthID; }
    public String getEnsAuthID() { return ensAuthID_; }

    public void setEnsAuthKey(String ensAuthKey) { ensAuthKey_ = ensAuthKey; }
    public String getEnsAuthKey() { return ensAuthKey_; }

    public void setEnsInfo(EnsInfo ensInfo) { ens_info_ = ensInfo; }
    public EnsInfo getEnsInfo() { return ens_info_; }

    /**
     * Renders the AeroDomain extension "extension" for EPP RFC.<br>
     * This qualifies the ensAuthID name of the domain(s) in 
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
            throw new epp_XMLException("missing epp command for aero extension");
        }

        Document doc = new DocumentImpl();

        Element change = null;

        if ("update".equals(command_))
        {
            change = doc.createElement("aero:chg");
        }

        Element aero = doc.createElement("aero:"+command_);

        setAeroAttribute(aero);
        
		// create <aero:ensAuthID>
        if (ensAuthID_ != null)
        {
            if (change != null)
            {
                ExtUtils.addXMLElement(doc, change, "aero:ensAuthID", ensAuthID_);
            }
            else
            {
                ExtUtils.addXMLElement(doc, aero, "aero:ensAuthID", ensAuthID_);
            }
        }

		// create <aero:ensAuthKey>
        if (ensAuthKey_ != null)
        {
            if (change != null)
            {
                ExtUtils.addXMLElement(doc, change, "aero:ensAuthKey", ensAuthKey_);
            }
            else
            {
                ExtUtils.addXMLElement(doc, aero, "aero:ensAuthKey", ensAuthKey_);
            }
        }

        if (change != null)
        {
            aero.appendChild(change);
        }

        if (ens_info_ != null)
        {
            Element element = doc.createElement("aero:ensInfo");
            addXMLElement(doc, element, ens_info_);
            aero.appendChild( element );
        }

        doc.appendChild( aero );
        
        String aero_xml;
        
        try
        {
            aero_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building oxrs:transfer XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The AeroDomain extension XML is: ["+aero_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return aero_xml;
    }

    private void addXMLElement(Document doc, Element element, EnsInfo ens_info)
    {
        if (ens_info == null)
        {
            return;
        }

        if (ens_info.getEnsClass() != null)
        {
            for (Iterator itor = ens_info.getEnsClass().iterator(); itor.hasNext();)
            {
                ExtUtils.addXMLElement(doc, element, "aero:ensClass", (String) itor.next());
            }
        }

        if (ens_info.getRegistrantGroup() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:registrantGroup", ens_info.getRegistrantGroup());
        }

        if (ens_info.getEnsO() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:ensO", ens_info.getEnsO());
        }

        if (ens_info.getRequestType() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:requestType", ens_info.getRequestType());
        }

        if (ens_info.getRegistrationType() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:registrationType", ens_info.getRegistrationType());
        }

        if (ens_info.getCredentialsType() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:credentialsType", ens_info.getCredentialsType());
        }

        if (ens_info.getCredentialsValue() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:credentialsValue", ens_info.getCredentialsValue());
        }

        if (ens_info.getCodeValue() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:codeValue", ens_info.getCodeValue());
        }

        if (ens_info.getUniqueIdentifier() != null)
        {
            ExtUtils.addXMLElement(doc, element, "aero:uniqueIdentifier", ens_info.getUniqueIdentifier());
        }
    }

    public void fromXML(String xml) throws epp_XMLException
	{
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = getInnerXML(xml);
        
        try
        {
            ensAuthID_ = null;

            if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element aero_node = getDocumentElement();

            if ( aero_node == null )
            {
                return;
            }

            NodeList aero_node_list = aero_node.getChildNodes();
			
            if ( aero_node_list.getLength() == 0 )
            {
                return;
			}

            debug(DEBUG_LEVEL_TWO,method_name,"aero_node_list's node count ["+aero_node_list.getLength()+"]");

            for (int count = 0; count < aero_node_list.getLength(); count++)
            {
                Node a_node = aero_node_list.item(count);

                if ( a_node.getNodeName().equals("aero:ensAuthID") ) 
				{
                    ensAuthID_ = a_node.getFirstChild().getNodeValue();
                }

                if ( a_node.getNodeName().equals("aero:ensInfo") )
                {
                    ens_info_ = new EnsInfo(a_node);
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
