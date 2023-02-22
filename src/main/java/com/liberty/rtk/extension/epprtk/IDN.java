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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/IDN.java,v 1.3 2010/08/20 16:48:20 dongjinkim Exp $
 * $Revision: 1.3 $
 * $Date: 2010/08/20 16:48:20 $
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
 * IDN Helper class
 */
public class IDN extends EPPXMLBase implements epp_Extension
{
    private static final long serialVersionUID = 4647849386978702293L;
    
    private String command_;
    private String script_;
    private String reason_;
    private boolean chg_;

    // This option is only used to produce invalid IDN XML
    // when doing OTNE test,
    private boolean isOtne;

    /**
     * Default constructor
     */
    public IDN () { this(false); }

    /**
     * Do not initialize isOtne to be true unless you are doing 
     * otne and need to perform sending invalid IDN request
     * @param boolean to control if bypassing script tag when create XML
     */
    public IDN(boolean isOtne) 
    { 
        this.isOtne = isOtne;
    }

    /**
     * Accessor method for the EPP command for this IDN extension.
     * Typical values are "create" and "check", though this is 
     * not validated.
     * @param value The new command name
     */
    public void setCommand(String value) { command_ = value; }
    /**
     * Accessor method for the EPP command for this IDN extension.
     * Typical values are "create" and "check".
     * @return value The current command name
     */
    public String getCommand() { return command_; }

    /**
     * Accessor method for the IDN ISO locale/script.
     * Format is governed by RFC3066 (eg. "en-CA", "de", etc...)
     * @param value The new IDN script.
     */
    public void setScript(String value) { script_ = value; }
    /**
     * Accessor method for the IDN ISO locale/script.
     * Format is governed by RFC3066 (eg. "en-CA", "de", etc...)
     * @return value The current IDN script
     */
    public String getScript() { return script_; }

    /**
     * Accessor method for the error reason string
     * @param value The new error reason string.
     */
    public void setReason(String value) { reason_ = value; }
    /**
     * Accessor method for the error reason string
     * @return value The current error reason string
     */
    public String getReason() { return reason_; }

    public void setChg(boolean value) { chg_ = value; }
    
    public boolean getChg() { return chg_; }

    /**
     * Renders the IDN extension "extension" for EPP 02.<br>
     * This qualifies the punycode name of the domain(s) in 
     * domain:create and domain:check commands.
     * Implemented method from org.openrrc.rtk.epprtk.epp_Unspec interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        if ( !isOtne && (script_ == null || script_.equals("")) ) 
        {
            throw new epp_XMLException("missing locale/script for idn:"+command_+" extension ");
        } 

        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( command_ == null ||
             command_.equals("") )
        {
            throw new epp_XMLException("missing epp command for idn extension");
        }

        Document doc = new DocumentImpl();
        
        Element idn = doc.createElement("idn:"+command_);
        idn.setAttribute("xmlns:idn", "urn:ietf:params:xml:ns:idn-1.0");
        idn.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        idn.setAttribute("xsi:schemaLocation", "urn:ietf:params:xml:ns:idn-1.0 idn-1.0.xsd");
        
        Element change_element = null;
        
		// create idn Script Element
		Element idnScript = doc.createElement("idn:script");
		if ( script_ != null && script_.length() != 0 )
		{
			idnScript.appendChild( doc.createTextNode(script_) );
		}

		if (command_.equals("update")) 
		{
			if (getChg()) {
			    change_element = doc.createElement("idn:chg");            
			    ExtUtils.addXMLElement(doc, change_element, "idn:script", script_);
			    
			    if (change_element != null) {
			        idn.appendChild(change_element);
			    }
			}
		}
		else
		{
			idn.appendChild( idnScript );
		}

        doc.appendChild( idn );
        
        String idn_xml;
        
        try
        {
            idn_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building oxrs:transfer XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"The IDN extension XML is: ["+idn_xml+"]");
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return idn_xml;
    }

    /**
     * This method is used to parse the IDN extension contained in an EPP response.<br>
     * @param A new IDN response extension XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        int indexOfStart = xml.indexOf("<idn:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</idn:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

        xml_ = xml;
        
        try
        {

            command_ = null;
            script_  = null;
            reason_  = null;

            if ( xml_ == null ||
                 xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element idn_node = getDocumentElement();

            if ( idn_node == null )
            {
                // XXX should we throw an exception at this point???
                return;
            }

            command_ = idn_node.getNodeName();
            command_ = command_.replaceFirst("^[^:]+:","");

            NodeList idn_node_list = idn_node.getChildNodes();

            if ( idn_node_list.getLength() == 0 )
            {
                // XXX should we throw an exception at this point???
                return;
            }

            debug(DEBUG_LEVEL_TWO,method_name,"idn_node_list's node count ["+idn_node_list.getLength()+"]");

            for (int count = 0; count < idn_node_list.getLength(); count++)
            {
                Node a_node = idn_node_list.item(count);

                if ( a_node.getNodeName().equals("idn:script") ) {
                    script_ = a_node.getFirstChild().getNodeValue();
                }
                
                if ( a_node.getNodeName().equals("idn:reason") ) {
                    reason_ = a_node.getFirstChild().getNodeValue();
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

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

    }

}
