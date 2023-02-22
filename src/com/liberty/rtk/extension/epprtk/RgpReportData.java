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

public class RgpReportData
{
    private String rpg_pre_data_;
    private String rpg_post_data_;
    private String rpg_del_time_;
    private String rpg_res_time_;
    private String rpg_res_reason_;
    private String rpg_statement_1_;
    private String rpg_statement_2_;
    private String rpg_other_;

    public void setPreData(String value) { rpg_pre_data_ = value; }
    public String getPreData() { return rpg_pre_data_; }

    public void setPostData(String value) { rpg_post_data_ = value; }
    public String getPostData() { return rpg_post_data_; }

    public void setDelTime(String value) { rpg_del_time_ = value; }
    public String getDelTime() { return rpg_del_time_; }

    public void setResTime(String value) { rpg_res_time_ = value; }
    public String getResTime() { return rpg_res_time_; }

    public void setResReason(String value) { rpg_res_reason_ = value; }
    public String getResReason() { return rpg_res_reason_; }

    public void setStatement1(String value) { rpg_statement_1_ = value; }
    public String getStatement1() { return rpg_statement_1_; }

    public void setStatement2(String value) { rpg_statement_2_ = value; }
    public String getStatement2() { return rpg_statement_2_; }

    public void setOther(String value) { rpg_other_ = value; }
    public String getOther() { return rpg_other_; }
    
    public String toString()
    {
        return "[preData:" + rpg_pre_data_ + "|postData:" + rpg_post_data_ +"|delTime:" + rpg_del_time_ + "|resTime:" + rpg_res_time_ + "|resReason:" + rpg_res_reason_  + "|statement:" + rpg_statement_1_ + "|statement:" + rpg_statement_2_ + "|other:" + rpg_other_ + "]";
    }
}
