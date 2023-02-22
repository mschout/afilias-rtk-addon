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

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.liberty.rtk.extension.epprtk.*;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.contact.*;

public class SupplementalDataExample extends SessionExample
{
    String contact_id = null;

    public SupplementalDataExample(String args[])
    {
 	super(args);
        contact_id = nextArgument();
        assertNotNull(contact_id);
    }

    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.SupplementalDataExample epp_host_name epp_host_port epp_client_id epp_password contact_id";

    public static void main(String args[])
    {
 	SessionExample example = new SupplementalDataExample(args);
        example.session();
    }

    protected void process() throws epp_Exception, IOException, epp_XMLException
    {
        if (!checkContact())
        {
                        System.out.println("contact " + contact_id + " exists in the registry, please choose another contact");
                        System.exit(1);
        }

        createContact();
        contactInfo();
        contactUpdate();
    }

    private boolean checkContact() throws epp_Exception, epp_XMLException
        {
                System.out.println("Creating the Contact Check command for ["+contact_id+"]");

                epp_ContactCheckReq contact_check_request = new epp_ContactCheckReq();                                                                                                       
                contact_check_request.setCmd( createEPPCommand() );

                List contact_list = (List)new ArrayList();
                contact_list.add(contact_id);

                contact_check_request.setIds( EPPXMLBase.convertListToStringArray(contact_list) );

                EPPContactCheck contact_check = new EPPContactCheck();
                contact_check.setRequestData(contact_check_request);

                contact_check = (EPPContactCheck) epp_client.processAction(contact_check);

                epp_ContactCheckRsp contact_check_response = contact_check.getResponseData();

                epp_CheckResult[] check_results = contact_check_response.getResults();
                System.out.println("ContactCheck results: contact ["+contact_id+"] available? ["+EPPXMLBase.getAvailResultFor(check_results, contact_id)+"]");

                if ( EPPXMLBase.getAvailResultFor(check_results, contact_id) == null )
            return false;

        return EPPXMLBase.getAvailResultFor(check_results, contact_id).booleanValue();
        }

        private void createContact() throws epp_Exception, IOException, epp_XMLException
        {
                System.out.println("Creating the Contact Create command");
                epp_ContactCreateReq contact_create_request = new epp_ContactCreateReq();
                
                contact_create_request.setCmd( createEPPCommand() );
                contact_create_request.setId( contact_id );

                System.out.print("Dear registrant, please enter a passphrase for the new registrant contact(min 6, max 16): ");
                epp_AuthInfo contact_auth_info = getUserInputAuthInfo();

                contact_create_request.setAuthInfo( contact_auth_info );

                epp_ContactNameAddress[] name_address = new epp_ContactNameAddress[1];
                name_address[0] = new epp_ContactNameAddress();
                name_address[0].setType( epp_ContactPostalInfoType.INT );
                name_address[0].setName( "John Doe" );
                name_address[0].setOrg( "ACME Solutions" );
                epp_ContactAddress address = new epp_ContactAddress();
                address.setStreet1( "100 Centre St" );
                address.setCity( "Townsville" );
                address.setStateProvince( "County Derry" );
                address.setPostalCode( "Z1Z1Z1" );
                address.setCountryCode( "CA" );
                name_address[0].setAddress( address );

                contact_create_request.setAddresses( name_address );
                contact_create_request.setVoice( new epp_ContactPhone("1234", "+1.4165559999") );
                contact_create_request.setFax( new epp_ContactPhone("9876", "+1.4165558888") );
                contact_create_request.setEmail( "john.doe@company.info" );

	        SupplementalDataCreate supp_create = new SupplementalDataCreate();
		supp_create.setData("{\"profession\": \"Driver\",\"authorityName\": \"MTO\"}");
                
                contact_create_request.getCmd().setExtension(supp_create);
                
                EPPContactCreate contact_create = new EPPContactCreate();
                contact_create.setRequestData(contact_create_request);
                
                contact_create = (EPPContactCreate) epp_client.processAction(contact_create);

                epp_ContactCreateRsp contact_create_response = contact_create.getResponseData();

		System.out.println("ContactCreate results: contact id ["+contact_create_response.getId()+"]");
        }

        private void contactInfo() throws epp_XMLException, epp_Exception
        {        
                System.out.println("Creating the Contact Info command");
                epp_ContactInfoReq contact_info_request = new epp_ContactInfoReq();

                contact_info_request.setCmd( createEPPCommand() );

                contact_info_request.setId(contact_id);
                
                EPPContactInfo contact_info = new EPPContactInfo();
                contact_info.setRequestData(contact_info_request);
                
                contact_info = (EPPContactInfo) epp_client.processAction(contact_info);

                epp_ContactInfoRsp contact_info_response = contact_info.getResponseData();
                epp_Response response = contact_info_response.m_rsp;
                epp_Result[] results = response.m_results;

                String[] extensionStrings = response.getExtensionStrings();
                if (  extensionStrings != null )
                {
                    SupplementalDataInfData inf_data_extension = new SupplementalDataInfData();
                    inf_data_extension.fromXML(extensionStrings[0]);
                    System.out.println("Supplemental data ["+inf_data_extension.getData() +"]");
                }
                else
                {
                    System.out.println("Contact Info response contained no extension!!!");
                }
        }


        private void contactUpdate() throws epp_XMLException, epp_Exception
        { 
                // Put
                System.out.println("Creating the Contact Update command to test put");
                epp_ContactUpdateReq contact_update_request = new epp_ContactUpdateReq();
                
                contact_update_request.setCmd( createEPPCommand() );
                contact_update_request.setId( contact_id );

                SupplementalDataPut put = new SupplementalDataPut();
                put.setData("{\"profession\": \"Driver1\",\"authorityName\": \"MTO\"}");

                SupplementalDataUpdate supp_update = new SupplementalDataUpdate();
                supp_update.setPut(put);

                contact_update_request.getCmd().setExtension(supp_update);

                EPPContactUpdate contact_update = new EPPContactUpdate();
                contact_update.setRequestData(contact_update_request);
                
                contact_update = (EPPContactUpdate) epp_client.processAction(contact_update);

                epp_ContactUpdateRsp contact_update_response = contact_update.getResponseData();
                epp_Response response = contact_update_response.m_rsp;
                epp_Result[] results = response.m_results;
                System.out.println("ContactUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");

        }
}
