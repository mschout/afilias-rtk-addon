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

public class AssociationContact 
{
    private String contactValue_;
    private String type_;
    private String authInfo_;

    public AssociationContact () {}

    public AssociationContact (String v, String t, String auth_info) {
       contactValue_ = v;
       type_ = t;
       authInfo_ = auth_info;
    }

    public void setContactValue(String value) { contactValue_ = value; }
    public String getContactValue() { return contactValue_; }

    public void setType(String value) { type_ = value; }
    public String getType() { return type_; }

    public void setAuthInfo(String value) { authInfo_ = value; }
    public String getAuthInfo() { return authInfo_; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        Element e = doc.createElement("association:contact");

        if (contactValue_ == null || contactValue_.length() == 0) throw new epp_XMLException("association:contact:id invalid ("+contactValue_+")");

        if (type_ == null || type_.length() == 0) throw new epp_XMLException("association:contact:type invalid ("+type_+")");

        e.setAttribute("type", type_);
        ExtUtils.addXMLElement(doc, e, "association:id", contactValue_);
        
        Element auth_info_element = doc.createElement("association:authInfo");

        ExtUtils.addXMLElement(doc, auth_info_element, "association:pw", authInfo_);

        e.appendChild(auth_info_element);

        return e;
    }

    public String toString()
    {
        return "[contact:" + contactValue_ + "|type:" + type_ + "]";
    }
}
