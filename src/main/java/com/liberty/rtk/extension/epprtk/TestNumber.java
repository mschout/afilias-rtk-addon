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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/TestNumber.java,v 1.1 2004/12/20 22:45:44 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/20 22:45:44 $
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

/**
 * This class is used to exchange Registrar Certification Test Number
 * data with the Liberty .info Registry.
 * The data should only be used in the OT&E environment and only during
 * a registrar cerfication test.
 * @see com.liberty.rtk.addon.example.TestNumberExample
 */
public class TestNumber extends EPPXMLBase implements epp_Extension
{

    private String test_num_;

    /**
     * Default constructor
     */
    public TestNumber () {}

    /**
     * Constructor with Test Number Unspec XML string to automatically parse.
     * @param xml The Test Number Unspec response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public TestNumber (String xml) throws epp_XMLException, epp_Exception
    {
        String method_name = "TestNumber(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    /**
     * Accessor method for the test number data member.
     * Normally in the format of "2.2.14"
     * @param value The test number
     */
    public void setTestNum(String value) { test_num_ = value; }
    /**
     * Accessor method for the test number data member.
     * @return value The test number
     */
    public String getTestNum() { return test_num_; }

    /**
     * Converts the test number data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Unspec
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( test_num_ == null )
        {
            throw new epp_XMLException("trademark name");
        }

        Document doc = new DocumentImpl();
        
        Element testnum = doc.createElement("testnum");
        
        testnum.appendChild( doc.createTextNode(test_num_) );

        doc.appendChild( testnum );
        
        String trademark_xml;
        
        try
        {
            trademark_xml = createXMLFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return trademark_xml;
    }

    /**
     * Parses an XML String of test number data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @param A new test number Unspec XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = xml;

        try
        {

            test_num_ = null;

            if ( xml_ == null ||
                 xml_.length() == 0 )
            {
                // no xml string to parse
                return;
            }
            
            Element testnum_node = getDocumentElement();

            if ( testnum_node == null )
            {
                throw new epp_XMLException("unparsable or missing testnum");
            }

            test_num_ = ((Element)testnum_node).getFirstChild().getNodeValue();

        }
        catch (SAXException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }
        catch (IOException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }

    }

}
