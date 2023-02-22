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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/SecDNSKeyData.java,v 1.2 2007/11/19 20:25:45 asimbirt Exp $
 * $Revision: 1.2 $
 * $Date: 2007/11/19 20:25:45 $
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

public class SecDNSKeyData
{
    private int ds_flags_ = -1;
    private int ds_protocol_ = -1;
    private int ds_alg_ = -1;
    private String ds_pub_key_;

    public SecDNSKeyData () {}

    public void setFlags(int value) { ds_flags_ = value; }
    public int getFlags() { return ds_flags_; }

    public void setProtocol(int value) { ds_protocol_ = value; }
    public int getProtocol() { return ds_protocol_; }

    public void setAlg(int value) { ds_alg_ = value; }
    public int getAlg() { return ds_alg_; }

    public void setPubKey(String value) { ds_pub_key_ = value; }
    public String getPubKey() { return ds_pub_key_; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        Element e = doc.createElement("secDNS:keyData");

        if (ds_flags_ < 0) throw new epp_XMLException("secDNS:dsData.secDNS:flags invalid ("+ds_flags_+")");
        if (ds_protocol_ < 0) throw new epp_XMLException("secDNS:dsData.secDNS:protocol invalid ("+ds_protocol_+")");
        if (ds_alg_ < 0) throw new epp_XMLException("secDNS:dsData.secDNS:alg invalid ("+ds_alg_+")");
        if (ds_pub_key_ == null || ds_pub_key_.length() == 0) throw new epp_XMLException("secDNS:dsData.secDNS:pub_key invalid ("+ds_pub_key_+")");

        ExtUtils.addXMLElement(doc, e, "secDNS:flags", String.valueOf(ds_flags_));
        ExtUtils.addXMLElement(doc, e, "secDNS:protocol", String.valueOf(ds_protocol_));
        ExtUtils.addXMLElement(doc, e, "secDNS:alg", String.valueOf(ds_alg_));
        ExtUtils.addXMLElement(doc, e, "secDNS:pubKey", ds_pub_key_);

        return e;
    }

    public String toString()
    {
        return "[flags: " + ds_flags_ + "|protocol: " + ds_protocol_ + "|alg: " + ds_alg_ + "|pubKey: " + ds_pub_key_ + "]";
    }
}
