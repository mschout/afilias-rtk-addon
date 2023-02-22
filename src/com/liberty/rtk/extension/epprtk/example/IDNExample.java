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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/IDNExample.java,v 1.1 2004/12/20 22:46:17 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/20 22:46:17 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.ArrayList;
import java.util.List;

import org.openrtk.idl.epprtk.epp_CheckResult;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Exception;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_Greeting;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckRsp;

import com.liberty.rtk.extension.epprtk.IDN;
import com.liberty.rtk.util.VGRSPuny;
import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.RTKBase;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;


/**
 * Example code for Liberty's IDN extension for the .org TLD.
 * Uses domain check to demonstrate its usage.  Please see the 
 * IDNGUIExample for more usage.
 *
 * @author Daniel Manley
 * @version $Revision: 1.1 $ $Date: 2004/12/20 22:46:17 $
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck
 * @see com.liberty.rtk.extension.epprtk.example.IDNGUIExample
 * @see com.liberty.rtk.util.VGRSPuny
**/
public class IDNExample
{

    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.IDNExample epp_host_name epp_host_port epp_client_id epp_password domain";

    /**
     * Main of the example.
     * Performs Domain renew to demontrate
     * the usage of the RGPRenew class.
    **/
    public static void main(String args[])
    {

        System.out.println("Start of the IDN extension example");

        epp_Command command_data = null;
	
        try
        {
            if (args.length != 5)
            {
                System.err.println(USAGE);
                System.exit(1);
            }

            RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_ONE);

            String epp_host_name        = args[0];
            String epp_host_port_string = args[1];
            String epp_client_id        = args[2];
            String epp_password         = args[3];
            String domain_name          = args[4];

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
                // The client trid is optional.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests
                command_data.setClientTrid(client_trid);
                domain_check_request.setCmd(command_data);

                // The Domain Check request can accept an array of domain
                // names.  In this example, an ArrayList is used to dynamically
                // create the List of domain names and then EPPXMLBase's
                // utility method convertListToStringArray() is used
                // to convert the List to a String array.
                List domain_list = new ArrayList();
                domain_list.add(epp_client_id+"1.org");
                domain_list.add(epp_client_id+"2.org");

                // Using a wrapper class to encode the utf-8 domain name into punycode
                domain_list.add(VGRSPuny.easyEncodeDomain(domain_name));

                domain_check_request.setNames(EPPXMLBase.convertListToStringArray(domain_list));
                
                // ********************************
                //
                // Domain IDN extension
                //
                // ********************************
                IDN idn_extension = new IDN();
                idn_extension.setCommand("check");
                idn_extension.setScript("de");

                epp_Extension[] extensions = {idn_extension};

                domain_check_request.getCmd().setExtensions(extensions);
                
                EPPDomainCheck domain_check = new EPPDomainCheck();
                domain_check.setRequestData(domain_check_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_check = (EPPDomainCheck) epp_client.processAction(domain_check);
                // or, alternatively, this method can be used...
                //domain_check.fromXML(epp_client.processXML(domain_check.toXML()));

                epp_DomainCheckRsp domain_check_response = domain_check.getResponseData();
                epp_Response response = domain_check_response.getRsp();
                epp_Result[] results = response.getResults();
                System.out.println("DomainCheck results: ["+results[0].getCode()+"] ["+results[0].getMsg()+"]");
                // All EPP Check requests, regardless of the object being checked,
                // will return a generic epp_CheckResult array.  To find the
                // check results for a particular object, EPPXMLBase's utility
                // method getAvailResultFor() can be used.  This method returns
                // a Boolean object or null if the value was not found in the
                // epp_CheckResult array.
                epp_CheckResult[] check_results = domain_check_response.getResults();
                System.out.println("DomainCheck results: domain ["+epp_client_id+"1.org] exists? ["+EPPXMLBase.getAvailResultFor(check_results, epp_client_id+"1.org")+"]");
                System.out.println("DomainCheck results: domain ["+epp_client_id+"2.org] exists? ["+EPPXMLBase.getAvailResultFor(check_results, epp_client_id+"2.org")+"]");
                System.out.println("DomainCheck results: domain [domain.org] exists? ["+EPPXMLBase.getAvailResultFor(check_results, "domain.org")+"]");

                String[] extensionStrings = response.getExtensionStrings();
                if ( extensionStrings != null ) {
                    idn_extension = new IDN();
                    idn_extension.fromXML(extensionStrings[0]);
                    System.out.println("Command ["+idn_extension.getCommand()+"]");
                    System.out.println("Script  ["+idn_extension.getScript()+"]");
                } else {
                    System.out.println("Domain Check response contained no unspec!!!");
                }

            }
            catch ( epp_XMLException xcp )
            {
                // Either the request was missing some required data in
                // validation before sending to the server, or the server's
                // response was either unparsable or missing some required data.
                xcp.printStackTrace();
                System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
            }
            catch ( epp_Exception xcp )
            {
                // The EPP Server has responded with an error code with
                // some optional messages to describe the error.
                System.err.println("epp_Exception!");
                epp_Result[] results = xcp.getDetails();
                System.err.println("\tcode: ["+results[0].getCode()+"] lang: ["+results[0].getLang()+"] msg: ["+results[0].getMsg()+"]");
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
