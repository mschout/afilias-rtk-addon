/*
**
** EPP RTK Java
** Copyright (C) 2001, Liberty Registry Management Services, Inc.
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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/DomainProtocolExample.java,v 1.1 2004/12/20 22:46:17 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/20 22:46:17 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.liberty.rtk.extension.epprtk.DomainProtocol;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;


/**
 * Example code for Liberty's Domain Trademark add-on to the RTK.
 * Uses domain create, info and update to demonstrate its usage.
 * The Domain Trademark is only required during the Sunrise period
 * of the .info registry.
 *
 * @author Daniel Manley
 * @version $Revision: 1.1 $ $Date: 2004/12/20 22:46:17 $
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
**/
public class DomainProtocolExample
{

    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.DomainProtocolExample epp_host_name epp_host_port epp_client_id epp_password org_rpp_domain";

    /**
     * Main of the example.
     * Performs Domain create, info and update and demontrate
     * the usage of the DomainTrademark class in those commands.
    **/
    public static void main(String args[])
    {

        System.out.println("Start of the .org Domain Protocol example");

        epp_Command command_data = null;
        epp_AuthInfo domain_auth_info = null;
        Date domain_exp_date = null;
	
        try
        {
            if (args.length != 5)
            {
                System.err.println("The args length is: ["+args.length+"]");
                System.err.println(USAGE);
                System.exit(1);
            }

            String epp_host_name = args[0];
            String epp_host_port_string = args[1];
            String epp_client_id = args[2];
            String epp_password  = args[3];

            String org_rrp_domain = args[4];

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

            String client_trid = "ABC:"+epp_client_id+":123";
            
            System.out.println("Logging into the EPP Server");
            epp_client.login(client_trid);

            try
            {
                // ***************************
                // Domain Info
                // ***************************
                System.out.println("Creating the Domain Info command");
                epp_DomainInfoReq domain_info_request = new epp_DomainInfoReq();

                command_data = new epp_Command();
                command_data.setClientTrid(client_trid);
                domain_info_request.setCmd(command_data);

                domain_info_request.setName(org_rrp_domain);
                
                EPPDomainInfo domain_info = new EPPDomainInfo();
                domain_info.setRequestData(domain_info_request);
                
                domain_info = (EPPDomainInfo) epp_client.processAction(domain_info);

                epp_DomainInfoRsp domain_info_response = domain_info.getResponseData();
                epp_Response response = domain_info_response.m_rsp;
                epp_Result[] results = response.m_results;

                System.out.println("DomainInfo results: ["+results[0].getCode()+"] ["+results[0].getMsg()+"]");
                
                if ( response.getExtensionStrings() != null &&
                     response.getExtensionStrings().length > 0 )
                {
                    DomainProtocol protocol = new DomainProtocol();
                    protocol.fromXML(response.getExtensionStrings()[0]);
                    System.out.println("***Domain Protocol ["+protocol.getProtocol()+"]");
                }
                else
                {
                    System.out.println("Domain has no trademark info!?!?!?");
                }
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
                System.err.println("\tcode: ["+results[0].m_code+"] lang: ["+results[0].m_lang+"] msg: ["+results[0].m_msg+"]");
                if ( results[0].m_values != null && results[0].m_values.length > 0 )
                {
                    System.err.println("\tvalue: ["+results[0].m_values[0]+"]");
                }
            }
            catch ( Exception xcp )
            {
                // Other unexpected exceptions
                System.err.println("Domain Info failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
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
}
