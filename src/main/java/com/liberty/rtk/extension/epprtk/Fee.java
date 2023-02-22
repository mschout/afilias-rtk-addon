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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/Fee.java,v 1.5 2010/08/13 19:43:03 dongjinkim Exp $
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
 * This class is used to exchange Fee data.
 *
 * @see com.liberty.rtk.extension.epprtk.FeeData
 * @see com.liberty.rtk.extension.epprtk.example.FeeSessionExample
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class Fee extends EPPXMLBase implements epp_Extension
{
    private static final long serialVersionUID = 7303436587391795381L;

    private static final String prefix = "fee:";

    private FeeData tm_data_;
    private String command_;
    private String tm_tld_;
    private boolean chg_;
    private boolean rem_;
    
    /**
     * Default constructor
     */
    public Fee () {}

    /**
     * Constructor with Domain Fee Unspec XML string to automatically parse.
     * @param xml The EPP Domain Info response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see #fromXML(String)
     */
    public Fee (String xml) throws epp_XMLException
    {
        String method_name = "Fee(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    public void setFeeData(FeeData data) { tm_data_ = data; }
    /**
     * Accessor method for the fee app_date data member.
     * @param value java.util.String
     */
    public FeeData getFeeData() { return tm_data_; }

    public void setCommand(String command)  { command_ = command; }
    public String getCommand() { return command_; }

    public void setTld(String tld)  { tm_tld_ = tld; }
    public String getTld() { return tm_tld_; }

    public void setChg(boolean chg) { chg_ = chg; }
    public boolean getChg() { return chg_; }
    
    public void setRem(boolean rem) { rem_ = rem; }
    public boolean getRem() { return rem_; }
    
    /**
     * Converts the fee data into XML to be put into the extension
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
            throw new epp_XMLException("fee command");
        }

        Document doc = new DocumentImpl();

        Element fee = doc.createElement(prefix + command_);
        setNamespace(fee, tm_tld_);
        if (getCommand().equals("create")) {
            addFEEData(doc, fee, tm_data_);
        }
        
        else if (getCommand().equals("check")){
        
            Element check_element = doc.createElement("fee:domain");
            addFEEData(doc, check_element, tm_data_);
            fee.appendChild(check_element);
        }
        
        
        doc.appendChild( fee );

        String fee_xml;
        
        try
        {
            fee_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        
        return fee_xml;
    }

    /**
     * Parses an XML String of fee data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Unspec interface.
     * @param A new fee Unspec XML String to parse
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
             * <fee>< ...></fee></extension>.
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
                // no fee child elements
                throw new epp_XMLException("no fee child elements");
            }

            tm_data_ = new FeeData();

            for (int count = 0; count < detail_node_list.getLength(); count++)
            {
                Node a_node = detail_node_list.item(count);

                if ( a_node.getNodeName().equals(prefix + "name") ) 
                { 
                    tm_data_.setName( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "command") ) 
                { 
                    tm_data_.setCommand( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "currency") ) 
                { 
                    tm_data_.setCurrency( a_node.getFirstChild().getNodeValue() ); 
                }
                if ( a_node.getNodeName().equals(prefix + "fee") ) 
                { 
                    tm_data_.setFee( a_node.getFirstChild().getNodeValue() ); 
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
        //element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        element.setAttribute("xmlns:fee", "urn:afilias:params:xml:ns:fee-0.8");
        //element.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:fee-0.7 fee-0.7.xsd");
    }

	protected String getInnerXML(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return xml;

        int indexOfStart = xml.indexOf("<fee:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</fee:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);
        
		return xml;
	}

	private void addFEEData(Document doc, Element node, FeeData data) throws epp_XMLException 
	{
        if (tm_data_.getName() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "name", data.getName());
        if (tm_data_.getCurrency() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "currency", data.getCurrency());
        if (tm_data_.getCommand() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "command", data.getCommand());
        if (tm_data_.getFee() != null )
             ExtUtils.addXMLElement(doc, node, prefix + "fee", data.getFee());
    }
}
