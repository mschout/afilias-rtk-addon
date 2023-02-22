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

public class AssociationUpdate extends EPPXMLBase implements epp_Extension
{
    private AssociationAdd add_;
    private AssociationRem rem_;

    public AssociationUpdate () {}

    public void setAdd(AssociationAdd value) { add_ = value; }
    public AssociationAdd getAdd() { return add_; }

    public void setRem(AssociationRem value) { rem_ = value; }
    public AssociationRem getRem() { return rem_; }

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( add_ == null && rem_ == null ) throw new epp_XMLException("data missing");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("association:update");
        e.setAttribute("xmlns:association", "urn:afilias:params:xml:ns:association-1.0");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:association-1.0 association-1.0.xsd"); 

        if (add_ != null && !add_.isEmpty()) e.appendChild(add_.getElement(doc));
        if (rem_ != null && !rem_.isEmpty()) e.appendChild(rem_.getElement(doc));

        doc.appendChild(e);
        
        String membership_update_xml;
        
        try
        {
            membership_update_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building association:update XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The extension XML is: ["+membership_update_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return membership_update_xml;
    }

    /**
     * This would parse a response from the server with the <association:update> extension.
     * but <association:update> is only used in the <domain:update> request.<br>
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
