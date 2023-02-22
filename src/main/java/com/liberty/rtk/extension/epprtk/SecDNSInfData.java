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

/**
 * @see com.liberty.rtk.extension.epprtk.example.SecDNSExample
 */
public class SecDNSInfData extends EPPXMLBase implements epp_Extension
{
    private Collection ds_data_ = Collections.EMPTY_LIST; // SecDNSDsData
    private int ds_max_sig_life_ = -1;


    public SecDNSInfData () {}

    public void setDsData(Collection value) { ds_data_ = value; }
    public Collection getDsData() { return ds_data_; }

    public void setMaxSigLife(int value) { ds_max_sig_life_ = value; }
    public int getMaxSigLife() { return ds_max_sig_life_; }


    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        return("Nothing to do");
    }



    /**
     * This would parse a response from the server with the <secDNS:infData> extension.
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @param A new oxrs transfer Extension XML String to parse
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
           if ( xml_ == null || xml_.length() == 0 )
            {
                // no xml string to parse
                debug(DEBUG_LEVEL_THREE,method_name,"No XML to parse");
                debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
                return;
            }

            Element secDns_node = getDocumentElement();

            if ( secDns_node == null )
            {
                return;
            }

            NodeList infData_node_list = secDns_node.getChildNodes();

            if ( infData_node_list.getLength() == 0 )
            {
                throw new epp_XMLException("missing info results");
            }

            ds_data_ = new ArrayList();
            for (int count = 0; count < infData_node_list.getLength(); count++)
            {
                Node a_node = infData_node_list.item(count);
                
                if (a_node.getNodeName().equals("secDNS:maxSigLife")) {
                    ds_max_sig_life_ = Integer.parseInt(a_node.getFirstChild().getNodeValue());	 
		}     
                if (a_node.getNodeName().equals("secDNS:dsData")) {
                   NodeList a_subNode = a_node.getChildNodes();

                   SecDNSDsData temp_ds_data = new SecDNSDsData();
                   for (int i = 0; i < a_subNode.getLength(); i++)
                   {
                       Node the_node = a_subNode.item(i);

                       if ( the_node.getNodeName().equals("secDNS:keyTag")) {
                            temp_ds_data.setKeyTag(Integer.parseInt(the_node.getFirstChild().getNodeValue()));         
                       }
                       if ( the_node.getNodeName().equals("secDNS:alg")) {
                            temp_ds_data.setAlg(Integer.parseInt(the_node.getFirstChild().getNodeValue()));
                       }
                       if ( the_node.getNodeName().equals("secDNS:digestType")) {
                            temp_ds_data.setDigestType(Integer.parseInt(the_node.getFirstChild().getNodeValue()));
                       }
                       if ( the_node.getNodeName().equals("secDNS:digest")) {
                            temp_ds_data.setDigest(the_node.getFirstChild().getNodeValue());
                       }
                       //if ( the_node.getNodeName().equals("secDNS:maxSigLife")) {
                       //     temp_ds_data.setMaxSigLife(Integer.parseInt(the_node.getFirstChild().getNodeValue()));
                       //}
                       if ( the_node.getNodeName().equals("secDNS:keyData")) {
                            SecDNSKeyData key_data = new SecDNSKeyData();

                            NodeList a_subNode_2 = the_node.getChildNodes(); 
                            for (int j = 0; j < a_subNode_2.getLength(); j++) {
                                Node the_node_2 = a_subNode_2.item(j);

                                if ( the_node_2.getNodeName().equals("secDNS:flags")) {
                                   key_data.setFlags(Integer.parseInt(the_node_2.getFirstChild().getNodeValue()));
                                }
                                if ( the_node_2.getNodeName().equals("secDNS:protocol")) {
                                   key_data.setProtocol(Integer.parseInt(the_node_2.getFirstChild().getNodeValue()));
                                }
                                if ( the_node_2.getNodeName().equals("secDNS:alg")) {
                                   key_data.setAlg(Integer.parseInt(the_node_2.getFirstChild().getNodeValue()));
                                }
                                if ( the_node_2.getNodeName().equals("secDNS:pubKey")) {
                                   key_data.setPubKey(the_node_2.getFirstChild().getNodeValue());
                                }
                            }
                            temp_ds_data.setKeyData(key_data); 
                       }

                   }
                   ds_data_.add(temp_ds_data);
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

    protected String getInnerXML(String xml)
    {
        if ( xml == null || xml.length() == 0 )
                return xml;

        int indexOfStart = xml.indexOf("<secDNS:");
        xml = xml.substring(indexOfStart);
        int indexOfEnd = xml.lastIndexOf("</secDNS:");
        int realIndexOfEnd = xml.indexOf(">", indexOfEnd);
        xml = xml.substring(0, realIndexOfEnd+1);

        return xml;
    }
}
