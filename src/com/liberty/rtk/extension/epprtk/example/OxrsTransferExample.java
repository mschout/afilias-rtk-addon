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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/OxrsTransferExample.java,v 1.1 2004/12/20 22:46:17 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/20 22:46:17 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.liberty.rtk.extension.epprtk.OxrsTransfer;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;


/**
 * Example code for Liberty's Oxrs Transfer add-on to the RTK.
 * This data is needed to transfer a domain from an RRP-based registrar
 * to an EPP-based one.
 *
 * @author Daniel Manley
 * @version $Revision: 1.1 $ $Date: 2004/12/20 22:46:17 $
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainTransfer
 */
public class OxrsTransferExample
{

    private static String USAGE = "Usage: com.liberty.rtk.addon.example.OxrsTransferExample epp_host_name epp_host_port epp_client_id epp_password";

    /**
     * Main of the example.
     * Performs Domain transfer request command to show
     * the usage of the OxrsTransfer class.
     */
    public static void main(String args[])
    {

        System.out.println("Start of the Oxrs:Transfer example");

        epp_Command command_data = null;
	
        try
        {
            if (args.length != 4)
            {
                System.err.println(USAGE);
                System.exit(1);
            }

            RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_ONE);

            String epp_host_name = args[0];
            String epp_host_port_string = args[1];
            String epp_client_id = args[2];
            String epp_password  = args[3];

            int epp_host_port = Integer.parseInt(epp_host_port_string);

            EPPClient epp_client = new EPPClient(epp_host_name,
                                                 epp_host_port,
                                                 epp_client_id,
                                                 epp_password);
            
            epp_client.setLang("en");

            // The protocol used is set by the rtk.transport property
            // in etc/rtk.properties

            System.out.println("Connecting to the EPP Server and getting the greeting");
            epp_Greeting greeting = epp_client.connectAndGetGreeting();

            System.out.println("greeting's server: ["+greeting.getServerId()+"]");
            System.out.println("greeting's server-date: ["+greeting.getServerDate()+"]");
            System.out.println("greeting's service menu: ["+greeting.getSvcMenu()+"]");
	    System.out.println();
	    
            String client_trid = getClientTrid(epp_client_id);
            
            System.out.println("Logging into the EPP Server");

            epp_client.login(client_trid);

            try
            {
                // *****************************************************
                // Domain Transfer Request with OxrsTransfer extension
                // *****************************************************
                System.out.println("Creating the Domain Transfer command");
                epp_DomainTransferReq domain_transfer_request = new epp_DomainTransferReq();
                
                command_data = new epp_Command();
                command_data.m_client_trid = getClientTrid(epp_client_id);
                domain_transfer_request.m_cmd = command_data;

                epp_TransferRequest transfer_request = new epp_TransferRequest();
                // The OxrsTransfer is only needed for REQUEST transfer OP types
                transfer_request.m_op = epp_TransferOpType.REQUEST;
                transfer_request.m_auth_info = new epp_AuthInfo(epp_AuthInfoType.PW, null, "tralala");
                domain_transfer_request.m_trans = transfer_request;

                domain_transfer_request.m_name = "example.org";
                
                //****************************
                // The OxrsTransfer extension
                //****************************

                /*
                 * When transferring a .org domain from an RRP-based to
                 * an EPP-based registrar, the .org registry will require
                 * a set of EPP contacts to replace the placeholder
                 * contacts in place while the domain was in RRP.
                 */
                OxrsTransfer oxrs_transfer = new OxrsTransfer();
                oxrs_transfer.setRegistrant( "CNTCT-100" );
                List domain_contacts = new ArrayList();
                // The .org EPP registry requires one contact of each type.
                domain_contacts.add( new epp_DomainContact( epp_DomainContactType.TECH, "CNTCT-101" ) );
                domain_contacts.add( new epp_DomainContact( epp_DomainContactType.ADMIN, "CNTCT-102" ) );
                domain_contacts.add( new epp_DomainContact( epp_DomainContactType.BILLING, "CNTCT-103" ) );
                oxrs_transfer.setContacts( (epp_DomainContact[]) EPPXMLBase.convertListToArray((new epp_DomainContact()).getClass(), domain_contacts) );

                domain_transfer_request.m_cmd.m_extensions = new epp_Extension[1];
                domain_transfer_request.m_cmd.m_extensions[0] = oxrs_transfer;

                //****************************

                EPPDomainTransfer domain_transfer = new EPPDomainTransfer();
                domain_transfer.setRequestData(domain_transfer_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_transfer = (EPPDomainTransfer) epp_client.processAction(domain_transfer);
                // or, alternatively, this method can be used...
                //domain_transfer.fromXML(epp_client.processXML(domain_transfer.toXML()));

                epp_DomainTransferRsp domain_transfer_response = domain_transfer.getResponseData();
                epp_Response response = domain_transfer_response.m_rsp;
                epp_Result[] results = response.m_results;
                System.out.println("DomainTransfer results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");

                System.out.println("DomainTransfer Results: transfer status ["+domain_transfer_response.getTrnData().getTransferStatus()+"]");
            }
            catch ( epp_XMLException xcp )
            {
                // Either the request was missing some required data in
                // validation before sending to the server, or the server's
                // response was either unparsable or missing some required data.
                System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
            }
            catch ( epp_Exception xcp )
            {
                // The EPP Server has responded with an error code with
                // some optional messages to describe the error.
                System.err.println("epp_Exception!");
                epp_Result[] results = xcp.m_details;
                // We're taking advantage epp_Result's toString() here
                // for debugging.  Take a look at the javadocs for
                // the full list of attributes in the class.
                System.err.println("\tresult: ["+results[0]+"]");
            }
            catch ( Exception xcp )
            {
                // Other unexpected exceptions
                System.err.println("Domain Transfer (Query) failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
                xcp.printStackTrace();
            }


            System.out.println("Logging out from the EPP Server");
            epp_client.logout(client_trid);

            System.out.println("Disconnecting from the EPP Server");
            epp_client.disconnect();

        }
        catch ( epp_XMLException xcp )
        {
            System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
        }
        catch ( epp_Exception xcp )
        {
            System.err.println("epp_Exception!");
            epp_Result[] results = xcp.m_details;
            System.err.println("\tcode: ["+results[0].m_code+"] lang: ["+results[0].m_lang+"] msg: ["+results[0].m_msg+"]");
            if ( results[0].m_values != null && results[0].m_values.length > 0 )
            {
                System.err.println("\tvalue: ["+results[0].m_values[0]+"]");
            }
        }
        catch ( Exception xcp )
        {
            System.err.println("Exception! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
            xcp.printStackTrace();
        }

    }

    protected static String getClientTrid(String epp_client_id)
    {
        return "ABC:"+epp_client_id+":"+System.currentTimeMillis();
    }
}
