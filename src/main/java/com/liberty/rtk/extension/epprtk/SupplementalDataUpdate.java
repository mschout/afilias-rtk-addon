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

public class SupplementalDataUpdate extends EPPXMLBase implements epp_Extension
{
    private SupplementalDataPut put_;

    public SupplementalDataUpdate () {}

    public void setPut(SupplementalDataPut value) { put_ = value; }
    public SupplementalDataPut getPut() { return put_; }

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( put_ == null) throw new epp_XMLException("data missing");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("supplementalData:update");
        e.setAttribute("xmlns:supplementalData", "urn:afilias:params:xml:ns:supplementalData-1.0");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:supplementalData-1.0 supplementalData-1.0.xsd");

        if (put_ != null && !put_.isEmpty()) e.appendChild(put_.getElement(doc));

        doc.appendChild(e);
        
        String suppdata_update_xml;
        
        try
        {
            suppdata_update_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building secDNS:update XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The extension XML is: ["+suppdata_update_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return suppdata_update_xml;
    }

    /**
     * This would parse a response from the server with the <supplementalData:update> extension.
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
