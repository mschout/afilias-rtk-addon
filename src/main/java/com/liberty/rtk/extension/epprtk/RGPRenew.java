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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/RGPRenew.java,v 1.1 2004/12/20 22:45:44 ewang2004 Exp $
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

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

/**
 * XXX Something should go here.
 */
public class RGPRenew extends EPPXMLBase implements epp_Extension
{

    /**
     * Default constructor
     */
    public RGPRenew () {}


    /**
     * Converts the rgp renew data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        Document doc = new DocumentImpl();
        
        Element rgp_renew = doc.createElement("rgp:renew");
        rgp_renew.setAttribute("xmlns:rgp", "urn:EPP:xml:ns:ext:rgp-1.0");
        rgp_renew.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rgp_renew.setAttribute("xsi:schemaLocation", "urn:EPP:xml:ns:ext:rgp-1.0 rgp-1.0.xsd");
        
        addXMLElement(doc, rgp_renew, "rgp:restore", null);

        doc.appendChild( rgp_renew );
        
        String rgp_renew_xml;
        
        try
        {
            rgp_renew_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building oxrs:transfer XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The rgp renew extension XML is: ["+rgp_renew_xml+"]");

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return rgp_renew_xml;
    }

    /**
     * Parses an XML String of rgp renew data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @param A new rgp renew Extension XML String to parse
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
