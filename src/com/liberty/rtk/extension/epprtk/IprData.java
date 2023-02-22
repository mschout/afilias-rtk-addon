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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/IprData.java,v 1.2 2007/06/14 14:22:56 asimbirt Exp $
 * $Revision: 1.2 $
 * $Date: 2007/06/14 14:22:56 $
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

public class IprData
{
    private String ipr_name_;
    private String ipr_country_;
    private String ipr_number_;
    private String ipr_app_date_;
    private String ipr_reg_date_;
    private String ipr_class_;
    private String ipr_entitlement_;
    private String ipr_form_;
    private String ipr_type_;
    private String ipr_preVerified_;

    public void setName(String name) { ipr_name_ = name; }
    public String getName() { return ipr_name_; }

    public void setCountry(String country) { ipr_country_ = country; }
    public String getCountry() { return ipr_country_; }

    public void setNumber(String number) { ipr_number_ = number; }
    public String getNumber() { return ipr_number_; }

    public void setAppDate(String appDate) { ipr_app_date_ = appDate; }
    public String getAppDate() { return ipr_app_date_; }

    public void setRegDate(String regDate) { ipr_reg_date_ = regDate; }
    public String getRegDate() { return ipr_reg_date_; }

    public void setIprClass(String my_class) { ipr_class_ = my_class; }
    public String getIprClass() { return ipr_class_; }

    public void setEntitlement(String value) { ipr_entitlement_ = value; }
    public String getEntitlement() { return ipr_entitlement_; }

    public void setForm(String value) { ipr_form_ = value; }
    public String getForm() { return ipr_form_; }

    public void setType(String value) { ipr_type_ = value; }
    public String getType() { return ipr_type_; }

    public void setPreVerified(String value) { ipr_preVerified_ = value; }
    public String getPreVerified() { return ipr_preVerified_; }

    public String toString()
    {
        return "[name:" + ipr_name_ + "|ccLocality:" + ipr_country_ + "|number:" + ipr_number_  + "|regDate:" + ipr_reg_date_ + "|appDate:" + ipr_app_date_ + "|class:" + ipr_class_ + "|entitlement:" + ipr_entitlement_ + "|form:" + ipr_form_ + "|type:" + ipr_type_ + "|PreVerified:" + ipr_preVerified_ + "]";
    }
}
