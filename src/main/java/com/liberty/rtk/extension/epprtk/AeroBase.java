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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/AeroBase.java,v 1.1 2006/01/13 16:29:31 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2006/01/13 16:29:31 $
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

public abstract class AeroBase extends EPPXMLBase implements epp_Extension
{

    private String command_;

    /**
     * Default constructor
     */
    public AeroBase () { }

    /**
     * Accessor method for the EPP command for this AeroBase extension.
     * Typical values are "create" and "check", though this is 
     * not validated.
     * @param value The new command name
     */
    public void setCommand(String value) 
	{ 
		command_ = value; 
	}
    /**
     * Accessor method for the EPP command for this AeroBase extension.
     * Typical values are "create".
     * @return value The current command name
     */
    public String getCommand() { return command_; }

	protected void setAeroAttribute(Element aero)
	{
        aero.setAttribute("xmlns:aero", "urn:ietf:params:xml:ns:aero-1.0");
        aero.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        aero.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:aero-1.0 aero-1.0.xsd");
	}

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<aero:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</aero:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

		return xml;
	}
}
