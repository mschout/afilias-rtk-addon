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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/TrademarkData.java,v 1.2 2008/01/02 19:02:51 asimbirt Exp $
 * $Revision: 1.2 $
 * $Date: 2008/01/02 19:02:51 $
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

public class TrademarkData
{
    private String tm_name_;
    private String tm_country_;
    private String tm_number_;
    private String tm_date_;
    private String tm_app_date_;
    private String tm_reg_date_;
    private String tm_owner_country_;

    private boolean remove = false;

    public void setName(String name) { tm_name_ = name; }
    public String getName() { return tm_name_; }

    public void setCountry(String country) { tm_country_ = country; }
    public String getCountry() { return tm_country_; }

    public void setNumber(String number) { tm_number_ = number; }
    public String getNumber() { return tm_number_; }

    public void setDate(String date) { tm_date_ = date; }
    public String getDate() { return tm_date_; }

    public void setAppDate(String appDate) { tm_app_date_ = appDate; }
    public String getAppDate() { return tm_app_date_; }

    public void setRegDate(String regDate) { tm_reg_date_ = regDate; }
    public String getRegDate() { return tm_reg_date_; }

    public void setOwnerCountry(String owner_country) { tm_owner_country_ = owner_country; }
    public String getOwnerCountry() { return tm_owner_country_; }

    public void setRemove(boolean remove) { this.remove = remove; }
    public boolean isRemove() { return remove; }

    public String toString()
    {
        return "[remove:" + remove + "|name:" + tm_name_ + "|country:" + tm_country_ + "|number:" + tm_number_ + "|date:" + tm_date_ + "|regDate:" + tm_reg_date_ + "|appDate:" + tm_app_date_ + "|ownerCountry:" + tm_owner_country_;
    }
}
