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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/SecDNSDsData.java,v 1.2 2007/11/19 20:25:45 asimbirt Exp $
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

public class SecDNSDsData
{
    private int ds_key_tag_ = -1;
    private int ds_alg_ = -1;
    private int ds_digest_type_ = -1;
    private String ds_digest_;
    private SecDNSKeyData ds_key_data_;

    public SecDNSDsData () {}

    public void setKeyTag(int value) { ds_key_tag_ = value; }
    public int getKeyTag() { return ds_key_tag_; }

    public void setAlg(int value) { ds_alg_ = value; }
    public int getAlg() { return ds_alg_; }

    public void setDigestType(int value) { ds_digest_type_ = value; }
    public int getDigestType() { return ds_digest_type_; }

    public void setDigest(String value) { ds_digest_ = value; }
    public String getDigest() { return ds_digest_; }

    public void setKeyData(SecDNSKeyData value) { ds_key_data_ = value; }
    public SecDNSKeyData getKeyData() { return ds_key_data_; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        Element e = doc.createElement("secDNS:dsData");

        if (ds_key_tag_ < 0) throw new epp_XMLException("secDNS:dsData.secDNS:keyTag invalid ("+ds_key_tag_+")");
        if (ds_alg_ < 0) throw new epp_XMLException("secDNS:dsData.secDNS:alg invalid ("+ds_alg_+")");
        if (ds_digest_type_ < 0) throw new epp_XMLException("secDNS:dsData.secDNS:digestType invalid ("+ds_digest_type_+")");
        if (ds_digest_ == null || ds_digest_.length() == 0) throw new epp_XMLException("secDNS:dsData.secDNS:digest invalid ("+ds_digest_+")");

        ExtUtils.addXMLElement(doc, e, "secDNS:keyTag", String.valueOf(ds_key_tag_));
        ExtUtils.addXMLElement(doc, e, "secDNS:alg", String.valueOf(ds_alg_));
        ExtUtils.addXMLElement(doc, e, "secDNS:digestType", String.valueOf(ds_digest_type_));
        ExtUtils.addXMLElement(doc, e, "secDNS:digest", ds_digest_);

        if (ds_key_data_ != null) e.appendChild(ds_key_data_.getElement(doc));

        return e;
    }

    public String toString()
    {
        return "[keyTag: " + ds_key_tag_ + "|alg: " + ds_alg_ + "|digestType: " + ds_digest_type_ + "|digest: " + ds_digest_ +  "|keyData: " + ds_key_data_ +  "]";  
    }
}
