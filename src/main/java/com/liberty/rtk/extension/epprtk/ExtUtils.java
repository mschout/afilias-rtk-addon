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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/ExtUtils.java,v 1.1 2005/09/22 17:24:57 fotsoft Exp $
 * $Revision: 1.1 $
 * $Date: 2005/09/22 17:24:57 $
 */

package com.liberty.rtk.extension.epprtk;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

public class ExtUtils
{
    public static Element addXMLElement(Document doc, Element containing_element, String tag_name, String value)
    {
        Element xml_element = doc.createElement(tag_name);
        if ( value != null && value.length() != 0 )
        {
            xml_element.appendChild( doc.createTextNode(value) );
        }
        containing_element.appendChild( xml_element );
        return xml_element;
    }
    public static Element addXMLElementWithAttribute(Document doc, Element containing_element, String tag_name, String value, String attribute, String attribute_value)
    {
        Element xml_element = doc.createElement(tag_name);
        if ( value != null && value.length() != 0 )
        {	
        	xml_element.setAttribute(attribute, attribute_value);
            xml_element.appendChild( doc.createTextNode(value) );
        }
        containing_element.appendChild( xml_element );
        return xml_element;
    }
}
