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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/Ipr.java,v 1.5 2010/08/13 19:43:03 dongjinkim Exp $
 * $Revision: 1.5 $
 * $Date: 2010/08/13 19:43:03 $
 */

package com.liberty.rtk.extension.epprtk;

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * This class is used to exchange Ipr data.
 *
 * @see com.liberty.rtk.extension.epprtk.IprData
 * @see com.liberty.rtk.extension.epprtk.example.IprSessionExample
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class Ipr extends EPPXMLBase implements epp_Extension
{
    private static final long serialVersionUID = 7303436587391795380L;

    private static final String prefix = "ipr:";

    private IprData tm_data_;
    private String command_;
    private String tm_tld_;
    private boolean chg_;
    private boolean rem_;
    
    /**
     * Default constructor
     */
    public Ipr () {}

    /**
     * Constructor with Domain Ipr Unspec XML string to automatically parse.
     * @param xml The EPP Domain Info response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public Ipr (String xml) throws epp_XMLException
    {
        String method_name = "Ipr(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    public void setIprData(IprData data) { tm_data_ = data; }
    /**
     * Accessor method for the ipr app_date data member.
     * @param value java.util.String
     */
    public IprData getIprData() { return tm_data_; }

    public void setCommand(String command)  { command_ = command; }
    public String getCommand() { return command_; }

    public void setTld(String tld)  { tm_tld_ = tld; }
    public String getTld() { return tm_tld_; }

    public void setChg(boolean chg) { chg_ = chg; }
    public boolean getChg() { return chg_; }
    
    public void setRem(boolean rem) { rem_ = rem; }
    public boolean getRem() { return rem_; }
    
    /**
     * Converts the ipr data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( command_ == null )
        {
            throw new epp_XMLException("ipr command");
        }

        Document doc = new DocumentImpl();

        Element ipr = doc.createElement(prefix + command_);
        setNamespace(ipr, tm_tld_);

        if (getCommand().equals("create")) {
            addIPRData(doc, ipr, tm_data_);
        }
        else if (getCommand().equals("update")) {
            if (getChg()) { 
                Element change_element = doc.createElement("ipr:chg");
                addIPRData(doc, change_element, tm_data_);
                ipr.appendChild(change_element);
            }
            else if (getRem()) {
                Element remove_element = doc.createElement("ipr:rem");
                ipr.appendChild(remove_element);
            }
        }
        
        doc.appendChild( ipr );

        String ipr_xml;

        try
        {
            ipr_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return ipr_xml;
    }

    /**
     * Parses an XML String of ipr data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @param A new ipr Unspec XML String to parse
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

            /*
             * EPPRTK has a bug which assumes there can be mutiple <extension></extension>
             * elements in an command or response, so the xml snippet becomes <extension>
             * <ipr>< ...></ipr></extension>.
             */

            Element extension_node = getDocumentElement();

            if ( extension_node == null )
            {
                throw new epp_XMLException("unparsable or missing extension");
            }

            NodeList detail_node_list = extension_node.getChildNodes();

            debug(DEBUG_LEVEL_TWO,method_name,"detail_node_list's node count ["+detail_node_list.getLength()+"]");

            if ( detail_node_list.getLength() == 0 )
            {
                // no ipr child elements
                throw new epp_XMLException("no ipr child elements");
            }

            tm_data_ = new IprData();

            for (int count = 0; count < detail_node_list.getLength(); count++)
            {
                Node a_node = detail_node_list.item(count);

                if ( a_node.getNodeName().equals(prefix + "name") ) 
                { 
                    tm_data_.setName( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "appDate") ) 
                { 
                    tm_data_.setAppDate( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "regDate") ) 
                { 
                    tm_data_.setRegDate( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "ccLocality") ) 
                { 
                    tm_data_.setCountry( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "number") ) 
                { 
                    tm_data_.setNumber( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "class") )
                {
                    tm_data_.setIprClass( a_node.getFirstChild().getNodeValue() );
                }
                if ( a_node.getNodeName().equals(prefix + "entitlement") )
                {
                    tm_data_.setEntitlement( a_node.getFirstChild().getNodeValue() );
                }
                if ( a_node.getNodeName().equals(prefix + "form") )
                {
                    tm_data_.setForm( a_node.getFirstChild().getNodeValue() );
                }
                if ( a_node.getNodeName().equals(prefix + "type") )
                {
                    tm_data_.setType( a_node.getFirstChild().getNodeValue() );
                }
                if ( a_node.getNodeName().equals(prefix + "preVerified") )
                {
                    tm_data_.setPreVerified( a_node.getFirstChild().getNodeValue() );
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
        if (tld.equals("asia")) {
            element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            element.setAttribute("xmlns:ipr", "urn:afilias:params:xml:ns:ipr-1.0");
            element.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:ipr-1.0 ipr-1.0.xsd");
        } else {
            element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            element.setAttribute("xmlns:ipr", "urn:afilias:params:xml:ns:ipr-1.1");
            element.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:ipr-1.1 ipr-1.1.xsd");
        } 
    }

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<ipr:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</ipr:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

		return xml;
	}

	private void addIPRData(Document doc, Element node, IprData data) throws epp_XMLException 
	{
        if (tm_data_.getName() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "name", data.getName());
        if (tm_data_.getCountry() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "ccLocality", data.getCountry());
        if (tm_data_.getNumber() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "number", data.getNumber());
        if (tm_data_.getRegDate() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "regDate", data.getRegDate());
        if (tm_data_.getAppDate() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "appDate", data.getAppDate());
        if (tm_data_.getIprClass() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "class", data.getIprClass());
        if (tm_data_.getEntitlement() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "entitlement", data.getEntitlement());
        if (tm_data_.getForm() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "form", data.getForm());
        if (tm_data_.getType() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "type", data.getType());
        if (tm_data_.getPreVerified() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "preVerified", data.getPreVerified());
    }
}
