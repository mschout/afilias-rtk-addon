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

public class AssociationAdd
{
    private Collection contacts_ = Collections.EMPTY_LIST; // AssociationContact

    public void setContacts(Collection value) { contacts_ = value; }
    public Collection getContacts() { return contacts_; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        if (contacts_ == null) throw new epp_XMLException("association:update.association:add missing association:contact");
        Element e = doc.createElement("association:add");
        for (Iterator it = contacts_.iterator(); it.hasNext();) e.appendChild(((AssociationContact)it.next()).getElement(doc));
        return e;
    }

    public boolean isEmpty() { return contacts_ == null || contacts_.size() == 0; }
}
