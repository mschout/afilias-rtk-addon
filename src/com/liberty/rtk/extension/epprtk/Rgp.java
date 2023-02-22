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

/**
 * This class is used to exchange Rgp data.
 *
 * @see com.liberty.rtk.extension.epprtk.example.RgpSessionExample
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class Rgp extends EPPXMLBase implements epp_Extension
{
    private static final String prefix = "rgp:";

    private String tm_cmd_;
    private String restore_op_;
    private RgpReportData report_data_;
    private List rgp_statuses_;

    /**
     * Default constructor
     */
    public Rgp () {}

    /**
     * Constructor with Domain Rgp Unspec XML string to automatically parse.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public Rgp (String xml) throws epp_XMLException
    {
        String method_name = "Rgp(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    public void setRgpReportData(RgpReportData data) { report_data_ = data; }
    public RgpReportData getReportData() { return report_data_; }

    public void setCommand(String command)  { tm_cmd_ = command; }
    public String getCommand() { return tm_cmd_; }

    public void setRestoreOp(String value)  { restore_op_ = value; }
    public String getRestoreOp() { return restore_op_; }

    public void setRgpStatus(List value)  { rgp_statuses_ = value; }
   
    public void setRgpStatus(String value) { 
        if (rgp_statuses_ == null ) rgp_statuses_ = (List) new ArrayList();
        rgp_statuses_.add(value);
    }
    public List getRgpStatus() { return rgp_statuses_; }

    /**
     * Converts the rgp data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( tm_cmd_ == null  || !"update".equals(tm_cmd_))
        {
            throw new epp_XMLException("rgp command");
        }

        if ( restore_op_ == null) {
            throw new epp_XMLException("No restore option");
        } 

        Document doc = new DocumentImpl();

        Element rgp = doc.createElement(prefix + tm_cmd_);
        setNamespace(rgp);

        Element e = ExtUtils.addXMLElement(doc, rgp, prefix + "restore", null);
        e.setAttribute("op", restore_op_);
        
        if (report_data_ != null) {
           Element report = doc.createElement(prefix + "report");       

           if (report_data_.getPreData() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "preData", report_data_.getPreData());
           if (report_data_.getPostData() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "postData", report_data_.getPostData());
           if (report_data_.getDelTime() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "delTime", report_data_.getDelTime());
           if (report_data_.getResTime() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "resTime", report_data_.getResTime());  
           if (report_data_.getResReason() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "resReason", report_data_.getResReason());      
           if (report_data_.getStatement1() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "statement", report_data_.getStatement1()); 
           if (report_data_.getStatement2() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "statement", report_data_.getStatement2());
           if (report_data_.getOther() != null )
                ExtUtils.addXMLElement(doc, report, prefix + "other", report_data_.getOther());

           if (restore_op_.equals("request")) 
                rgp.appendChild(report);
           else 
                e.appendChild(report);
        }

        doc.appendChild( rgp );

        String rgp_xml;

        try
        {
            rgp_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return rgp_xml;
    }

    /**
     * Parses an XML String of rgp data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @param A new rgp Unspec XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = getInnerXML(xml);

        try
        {
            if ( xml_ == null ||
                 xml_.length() == 0 )
            {
                // no xml string to parse
                return;
            }

            Element extension_node = getDocumentElement();

            if ( extension_node == null )
            {
                throw new epp_XMLException("unparsable or missing extension");
            }

            NodeList detail_node_list = extension_node.getChildNodes();

            debug(DEBUG_LEVEL_TWO,method_name,"detail_node_list's node count ["+detail_node_list.getLength()+"]");

            if ( detail_node_list.getLength() == 0 )
            {
                // no rgp child elements
                throw new epp_XMLException("no rgp child elements");
            }

            for (int count = 0; count < detail_node_list.getLength(); count++)
            {
                Node a_node = detail_node_list.item(count);

                if ( a_node.getNodeName().equals(prefix + "rgpStatus") ) 
                { 
                    setRgpStatus(((Element)a_node).getAttribute("s")); 
                }
            }

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

    private static void setNamespace(Element element) throws epp_XMLException
    {
        String nameSpace = null;
        String xsdFile = null;

        element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        element.setAttribute("xmlns:rgp", "urn:ietf:params:xml:ns:rgp-1.0");
        element.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:rgp-1.0 rgp-1.0.xsd");
    }

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<rgp:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</rgp:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

		return xml;
	}
}
