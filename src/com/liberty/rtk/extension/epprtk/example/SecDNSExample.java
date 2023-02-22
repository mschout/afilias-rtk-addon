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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/SecDNSExample.java,v 1.3 2007/11/19 20:25:47 asimbirt Exp $
 * $Revision: 1.3 $
 * $Date: 2007/11/19 20:25:47 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.liberty.rtk.extension.epprtk.*;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;

public class SecDNSExample
{
    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.SecDNSExample epp_host_name epp_host_port epp_client_id epp_password";

    public static void main(String args[])
    {
        System.out.println("Start of the SecDNS example");

        epp_Command command_data = null;
        epp_AuthInfo domain_auth_info = null;
        Date domain_exp_date = null;
    
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

            RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_THREE);

            System.out.println("Connecting to the EPP Server and getting the greeting");
            epp_Greeting greeting = epp_client.connectAndGetGreeting();

            System.out.println("greeting's server: ["+greeting.m_server_id+"]");
            System.out.println("greeting's server-date: ["+greeting.m_server_date+"]");

            String client_trid = "ABC:"+epp_client_id+":123";
            
            System.out.println("Logging into the EPP Server");
            epp_client.login(client_trid);

            try
            {
                // ***************************
                // Domain Create
                // ***************************
                System.out.println("Creating the Domain Create command");
                epp_DomainCreateReq domain_create_request = new epp_DomainCreateReq();
                
                command_data = new epp_Command();
                // The client trid is optional.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests
                command_data.m_client_trid = client_trid;
                domain_create_request.m_cmd = command_data;

                domain_create_request.m_name = epp_client_id+"2.org";
                                
                // In this example, we're only getting the Auth info because it's 
                // required.  No contact or nameserver info is shown here.  Please
                // see the DomainExample and SessionExample from the EPP RTK
                // for related sample code.
                
                BufferedReader buffed_reader = new BufferedReader(new InputStreamReader(System.in));
                domain_auth_info = new epp_AuthInfo();
                System.out.print("Dear registrant, please enter a passphrase for your new domain: ");
                while ( domain_auth_info.m_value == null ||
                        domain_auth_info.m_value.length() == 0 )
                {
                    domain_auth_info.m_value = buffed_reader.readLine();
                }
                domain_auth_info.m_type = epp_AuthInfoType.PW;
                domain_create_request.m_auth_info = domain_auth_info;

                // ***************************
                //
                // SecDNSCreate Data
                //
                // ***************************
                SecDNSDsData ds_data1 = new SecDNSDsData();
                ds_data1.setKeyTag(12345);
                ds_data1.setAlg(3);
                ds_data1.setDigestType(1);
                ds_data1.setDigest("49FD46E6C4B45C55D4AC49FD46E6C4B45C55D4AC");

                // keyData under dsData will be ignored by server
                
                SecDNSKeyData ds_kd1 = new SecDNSKeyData();
                ds_kd1.setFlags(256);
                ds_kd1.setProtocol(3);
                ds_kd1.setAlg(1);
                ds_kd1.setPubKey("AQPVBMkh2yOOMh0geuQXFn272x3YbkInnkp9QEf7x+4NkaMKePPruPRumyE87m5TdJVuunpGylNYmPy6zMkeWUat/6dqUxO0ugtPJTYT5d1O4OeCcgUr7LdTXMxfm+Ke57Ba3G3A8ELKdBZ56ZWgbFD+1dIxU7ejvN6Mh1jIfD7kew==");
                

                SecDNSDsData ds_data2 = new SecDNSDsData();
                ds_data2.setKeyTag(1234);
                ds_data2.setAlg(3);
                ds_data2.setDigestType(1);
                ds_data2.setDigest("49FD46E6C4B45C55D4AC49FD46E6C4B45C55D4AC");
                ds_data2.setKeyData(ds_kd1);
                
                SecDNSCreate secDNS_create = new SecDNSCreate();
                secDNS_create.setDsData(Arrays.asList(new SecDNSDsData[]{ds_data1,ds_data2}));
                
                domain_create_request.getCmd().setExtension(secDNS_create);
                
                EPPDomainCreate domain_create = new EPPDomainCreate();
                domain_create.setRequestData(domain_create_request);
                
                domain_create = (EPPDomainCreate) epp_client.processAction(domain_create);

                epp_DomainCreateRsp domain_create_response = domain_create.getResponseData();
                epp_Response response = domain_create_response.m_rsp;

                epp_Result[] results = response.m_results;
                System.out.println("DomainCreate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
                System.out.println("DomainCreate results: exp date ["+domain_create_response.m_expiration_date+"]");
                domain_exp_date = RTKBase.UTC_FMT.parse(domain_create_response.m_expiration_date);
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
                System.err.println("Domain Create failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
                xcp.printStackTrace();
            }


            try
            {
                // ***************************
                // Domain Info
                // ***************************
                System.out.println("Creating the Domain Info command");
                epp_DomainInfoReq domain_info_request = new epp_DomainInfoReq();

                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_info_request.m_cmd = command_data;

                domain_info_request.m_name = epp_client_id+"2.org";
                
                EPPDomainInfo domain_info = new EPPDomainInfo();
                domain_info.setRequestData(domain_info_request);
                
                domain_info = (EPPDomainInfo) epp_client.processAction(domain_info);

                epp_DomainInfoRsp domain_info_response = domain_info.getResponseData();
                epp_Response response = domain_info_response.m_rsp;
                epp_Result[] results = response.m_results;

                String[] extensionStrings = response.getExtensionStrings();
                if (  extensionStrings != null )
                {
                    SecDNSInfData inf_data_extension = new SecDNSInfData();
                    inf_data_extension.fromXML(extensionStrings[0]);
                    System.out.println("SecDNS maxSigLife ["+inf_data_extension.getMaxSigLife() +"]; SecDNS data ["+ inf_data_extension.getDsData() +"]");
                }
                else
                {
                    System.out.println("Domain has no SecDNS info!?!?!?");
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


            try
            {
                // ***************************
                // Domain Update
                // ***************************

                // Add 
                System.out.println("Creating the Domain Update command to test add");
                epp_DomainUpdateReq domain_update_request = new epp_DomainUpdateReq();
                
                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_update_request.m_cmd = command_data;

                domain_update_request.m_name = epp_client_id+"2.org";
                
                // *************
                //
                // SecDNSUpdate Data 
                //
                // *************

                SecDNSDsData ds_data1 = new SecDNSDsData();
                ds_data1.setKeyTag(1256);
                ds_data1.setAlg(3);
                ds_data1.setDigestType(1);
                ds_data1.setDigest("49FD46E6C4B45C55D4AC49FD46E6C4B45C55D4AC");
                
                SecDNSAdd add = new SecDNSAdd();
                add.setDsData(Arrays.asList(new SecDNSDsData[]{ds_data1}));

                SecDNSUpdate secDNS_update = new SecDNSUpdate();
                secDNS_update.setUrgent(true);
                secDNS_update.setAdd(add);

                domain_update_request.getCmd().setExtension(secDNS_update);

                EPPDomainUpdate domain_update = new EPPDomainUpdate();
                domain_update.setRequestData(domain_update_request);
                
                domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);

                epp_DomainUpdateRsp domain_update_response = domain_update.getResponseData();
                epp_Response response = domain_update_response.m_rsp;
                epp_Result[] results = response.m_results;
                System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");

                // Chg
                //epp server no longer support change
                /*
                System.out.println("Creating the Domain Update command to test change");
                epp_DomainUpdateReq domain_update_request1 = new epp_DomainUpdateReq();

                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_update_request1.m_cmd = command_data;

                domain_update_request1.m_name = epp_client_id+"2.org";

                // *************
                //
                // SecDNSUpdate Data
                //
                // *************

                SecDNSChg chg = new SecDNSChg();
                chg.setDsData(Arrays.asList(new SecDNSDsData[]{ds_data1}));

                SecDNSUpdate secDNS_update1 = new SecDNSUpdate();
                secDNS_update1.setUrgent(true);
                secDNS_update1.setChg(chg);

                domain_update_request1.getCmd().setExtension(secDNS_update1);

                EPPDomainUpdate domain_update1 = new EPPDomainUpdate();
                domain_update1.setRequestData(domain_update_request1);

                domain_update1 = (EPPDomainUpdate) epp_client.processAction(domain_update1);

                domain_update_response = domain_update1.getResponseData();
                response = domain_update_response.m_rsp;
                results = response.m_results;
                System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");  
                */


                //Rem

                System.out.println("Creating the Domain Update command to test remove");
                epp_DomainUpdateReq domain_update_request2 = new epp_DomainUpdateReq();

                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_update_request2.m_cmd = command_data;

                domain_update_request2.m_name = epp_client_id+"2.org";

                // *************
                //
                // SecDNSUpdate Data
                //
                // *************
             
                SecDNSDsData ds_data_rem = new SecDNSDsData();
                ds_data_rem.setKeyTag(1256);
                ds_data_rem.setAlg(3);
                ds_data_rem.setDigestType(1);
                ds_data_rem.setDigest("49FD46E6C4B45C55D4AC49FD46E6C4B45C55D4AC");

                SecDNSRem rem = new SecDNSRem();
                rem.setDsData(Arrays.asList(new SecDNSDsData[]{ds_data_rem}));

                SecDNSUpdate secDNS_update2 = new SecDNSUpdate();
                secDNS_update2.setUrgent(true);
                secDNS_update2.setRem(rem);

                domain_update_request2.getCmd().setExtension(secDNS_update2);

                EPPDomainUpdate domain_update2 = new EPPDomainUpdate();
                domain_update2.setRequestData(domain_update_request2);

                domain_update2 = (EPPDomainUpdate) epp_client.processAction(domain_update2);

                domain_update_response = domain_update2.getResponseData();
                response = domain_update_response.m_rsp;
                results = response.m_results;
                System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");

                //remove all
                System.out.println("Creating the Domain Update command to test remove all");
                epp_DomainUpdateReq domain_update_request3 = new epp_DomainUpdateReq();

                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_update_request3.m_cmd = command_data;

                domain_update_request3.m_name = epp_client_id+"2.org";
                
                SecDNSRem rem2 = new SecDNSRem();
                rem2.setSecDNSAll(true);
                
		SecDNSUpdate secDNS_update3 = new SecDNSUpdate();
                secDNS_update3.setUrgent(true);
                secDNS_update3.setRem(rem2);

                domain_update_request3.getCmd().setExtension(secDNS_update3);

                EPPDomainUpdate domain_update3 = new EPPDomainUpdate();
                domain_update3.setRequestData(domain_update_request3);

                domain_update3 = (EPPDomainUpdate) epp_client.processAction(domain_update3);

                domain_update_response = domain_update3.getResponseData();
                response = domain_update_response.m_rsp;
                results = response.m_results;
                System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
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
                System.err.println("Domain Update failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
                xcp.printStackTrace();
            }


            try
            {
                // ***************************
                // Domain Delete
                // ***************************
                System.out.println("Creating the Domain Delete command");
                epp_DomainDeleteReq domain_delete_request = new epp_DomainDeleteReq();
                
                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_delete_request.m_cmd = command_data;

                domain_delete_request.m_name = epp_client_id+"2.org";
                
                EPPDomainDelete domain_delete = new EPPDomainDelete();
                domain_delete.setRequestData(domain_delete_request);
                
                domain_delete = (EPPDomainDelete) epp_client.processAction(domain_delete);

                epp_DomainDeleteRsp domain_delete_response = domain_delete.getResponseData();
                epp_Response response = domain_delete_response.m_rsp;
                epp_Result[] results = response.m_results;
                System.out.println("DomainDelete results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
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
                System.err.println("Domain Delete failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
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
