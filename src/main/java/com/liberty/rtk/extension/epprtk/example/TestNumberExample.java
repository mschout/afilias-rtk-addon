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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/TestNumberExample.java,v 1.1 2004/12/20 22:46:17 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/20 22:46:17 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.liberty.rtk.extension.epprtk.TestNumber;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;


/**
 * Example code for the Test Number "unspec" add-on.  Uses Domain
 * Check command to show the use of the Test Number.  This add-on
 * is intended to be used by registrars when they are going through the
 * registrar certification test with the Liberty .info registry.
 *
 * @author Daniel Manley
 * @version $Revision: 1.1 $ $Date: 2004/12/20 22:46:17 $
 * @see com.liberty.rtk.addon.TestNumber
 * @see com.tucows.oxrs.epprtk.rtk.EPPClient
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck
**/
public class TestNumberExample
{

    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.TestNumberExample epp_host_name epp_host_port epp_client_id epp_password";

    /**
     * Main of the example.
     * Performs a Domain check with a test number.
     */
    public static void main(String args[])
    {

        System.out.println("Start of the Certification Test Number Unspec example");

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
                // Domain Check
                // ***************************
                System.out.println("Creating the Domain Check command");
                epp_DomainCheckReq domain_check_request = new epp_DomainCheckReq();
                
                command_data = new epp_Command();
                command_data.m_client_trid = client_trid;
                domain_check_request.m_cmd = command_data;

                List domain_list = (List)new ArrayList();
                domain_list.add(epp_client_id+"1.info");
                domain_list.add(epp_client_id+"2.info");
                domain_list.add("domain.info");
                domain_check_request.m_names = EPPXMLBase.convertListToStringArray(domain_list);
                
                // ***************************
                //
                // Request testnum info
                //
                // ***************************
                TestNumber testnum_info = new TestNumber();
                testnum_info.setTestNum("2.6.11");
                
                domain_check_request.m_cmd.setExtension(testnum_info);
                
                EPPDomainCheck domain_check = new EPPDomainCheck();
                domain_check.setRequestData(domain_check_request);
                
                domain_check = (EPPDomainCheck) epp_client.processAction(domain_check);

                epp_DomainCheckRsp domain_check_response = domain_check.getResponseData();
                epp_Response response = domain_check_response.m_rsp;
                epp_Result[] results = response.m_results;
                System.out.println("DomainCheck results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
                epp_CheckResult[] check_results = domain_check_response.m_results;
                System.out.println("DomainCheck results: domain ["+epp_client_id+"1.info] exists? ["+EPPXMLBase.getCheckResultFor(check_results, epp_client_id+"1.info")+"]");
                System.out.println("DomainCheck results: domain ["+epp_client_id+"2.info] exists? ["+EPPXMLBase.getCheckResultFor(check_results, epp_client_id+"2.info")+"]");
                System.out.println("DomainCheck results: domain [domain.info] exists? ["+EPPXMLBase.getCheckResultFor(check_results, "domain.info")+"]");

                if ( response.getExtensionString() != null )
                {
                    TestNumber trademark = new TestNumber();
                    trademark.fromXML(response.getExtensionString());
                    System.out.println("TestNumber testnum ["+trademark.getTestNum()+"]");
                }
                else
                {
                    System.out.println("Response has no testnum unspec data.");
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
                System.err.println("Domain Check failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
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
