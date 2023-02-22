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

import org.openrtk.idl.epprtk.epp_AuthInfo;
import org.openrtk.idl.epprtk.epp_AuthInfoType;
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
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import com.liberty.rtk.extension.epprtk.IDN;
import com.liberty.rtk.extension.epprtk.IDValidation;
import com.liberty.rtk.util.VGRSPuny;
import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.RTKBase;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;


/**
 * Example code for ID Validation.
 *
**/
public class IDValidationDomainUpdateExample
{

    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.IDValidationDomainUpdateExample epp_host_name epp_host_port epp_client_id epp_password domain";

    /**
     * Main of the example.
     * Performs Domain update to demonstrate ID Validation extension
    **/
    public static void main(String args[])
    {

        System.out.println("Start of the ID Validation extension example");

        epp_Command command_data = null;
	
        try
        {
            if (args.length != 5)
            {
                System.err.println(USAGE);
                System.exit(1);
            }

            RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_THREE);

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
                // Domain Update
                // ***************************
                System.out.println("Creating the Domain Update command");
                epp_DomainUpdateReq domain_update_request = new epp_DomainUpdateReq();

                command_data = new epp_Command();
                // The client trid is optional.  Its main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests
                command_data.setClientTrid(client_trid);
                domain_update_request.setCmd(command_data);

                // The only domain-specific parameter is the domain name itself.
                domain_update_request.setName(VGRSPuny.easyEncodeDomain(domain_name));
                
                // ********************************
                //
                // ID Validation extension
                //
                // ********************************
                IDValidation id_validation_extension = new IDValidation();
                id_validation_extension.setCommand("update");
                                
                epp_Extension[] extensions = {id_validation_extension};
                domain_update_request.getCmd().setExtensions(extensions);

                EPPDomainUpdate domain_update = new EPPDomainUpdate();
                
                domain_update.setRequestData(domain_update_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                //domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);
                // or, alternatively, this method can be used...
                //domain_info.fromXML(epp_client.processXML(domain_info.toXML()));

                System.out.println("domain_update.toXML() = " + domain_update.toXML());
                try
                {
                	domain_update.fromXML(epp_client.processXML(domain_update.toXML()));
                }
                catch(epp_Exception e)
                {
                	// Test kludge if no server expected output which will be parsed
                	/*
            		String xml = "<epp xmlns='urn:ietf:params:xml:ns:epp-1.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='urn:ietf:params:xml:ns:epp-1.0 epp- 1.0.xsd'> <response> <result code='1000'> <msg lang='en-US'>Command completed successfully</msg> </result> <resData> <domain:creData xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\"> <domain:name>testvalidation.info</domain:name> <domain:crDate>2014-07-03T13:42:09.0Z</domain:crDate> <domain:exDate>2016-07-03T13:42:09.0Z</domain:exDate> </domain:creData> </resData> <extension> <validation:creData xmlns:validation=\"urn:afilias:params:xml:ns:validation-1.0\" xsi:schemaLocation=\"urn:afilias:params:xml:ns:validation-1.0 validation-1.0.xsd\"> <validation:claimID>c70fa3f7-c880-4d19-985b-65fcda4ca2f3</validation:claimID> </validation:creData> </extension> <trID><clTRID>ABC:admin:123</clTRID><svTRID>SRO-1406061419532</svTRID></trID> </response> </epp>";

                    String[] extensionStrings = new String[1];
                    extensionStrings[0] = xml;
                    
                    if ( extensionStrings[0] != null ) {
                    	id_validation_extension = new IDValidation();
                        id_validation_extension.fromXML(extensionStrings[0]);
                        System.out.println("Command [" + id_validation_extension.getCommand() + "]");
                        System.out.println("Data  [" + id_validation_extension.getValidationData() + "]");
                    } else {
                        System.out.println("Domain Info response contained no extension!!!");
                    }
					*/
                	
                	throw e;
                }


                epp_DomainUpdateRsp domain_update_response = domain_update.getResponseData();
                epp_Response response = domain_update_response.getRsp();
                
                String[] extensionStrings = response.getExtensionStrings();
                if ( extensionStrings[0] != null ) {
                	id_validation_extension = new IDValidation();
                    id_validation_extension.fromXML(extensionStrings[0]);
                    System.out.println("Command [" + id_validation_extension.getCommand() + "]");
                    System.out.println("Data  [" + id_validation_extension.getValidationData() + "]");
                } else {
                    System.out.println("Domain Info response contained no extension!!!");
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
