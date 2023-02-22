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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/DomainTrademark.java,v 1.2 2006/03/01 21:19:52 ewang2004 Exp $
 * $Revision: 1.2 $
 * $Date: 2006/03/01 21:19:52 $
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
 * This class is used to exchange Domain Trademark data with the
 * Liberty .info/.in/.mobi Registry.
 * There is not name space to specify which registry it was used for 
 * since in the begining there was only .info. Later on as .in and .mobi
 * introduced and different registry has different settings for 
 * trademark infomation, it has to be optional for some fields in 
 * trademark to adapt to different registry. Here is the list of 
 * required fields:
 *
 *   INFO
 *     <name/> <country/> <number/> <date/>
 *   IN
 *     <name/> <country/> <ownerCountry/> <number/> <date/>
 *   MOBI
 *     <name/> <country/> <number/> <appDate/> <regDate/>
 * 
 * registrars are free to fill infomation in trademark and it is 
 * registry's responsibility to validate those infomation.
 *
 * The data should only be used in the OT&E environment and during
 * the live Sunrise period of the registry's operation.  During this
 * time the Registry will required the trademark data on domain creation.
 *
 * @see com.liberty.rtk.extension.epprtk.example.TrademarkExample
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class DomainTrademark extends EPPXMLBase implements epp_Extension
{

    private String tm_name_;
    private String tm_date_;
    private String tm_country_;
    private String tm_owner_country_;
    private String tm_number_;
    private String tm_app_date_;
    private String tm_reg_date_;

    /**
     * Default constructor
     */
    public DomainTrademark () {}

    /**
     * Constructor with Domain Trademark Unspec XML string to automatically parse.
     * @param xml The EPP Domain Info response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public DomainTrademark (String xml) throws epp_XMLException
    {
        String method_name = "DomainTrademark(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    /**
     * Accessor method for the trademark name data member.
     * @param value String
     */
    public void setName(String value) { tm_name_ = value; }
    /**
     * Accessor method for the trademark app_date data member.
     * @param value java.util.String
     */
    public void setAppDate(String value) { tm_app_date_ = value; }
    /**
     * Accessor method for the trademark reg_date data member.
     * @param value java.util.String
     */
    public void setRegDate(String value) { tm_reg_date_ = value; }
    /**
     * Accessor method for the trademark date data member.
     * @param value The new date value
     * @deprecated Please use setDate(String) instead.
     */
    public void setDate(Date value) { tm_date_ = value.toString(); }
    /**
     * Accessor method for the trademark date data member.
     * @param value java.util.String
     */
    public void setDate(String value) { tm_date_ = value; }
    /**
     * Accessor method for the trademark country data member.
     * Must be a two-character ISO country code.
     * @param value String
     */
    public void setCountry(String value) { tm_country_ = value; }
    /**
     * Accessor method for the trademark owner's country data member.
     * Must be a two-character ISO country code.
     * @param value String
     */
    public void setOwnerCountry(String value) { tm_owner_country_ = value; }
    /**
     * Accessor method for the trademark number data member.
     * @param value String
     */
    public void setNumber(String value) { tm_number_ = value; }
    /**
     * Accessor method for the trademark name data member.
     * @return The trademark name
     */
    public String getName() { return tm_name_; }
    /**
     * Accessor method for the trademark date data member.
     * Returns the date as a Date object.  If the date string
     * cannot be parsed into a date object, then null is returned.
     * @return The date as a Date object or null
     * @deprecated Please use getDateAsString() instead.
     */
    public Date getDate()
    {
        Date the_date;
        try { the_date = DATE_FMT.parse(tm_date_); }
        catch(ParseException xcp) { the_date = null; }
        return the_date;
    }
    /**
     * Accessor method for the trademark date data member.
     * @return The date as a String
     */
    public String getDateAsString() { return tm_date_; }
    /**
     * Accessor method for the trademark app_date data member.
     * @return The date as a String
     */
    public String getAppDateAsString() { return tm_app_date_; }
    /**
     * Accessor method for the trademark reg_date data member.
     * @return The date as a String
     */
    public String getRegDateAsString() { return tm_reg_date_; }
    /**
     * Accessor method for the trademark country data member.
     * @return value String
     */
    public String getCountry() { return tm_country_; }
    /**
     * Accessor method for the trademark owner's country data member.
     * @return value String
     */
    public String getOwnerCountry() { return tm_owner_country_; }
    /**
     * Accessor method for the trademark number data member.
     * @return value String
     */
    public String getNumber() { return tm_number_; }

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

        if ( tm_name_ == null )
        {
            throw new epp_XMLException("trademark name");
        }
        if ( tm_number_ == null )
        {
            throw new epp_XMLException("trademark number");
        }

        Document doc = new DocumentImpl();

        Element trademark = doc.createElement("trademark");

        addXMLElement(doc, trademark, "name", tm_name_);
        addXMLElement(doc, trademark, "number", tm_number_);
		addXMLElement(doc, trademark, "country", tm_country_);

		if ( tm_date_ != null)
		{
			addXMLElement(doc, trademark, "date", tm_date_);
		}
		if ( tm_app_date_ != null)
		{
			addXMLElement(doc, trademark, "appDate", tm_app_date_);
		}
		if ( tm_reg_date_ != null)
		{
			addXMLElement(doc, trademark, "regDate", tm_reg_date_);
		}
		if ( tm_owner_country_ != null)
		{
        	addXMLElement(doc, trademark, "ownerCountry", tm_number_);
		}

        doc.appendChild( trademark );

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

        xml_ = xml;

        try
        {

            tm_name_ = null;
            tm_date_ = null;
            tm_app_date_ = null;
            tm_reg_date_ = null;
            tm_country_ = null;
            tm_owner_country_ = null;
            tm_number_ = null;

            if ( xml_ == null ||
                 xml_.length() == 0 )
            {
                // no xml string to parse
                return;
            }

            /*
             * EPPRTK has a bug which assumes there can be mutiple <extension></extension>
             * elements in an command or response, so the xml snippet becomes <extension>
             * <trademark>< ...></trademark></extension>.
             */

            Element extension_node = getDocumentElement();

            if ( extension_node == null )
            {
                throw new epp_XMLException("unparsable or missing extension");
            }

            NodeList trademark_node_list = extension_node.getElementsByTagName("trademark");

            if ( trademark_node_list.getLength() == 0 )
            {
                // no trademark child elements
                throw new epp_XMLException("unparsable or missing trademark");
            }

            if ( trademark_node_list.getLength() > 1)
            {
                throw new epp_XMLException("more then one trademark element");
            }

            debug(DEBUG_LEVEL_TWO,method_name,"trademark_node_list's node count ["+trademark_node_list.getLength()+"]");
            Node trademark_node = trademark_node_list.item(0);

            NodeList detail_node_list = trademark_node.getChildNodes();

            if ( detail_node_list.getLength() == 0 ) {
                return;
            }

            for (int count = 0; count < detail_node_list.getLength(); count++)
            {
                Node a_node = detail_node_list.item(count);

                if ( a_node.getNodeName().equals("name") ) { tm_name_ = a_node.getFirstChild().getNodeValue(); }
                //if ( a_node.getNodeName().equals("date") ) { try { tm_date_ = DATE_FMT.parse(a_node.getFirstChild().getNodeValue());  } catch(ParseException xcp) { throw new epp_XMLException("bad trademark date format"); } }
                if ( a_node.getNodeName().equals("date") ) { tm_date_ = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("appDate") ) { tm_app_date_ = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("regDate") ) { tm_reg_date_ = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("country") ) { tm_country_ = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("ownerCountry") ) { tm_owner_country_ = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("number") ) { tm_number_ = a_node.getFirstChild().getNodeValue(); }
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

}
