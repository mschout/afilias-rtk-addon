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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Pricing Helper class
 */
public class IDValidation extends EPPXMLBase implements epp_Extension
{	
	public class IDValidationCommonData
	{
		public String claimId;

		public String getClaimId() {
			return claimId;
		}

		public void setClaimId(String claimId) {
			this.claimId = claimId;
		}

		@Override
		public String toString() {
			return "IDValidationCommonData [claimId=" + claimId + "]";
		}		
	}
	
	public class IDValidationCreateData extends IDValidationCommonData
	{
	}
	
	public class IDValidationUpdateData extends IDValidationCommonData
	{
	}
	
	private static final long serialVersionUID = 7817397127499631344L;

	private String _command;
    private Object _validationData;

    /**
     * Default constructor
     */
    public IDValidation()
    {
    }

    /**
     * Accessor method for the EPP command for this custom extension.
     * Typical values are "create" and "update", though this is 
     * not validated.
     * @param value The new command name
     */
    public void setCommand(String value) { _command = value; }
    /**
     * Accessor method for the EPP command for this custom extension.
     * Typical values are "create" and "update".
     * @return value The current command name
     */
    public String getCommand() { return _command; }

    /**
     * Accessor method for the validation data
     * @return value The current IDN script
     */
    public Object getValidationData() { return _validationData; }

    /**
	 * @throws org.openrtk.idl.epprtk.epp_XMLException if operation is not update
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        final String method_name = "toXML()";

    	String rc = null;
    	
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( _command == null || _command.equals("") )
        {
        	throw new epp_XMLException("missing validation command for IDValidation extension");
        }

        Document doc = new DocumentImpl();

        Element validation = doc.createElement("validation:"+_command);	
        validation.setAttribute("xmlns:validation", "urn:afilias:params:xml:ns:validation-1.0");
        validation.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        validation.setAttribute("xsi:schemaLocation", "urn:afilias:params:xml:ns:validation-1.0 validation-1.0.xsd");

   		if (_command.equals("update")) 
   		{
   			Element change_element = doc.createElement("validation:chg");            
   			ExtUtils.addXMLElement(doc, change_element, "validation:ownership", null);

   			if (change_element != null) {
   				validation.appendChild(change_element);
   			}
   		}
   		else
   		{
   			throw new epp_XMLException("unsupported epp command for ID validation extension");
   		}

   		doc.appendChild(validation);

   		try
   		{
   			rc = createXMLSnippetFromDoc(doc);
   		}
   		catch (IOException xcp)
   		{
   			throw new epp_XMLException("IOException in building oxrs:transfer XML [" + xcp.getMessage() + "]");
   		}

   		debug(DEBUG_LEVEL_THREE,method_name,"The ID Validiation extension XML is: [" + rc + "]");

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return rc;
    }

    /**
     * This method is used to parse the custom extension contained in an EPP response.<br>
     * @param A new IDN response extension XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";

        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        int indexOfStart = xml.indexOf("<validation:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</validation:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

        xml_ = xml;
        
        try
        {
            _command = null;
            _validationData  = null;

            if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element node = getDocumentElement();

            if ( node == null )
            {
                return;
            }
            
            debug(DEBUG_LEVEL_THREE,method_name,node.getNodeName() + " = " + node.getFirstChild().getNodeValue());

            if (node.getNodeName().equalsIgnoreCase("validation:creData"))
            {
            	_command = "createDomain";
            	this.parseCreateData(node);
            }
            else if (node.getNodeName().equalsIgnoreCase("validation:updData"))
            {
            	_command = "updateDomain";
            	this.parseUpdateData(node);
            }
            else
            {
            	_command = "Error";
            	return;
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
    
    protected void debugPrintAttributes(Node node)
    {
        String method_name = "debugPrintAttributes()";

        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        NamedNodeMap attrs = node.getAttributes();

		for (int i = 0; i < attrs.getLength(); i++) 
		{
			Node a = attrs.item(i);
			debug(DEBUG_LEVEL_THREE,method_name," " + a.getNodeName() + "='" + a.getNodeValue() + "'");
		}                        	

		debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
    }
    
    public String parseClaimId(Node node) throws epp_XMLException
    {
    	String rc = null;
    	
        String method_name = "parseClaimid()";

        debug(DEBUG_LEVEL_THREE,method_name,"Entered");
    
        if (node.getNodeName().equalsIgnoreCase("validation:claimID")) 
		{
        	String claimId = node.getFirstChild().getNodeValue();
        	
        	rc = new String(claimId);
		}

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
        
        return rc;
    }

    public void parseCreateData(Element node) throws epp_XMLException
    {
        String method_name = "parseCreateData()";

        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        Node child_node = node.getFirstChild();

        if ( child_node == null )
        {
            return;
        }

		IDValidationCreateData validationCreateData = new IDValidationCreateData();
		
		if (child_node.getNodeName().equalsIgnoreCase("validation:claimID")) 
		{
			String claimId = parseClaimId(child_node);
			
			validationCreateData.setClaimId(claimId);
		}
        
		debug(DEBUG_LEVEL_TWO, method_name, "validationCreateData = " + validationCreateData);
		
		this._validationData = validationCreateData;

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
    }

    public void parseUpdateData(Element node) throws epp_XMLException
    {
        String method_name = "parseUpdateData()";

        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        Node child_node = node.getFirstChild();

        if ( child_node == null )
        {
            return;
        }

		IDValidationUpdateData validationUpdateData = new IDValidationUpdateData();

		if (child_node.getNodeName().equalsIgnoreCase("validation:claimID")) 
		{
			String domain = parseClaimId(child_node);
			
			validationUpdateData.setClaimId(domain);
		}
        
		debug(DEBUG_LEVEL_TWO, method_name, "validationUpdateData = " + validationUpdateData);
		
		this._validationData = validationUpdateData;
        
        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
    }
    
}
