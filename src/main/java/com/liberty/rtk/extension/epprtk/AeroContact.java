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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/AeroContact.java,v 1.1 2006/01/13 16:29:43 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2006/01/13 16:29:43 $
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

public class AeroContact extends AeroBase
{
    private String registrantGroup_ = null;
    private String ensO_ = null;
    private String requestType_ = null;
    private String registrationType_ = null;
    private String credentialsType_ = null;
    private String credentialsValue_ = null;
    private String codeValue_ = null;
    private String uniqueIdentifier_ = null;
    private String lastCheckedDate_ = null;

    public AeroContact() { }

    public void setRegistrantGroup(String registrantGroup) { registrantGroup_ = registrantGroup; }
    public String getRegistrantGroup() { return registrantGroup_; }

    public void setEnsO(String ensO) { ensO_ = ensO; }
    public String getEnsO() { return ensO_; }

    public void setRequestType(String requestType) { requestType_ = requestType; }
    public String getRequestType() { return requestType_; }

    public void setRegistrationType(String registrationType) { registrationType_ = registrationType; }
    public String getRegistrationType() { return registrationType_; }

    public void setCredentialsType(String credentialsType) { credentialsType_ = credentialsType; }
    public String getCredentialsType() { return credentialsType_; }

    public void setCredentialsValue(String credentialsValue) { credentialsValue_ = credentialsValue; }
    public String getCredentialsValue() { return credentialsValue_; }

    public void setCodeValue(String codeValue) { codeValue_ = codeValue; }
    public String getCodeValue() { return codeValue_; }

    public void setUniqueIdentifier(String uniqueIdentifier) { uniqueIdentifier_ = uniqueIdentifier; }
    public String getUniqueIdentifier() { return uniqueIdentifier_; }

    public void setLastCheckedDate(String lastCheckedDate) { lastCheckedDate_ = lastCheckedDate; }
    public String getLastCheckedDate() { return lastCheckedDate_; }

    /**
	 * Contact create, update and delete operations restrited to SITA
	 * normal user can only use contact info command, which does not
	 * need this extension.
	 *
     * @throws org.openrtk.idl.epprtk.epp_XMLException
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        return null;
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

            Element aero_node = getDocumentElement();

            if ( aero_node == null )
            {
                return;
            }

			//aero:infData contains only one node: aero:ensInfo
            Node aero_ens_info_node = aero_node.getFirstChild();
            if ( aero_ens_info_node == null )
            {
                return;
            }

            NodeList aero_node_list = aero_ens_info_node.getChildNodes();
			
            if ( aero_node_list.getLength() == 0 )
            {
                return;
			}

            debug(DEBUG_LEVEL_TWO,method_name,"aero_node_list's node count ["+aero_node_list.getLength()+"]");

            for (int count = 0; count < aero_node_list.getLength(); count++)
            {
                Node a_node = aero_node_list.item(count);

                if ( a_node.getNodeName().equals("aero:registrantGroup") ) 
				{
                    registrantGroup_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:ensO") ) 
				{
                    ensO_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:requestType") ) 
				{
                    requestType_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:registrationType") ) 
				{
                    registrationType_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:credentialsType") ) 
				{
                    credentialsType_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:credentialsValue") ) 
				{
                    credentialsValue_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:codeValue") ) 
				{
                    codeValue_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:uniqueIdentifier") ) 
				{
                    uniqueIdentifier_ = a_node.getFirstChild().getNodeValue();
                }
				else if ( a_node.getNodeName().equals("aero:lastCheckedDate") ) 
				{
                    lastCheckedDate_ = a_node.getFirstChild().getNodeValue();
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
