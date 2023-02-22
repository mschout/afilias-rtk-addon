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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/FeeData.java,v 1.2 2007/06/14 14:22:56 asimbirt Exp $
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

public class FeeData
{
    private String fee_name_;
    private String fee_command_;
    private String fee_currency_;
    private String fee_period_;
    private String fee_fee_;
    private String fee_class_;

    public void setName(String name) { fee_name_ = name; }
    public String getName() { return fee_name_; }

    public void setCommand(String command) { fee_command_ = command; }
    public String getCommand() { return fee_command_; }

    public void setCurrency(String currency) { fee_currency_ = currency; }
    public String getCurrency() { return fee_currency_; }

    public void setPeriod(String period) { fee_period_ = period; }
    public String getPeriod() { return fee_period_; }

    public void setFee(String fee) { fee_fee_ = fee; }
    public String getFee() { return fee_fee_; }

    public void setClasses(String classes) { fee_class_ = classes; }
    public String getClasses() { return fee_class_; }
    
    public String toString()
    {
        return "[name:" + fee_name_ + "|currency:" + fee_currency_ + "|command:" + fee_command_ + "|fee:" + fee_fee_ + "]";
    }
}
