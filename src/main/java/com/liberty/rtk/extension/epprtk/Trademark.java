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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/Trademark.java,v 1.4 2009/08/25 20:37:54 dongjinkim Exp $
 * $Revision: 1.4 $
 * $Date: 2009/08/25 20:37:54 $
 */

package com.liberty.rtk.extension.epprtk;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.xerces.dom.DocumentImpl;

import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * This class is used to exchange Domain Trademark data with the
 * Liberty .info/.mobi/.in Registry.
 * There is not name space to specify which registry it was used for 
 * since in the begining there was only .info. Later on .mobi
 * introduced and different registry has different settings for 
 * trademark infomation, it has to be optional for some fields in 
 * trademark to adapt to different registry. Here is the list of 
 * required fields:
 *
 *   INFO
 *     <name/> <country/> <number/> <date/>
 *   MOBI
 *     <name/> <country/> <number/> <appDate/> <regDate/>
 *   IN
 *     <name/> <number/> <country/> <date/> <ownerCountry/>  
 * 
 * registrars are free to fill infomation in trademark and it is 
 * registry's responsibility to validate those infomation.
 *
 * The data should only be used in the OT&E environment and during
 * the live Sunrise period of the registry's operation.  During this
 * time the Registry will required the trademark data on domain creation.
 *
 * @see com.liberty.rtk.extension.epprtk.TrademarkData
 * @see com.liberty.rtk.extension.epprtk.example.TrademarkExample
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class Trademark extends EPPXMLBase implements epp_Extension
{
    private static final long serialVersionUID = 2190793827520811476L;

    private static final String prefix = "trademark:";

    private TrademarkData tm_data_;
    private String tm_cmd_;
    private String tm_tld_;

    /**
     * Default constructor
     */
    public Trademark () {}

    /**
     * Constructor with Domain Trademark Unspec XML string to automatically parse.
     * @param xml The EPP Domain Info response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public Trademark (String xml) throws epp_XMLException
    {
        String method_name = "Trademark(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    public void setTrademarkData(TrademarkData data) { tm_data_ = data; }
    /**
     * Accessor method for the trademark app_date data member.
     * @param value java.util.String
     */
    public TrademarkData getTrademarkData() { return tm_data_; }

    public void setCommand(String command)  { tm_cmd_ = command; }
    public String getCommand() { return tm_cmd_; }

    public void setTld(String tld) { tm_tld_ = tld; }
    public String getTld() { return tm_tld_; }
    /**
     * Converts the trademark data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( tm_tld_ == null )
        {
            throw new epp_XMLException("TLD name(mobi/info/in) needed");
        }
        if ( tm_cmd_ == null )
        {
            throw new epp_XMLException("trademark command");
        }

        if ( !"update".equals(tm_cmd_) && tm_data_.isRemove() )
        {
            throw new epp_XMLException("trademark remove element");
        }

        Document doc = new DocumentImpl();

        Element trademark = doc.createElement(prefix + tm_cmd_);
        setNamespace(trademark, tm_tld_);

        if (tm_data_.isRemove())
        {
            ExtUtils.addXMLElement(doc, trademark, prefix + "rem", null);
        }
        else 
        {
            if (tm_data_.getName() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "name", tm_data_.getName());
            if (tm_data_.getCountry() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "country", tm_data_.getCountry());
            if (tm_data_.getNumber() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "number", tm_data_.getNumber());
            if (tm_data_.getDate() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "date", tm_data_.getDate());
            if (tm_data_.getRegDate() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "regDate", tm_data_.getRegDate());
            if (tm_data_.getAppDate() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "appDate", tm_data_.getAppDate());
            if (tm_data_.getOwnerCountry() != null )
                ExtUtils.addXMLElement(doc, trademark, prefix + "ownerCountry", tm_data_.getOwnerCountry()); 
        }

        doc.appendChild( trademark );

        String trademark_xml;

        try
        {
            trademark_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return trademark_xml;
    }

    /**
     * Parses an XML String of trademark data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @param A new trademark Unspec XML String to parse
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
                // no trademark child elements
                throw new epp_XMLException("no trademark child elements");
            }

            tm_data_ = new TrademarkData();

            for (int count = 0; count < detail_node_list.getLength(); count++)
            {
                Node a_node = detail_node_list.item(count);
                String value = null;                
                if (a_node.getFirstChild() != null) value = a_node.getFirstChild().getNodeValue();
                
                if ( a_node.getNodeName().equals(prefix + "name") ) 
                { 
                    tm_data_.setName(value); 
                }
                if ( a_node.getNodeName().equals(prefix + "date") ) 
                { 
                    tm_data_.setDate(value); 
                }
                if ( a_node.getNodeName().equals(prefix + "appDate") ) 
                { 
                    tm_data_.setAppDate(value); 
                }
                if ( a_node.getNodeName().equals(prefix + "regDate") ) 
                { 
                    tm_data_.setRegDate(value); 
                }
                if ( a_node.getNodeName().equals(prefix + "country") ) 
                { 
                    tm_data_.setCountry(value); 
                }
                if ( a_node.getNodeName().equals(prefix + "number") ) 
                { 
                    tm_data_.setNumber(value); 
                }
                if ( a_node.getNodeName().equals(prefix + "ownerCountry") )
                {
                    tm_data_.setOwnerCountry(value);
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

    private static void setNamespace(Element element, String tld) throws epp_XMLException
    {
        String nameSpace = null;
        String xsdFile = null;

        element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        if ("mobi".equals(tld))
        {
            nameSpace = "urn:afilias:params:xml:ns:ext:mobi-trademark-1.0";
            xsdFile = "mobi-trademark-1.0.xsd";
        }
        else if ("info".equals(tld))
        {
            nameSpace = "urn:afilias:params:xml:ns:ext:info-trademark-1.0";
            xsdFile = "info-trademark-1.0.xsd";
        }
        else if ("in".equals(tld))
        {
            nameSpace = "urn:afilias:params:xml:ns:ext:in-trademark-1.0";
            xsdFile = "in-trademark-1.0.xsd";
        }
        else
        {
            throw new epp_XMLException("unsupported tld");
        }

        element.setAttribute("xmlns:trademark", nameSpace);
        element.setAttribute("xsi:schemaLocation", nameSpace + " " + xsdFile);
    }

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<trademark:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</trademark:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

		return xml;
	}
}
