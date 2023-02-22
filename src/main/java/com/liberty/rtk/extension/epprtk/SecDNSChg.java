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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/SecDNSChg.java,v 1.1 2005/09/22 17:13:32 fotsoft Exp $
 * $Revision: 1.1 $
 * $Date: 2005/09/22 17:13:32 $
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

public class SecDNSChg
{
    private Collection ds_data_coll_ = Collections.EMPTY_LIST; // SecDNSDsData

    public void setDsData(Collection value) { ds_data_coll_ = value; }
    public Collection getDsData() { return ds_data_coll_; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        if (ds_data_coll_ == null) throw new epp_XMLException("secDNS:update.secDNS:chg missing secDNS:dsData");
        Element e = doc.createElement("secDNS:chg");
        for (Iterator it = ds_data_coll_.iterator(); it.hasNext();) e.appendChild(((SecDNSDsData)it.next()).getElement(doc));
        return e;
    }

    public boolean isEmpty() { return ds_data_coll_ == null || ds_data_coll_.size() == 0; }
}
