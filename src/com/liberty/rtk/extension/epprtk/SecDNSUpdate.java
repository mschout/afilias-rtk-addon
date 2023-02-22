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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/SecDNSUpdate.java,v 1.2 2007/11/19 20:25:47 asimbirt Exp $
 * $Revision: 1.2 $
 * $Date: 2007/11/19 20:25:47 $
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

public class SecDNSUpdate extends EPPXMLBase implements epp_Extension
{
    private SecDNSAdd add_;
    private SecDNSRem rem_;
    private SecDNSChg chg_;
    private boolean urgent_;

    public SecDNSUpdate () {}

    public void setAdd(SecDNSAdd value) { add_ = value; }
    public SecDNSAdd getAdd() { return add_; }

    public void setRem(SecDNSRem value) { rem_ = value; }
    public SecDNSRem getRem() { return rem_; }

    public void setChg(SecDNSChg value) { chg_ = value; }
    public SecDNSChg getChg() { return chg_; }

    public void setUrgent(boolean value) { urgent_ = value; }
    public boolean getUrgent() { return urgent_; }

    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( add_ == null && rem_ == null && chg_ == null ) throw new epp_XMLException("data missing");

        Document doc = new DocumentImpl();
        
        Element e = doc.createElement("secDNS:update");
        e.setAttribute("xmlns:secDNS", "urn:ietf:params:xml:ns:secDNS-1.1");
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:secDNS-1.1 secDNS-1.1.xsd");

        if (urgent_) e.setAttribute("urgent","1");

        if (add_ != null && !add_.isEmpty()) e.appendChild(add_.getElement(doc));
        if (rem_ != null && !rem_.isEmpty()) e.appendChild(rem_.getElement(doc));
        if (chg_ != null && !chg_.isEmpty()) e.appendChild(chg_.getElement(doc));

        doc.appendChild(e);
        
        String secDNS_update_xml;
        
        try
        {
            secDNS_update_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building secDNS:update XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The extension XML is: ["+secDNS_update_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return secDNS_update_xml;
    }

    /**
     * This would parse a response from the server with the <secDNS:update> extension.
     * but <secDNS:update> is only used in the <domain:update> request.<br>
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
