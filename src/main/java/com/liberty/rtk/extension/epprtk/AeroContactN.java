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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/AeroContactN.java,v 1.1 2006/08/23 20:26:47 ewang2004 Exp $
 * $Revision: 1.1 $
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

public class AeroContactN extends AeroBase
{
    private EnsInfo ens_info_ = null;
    private String maintainerUrl_ = null;

    public AeroContactN() { }

    public void setEnsInfo(EnsInfo ens_info) { ens_info_ = ens_info; }
    public EnsInfo getEnsInfo() { return ens_info_; }

    public void setMaintainerUrl(String maintainerUrl) { maintainerUrl_ = maintainerUrl; }
    public String getMaintainerUrl() { return maintainerUrl_; }

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

		String command_ = getCommand();

        if ( command_ == null || command_.equals("") )
        {
            throw new epp_XMLException("missing epp command for aero extension");
        }

        if ( !command_.equals("create") && !command_.equals("update") )
        {
            throw new epp_XMLException("only create/update command supported");
        }

        Document doc = new DocumentImpl();
        
        Element aero = doc.createElement("aero:"+command_);

        setAeroAttribute(aero);

        if ( maintainerUrl_ != null)
        {
            if ( command_.equals("create") )
            {
                ExtUtils.addXMLElement(doc, aero, "aero:maintainerUrl", maintainerUrl_);
            }
            else if ( command_.equals("update") )
            {
                Element chg = doc.createElement("aero:chg");
                ExtUtils.addXMLElement(doc, chg, "aero:maintainerUrl", maintainerUrl_);
                aero.appendChild( chg );
            }
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

        debug(DEBUG_LEVEL_THREE,method_name,"The MobiDomain extension XML is: ["+aero_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return aero_xml;
    }

    public void fromXML(String xml) throws epp_XMLException
	{
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        maintainerUrl_ = null;
        ens_info_ = null;

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

            NodeList aero_node_list = aero_node.getChildNodes();

            for (int count = 0; count < aero_node_list.getLength(); count++)
            {
                Node a_node = aero_node_list.item(count);

                if ( a_node.getNodeName().equals("aero:maintainerUrl") ) 
				{
                    maintainerUrl_ = a_node.getFirstChild().getNodeValue();
                }
                else if ( a_node.getNodeName().equals("aero:ensInfo"))
                {
                    NodeList ens_node_list = a_node.getChildNodes();

                    if ( ens_node_list.getLength() == 0 )
                    {
                        return;
                    }

                    debug(DEBUG_LEVEL_TWO,method_name,"aero_node_list's node count ["+aero_node_list.getLength()+"]");

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
