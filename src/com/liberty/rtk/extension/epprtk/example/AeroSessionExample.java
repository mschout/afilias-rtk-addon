/*
**
** EPP RTK Java
** Copyright (C) 2001-2002, Tucows, Inc.
** Copyright (C) 2003, Liberty RMS
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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/AeroSessionExample.java,v 1.2 2006/08/23 20:26:47 ewang2004 Exp $
 * $Revision: 1.2 $
 * $Date: 2006/08/23 20:26:47 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;
import com.tucows.oxrs.epprtk.rtk.transport.EPPTransportException;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;
import org.openrtk.idl.epprtk.host.*;
import org.openrtk.idl.epprtk.contact.*;

import com.liberty.rtk.extension.epprtk.AeroContactN;
import com.liberty.rtk.extension.epprtk.AeroDomain;
import com.liberty.rtk.extension.epprtk.EnsInfo;

/**
 * Example code for a typical logical EPP sessions.
 *
 * @author Eric Wang
 * @version $Revision: 1.2 $ $Date: 2006/08/23 20:26:47 $
**/
public class AeroSessionExample
{

    private static String USAGE = "Usage: com.tucows.oxrs.epprtk.rtk.example.AeroSessionExample epp_host_name epp_host_port epp_client_id epp_password domain_name ens_auth_id1 ens_auth_key1 ens_auth_id2 ens_auth_key2";

	private epp_AuthInfo ens_auth_info1 = null;
	private epp_AuthInfo ens_auth_info2 = null;
	private String epp_host_name = null;
	private int epp_host_port = 0;
	private String epp_client_id = null;
	private String epp_password  = null;
	private String domain_name  = null;
	private String ens_auth_id1 = null;
	private String ens_auth_key1 = null;
	private String ens_auth_id2 = null;
	private String ens_auth_key2 = null;
	private String contact_id1 = null;
	private String contact_id2 = null;

	private EPPClient epp_client = null;

	public AeroSessionExample() {}

	public void init(String args[])
	{
		if (args.length < 9)
		{
			System.err.println(USAGE);
			System.exit(1);
		}

		epp_host_name = args[0];
		epp_host_port = Integer.parseInt(args[1]);
		epp_client_id = args[2];
		epp_password  = args[3];
		domain_name  = args[4];
		ens_auth_id1 = args[5];
		ens_auth_key1 = args[6];
		ens_auth_id2 = args[7];
		ens_auth_key2 = args[8];

		ens_auth_info1 = new epp_AuthInfo();
		ens_auth_info1.setType(epp_AuthInfoType.PW);
		ens_auth_info1.setValue(ens_auth_key1);
		ens_auth_info2 = new epp_AuthInfo();
		ens_auth_info2.setType(epp_AuthInfoType.PW);
		ens_auth_info2.setValue(ens_auth_key2);

		contact_id1 = epp_client_id + "001";
		contact_id2 = epp_client_id + "002";
	}

	public void login() throws epp_Exception, IOException, epp_XMLException, EPPTransportException
	{
		epp_client = new EPPClient(epp_host_name, epp_host_port, epp_client_id, epp_password); 
		epp_client.setLang("en");

		System.out.println("Connecting to the EPP Server and getting the greeting");

		/*
		 * Uncomment following line if you don't want to send RTK version
		 * number on Login. Although Liberty RTK recomends to use this extension
		 * tag on Login request.
		 */
		//epp_client.setVersionSentOnLogin( false );

		epp_Greeting greeting = epp_client.connectAndGetGreeting();

		System.out.println("greeting's server: ["+greeting.getServerId()+"]");
		System.out.println("greeting's server-date: ["+greeting.getServerDate()+"]");
		System.out.println("greeting's service menu: ["+greeting.getSvcMenu()+"]");

		System.out.println("Logging into the EPP Server");
		epp_client.login( getClientTrid() );
	}

	public void logout() throws epp_Exception, IOException, epp_XMLException
	{
		// All done with this session, so let's log out...
		System.out.println("Logging out from the EPP Server");
		epp_client.logout(getClientTrid());

		// ... and disconnect
		System.out.println("Disconnecting from the EPP Server");
		epp_client.disconnect();
	}

	public void process() throws epp_Exception, IOException, epp_XMLException
	{
		verifyENSContact(ens_auth_id1, ens_auth_info1);
		verifyENSContact(ens_auth_id2, ens_auth_info2);

		checkDomain();

		if (isContactAvailable(contact_id1))
		{
			createContact(contact_id1);
		}

		if (isContactAvailable(contact_id2))
		{
			createContact(contact_id2);
		}

        String random_id = "AERO-Test-" + (new Random()).nextInt(10000);
        String maintainer_url1 = "http://www.mytest1.aero";
        String maintainer_url2 = "http://www.mytest2.aero";

        createContactWithMaintainerURL(random_id, maintainer_url1);
        verifyMaintainerURL(random_id);
        updateContactWithMaintainerURL(random_id, maintainer_url2);
        verifyMaintainerURL(random_id);

		createDomain();
		getDomainInfo(domain_name);
        updateDomainENSConect();
		getDomainInfo(domain_name);

        // pending create domain
        String domain = "rtk-" + (new Random().nextInt(10000)) + ".aero";
        createDomainPending(domain);
		getDomainInfo(domain);
	}

    private void createContactWithMaintainerURL(String contact_id, String url) throws epp_Exception, epp_XMLException, IOException
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

        AeroContactN aero_contact_extension = new AeroContactN();
        aero_contact_extension.setCommand("create");
        aero_contact_extension.setMaintainerUrl(url);

        epp_Extension[] extensions = {aero_contact_extension};
        contact_create_request.getCmd().setExtensions(extensions);

		EPPContactCreate contact_create = new EPPContactCreate();
		contact_create.setRequestData(contact_create_request);

		contact_create = (EPPContactCreate) epp_client.processAction(contact_create);

		epp_ContactCreateRsp contact_create_response = contact_create.getResponseData();
		System.out.println("ContactCreate results: contact id ["+contact_create_response.getId()+"]");
	}

    private void updateContactWithMaintainerURL(String contact_id, String url) throws epp_Exception, epp_XMLException
    {
        System.out.println("Creating the Contact Update command");
        epp_ContactUpdateReq contact_update_request = new epp_ContactUpdateReq();

        contact_update_request.setCmd( createEPPCommand() );
        contact_update_request.setId(contact_id);

        //empty chg emlement
        epp_ContactUpdateChange contact_update_change = new epp_ContactUpdateChange();
        contact_update_request.setChange( contact_update_change );

        AeroContactN aero_contact_extension = new AeroContactN();
        aero_contact_extension.setCommand("update");
        aero_contact_extension.setMaintainerUrl(url);
                                                                                                                             
        epp_Extension[] extensions = {aero_contact_extension};
        contact_update_request.getCmd().setExtensions(extensions);

        EPPContactUpdate contact_update = new EPPContactUpdate();
        contact_update.setRequestData(contact_update_request);

        contact_update = (EPPContactUpdate) epp_client.processAction(contact_update);

        epp_ContactUpdateRsp contact_update_response = contact_update.getResponseData();
        epp_Response response = contact_update_response.getRsp();
        epp_Result[] results = response.getResults();
        System.out.println("ContactUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
    }

	private void verifyMaintainerURL(String contact_id) throws epp_Exception, epp_XMLException
	{
		System.out.println("Creating the Contact Info command");


		// at aero registry implementation stage one, only contact info (query
		// ens contact) response require AeroContactN extension.

		EPPContactInfo contact_info = new EPPContactInfo();

		epp_ContactInfoReq contact_info_request = new epp_ContactInfoReq();

		contact_info_request.setCmd(createEPPCommand());
		contact_info_request.setId(contact_id);

		contact_info.setRequestData(contact_info_request);

		contact_info = (EPPContactInfo) epp_client.processAction(contact_info);

		epp_ContactInfoRsp contact_info_response = contact_info.getResponseData();

		// display result
		epp_Response response = contact_info_response.getRsp();
		epp_Result[] results = response.getResults();
		System.out.println("ContactInfo results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
		System.out.println("ContactInfo extension:");
		String[] extensionStrings = response.getExtensionStrings();
		if ( extensionStrings != null ) {
			AeroContactN aero_contact_extension = new AeroContactN();
			aero_contact_extension.fromXML(extensionStrings[0]);
			System.out.println("Maintainer URL: ["+aero_contact_extension.getMaintainerUrl()+"]");
		} else {
			System.out.println("Contact Info response contained no extension!!!");
		}
	}
	/*
	 * ENS contact info (ENS contact is a special contact which
	 * owned by SITA but assigned to registrant
	 *
	 * First, the registrar should check if the given ens_auth_id1 
	 * is valid in the registry.  If it does not, we'll quit this 
	 * session example.
	 */
	private void verifyENSContact(String ens_auth_id, epp_AuthInfo ens_auth_info) throws epp_Exception, epp_XMLException
	{
		System.out.println("Creating the Contact Info command");


		// at aero registry implementation stage one, only contact info (query
		// ens contact) response require AeroContactN extension.

		EPPContactInfo contact_info = new EPPContactInfo();

		epp_ContactInfoReq contact_info_request = new epp_ContactInfoReq();

		contact_info_request.setCmd(createEPPCommand());
		contact_info_request.setId(ens_auth_id);
		contact_info_request.setAuthInfo(ens_auth_info);

		contact_info.setRequestData(contact_info_request);

		contact_info = (EPPContactInfo) epp_client.processAction(contact_info);

		epp_ContactInfoRsp contact_info_response = contact_info.getResponseData();

		// display result
		epp_Response response = contact_info_response.getRsp();
		epp_Result[] results = response.getResults();
		System.out.println("ContactInfo results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
		System.out.println("ContactInfo extension:");
		String[] extensionStrings = response.getExtensionStrings();
		if ( extensionStrings != null ) {
			AeroContactN aero_contact_extension = new AeroContactN();
			aero_contact_extension.fromXML(extensionStrings[0]);
			System.out.println("RegistrantGroup ["+aero_contact_extension.getEnsInfo().getRegistrantGroup()+"]");
			System.out.println("EnsO  ["+aero_contact_extension.getEnsInfo().getEnsO()+"]");
			System.out.println("RequestType  ["+aero_contact_extension.getEnsInfo().getRequestType()+"]");
			System.out.println("RegistrationType  ["+aero_contact_extension.getEnsInfo().getRegistrationType()+"]");
			System.out.println("CredentialsType  ["+aero_contact_extension.getEnsInfo().getCredentialsType()+"]");
			System.out.println("CredentialsValue  ["+aero_contact_extension.getEnsInfo().getCredentialsValue()+"]");
			System.out.println("CodeValue  ["+aero_contact_extension.getEnsInfo().getCodeValue()+"]");
			System.out.println("UniqueIdentifier  ["+aero_contact_extension.getEnsInfo().getUniqueIdentifier()+"]");
			System.out.println("LastCheckedDate  ["+aero_contact_extension.getEnsInfo().getLastCheckedDate()+"]");
		} else {
			System.out.println("Contact Info response contained no extension!!!");
		}
	}

	private void checkDomain() throws epp_Exception, epp_XMLException
	{
		System.out.println("Creating the Domain Check command");
		epp_DomainCheckReq domain_check_request = new epp_DomainCheckReq();

		domain_check_request.setCmd( createEPPCommand() );

		List domain_list = (List)new ArrayList();
		domain_list.add(domain_name);
		domain_check_request.setNames( EPPXMLBase.convertListToStringArray(domain_list) );

		EPPDomainCheck domain_check = new EPPDomainCheck();
		domain_check.setRequestData(domain_check_request);

		domain_check = (EPPDomainCheck) epp_client.processAction(domain_check);

		epp_DomainCheckRsp domain_check_response = domain_check.getResponseData();
		epp_CheckResult[] check_results = domain_check_response.getResults();
		System.out.println("DomainCheck results: domain ["+domain_name+"] available? ["+EPPXMLBase.getAvailResultFor(check_results, domain_name)+"]");

		if ( EPPXMLBase.getAvailResultFor(check_results, domain_name) == null ||
				!EPPXMLBase.getAvailResultFor(check_results, domain_name).booleanValue() )
		{
			System.out.println("domain " + domain_name + " exists in .aero registry, please choose another domain");
			System.exit(1);
		}
	}

	private boolean isContactAvailable(String contact_id) throws epp_Exception, epp_XMLException
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

		if ( EPPXMLBase.getAvailResultFor(check_results, contact_id) != null )
		{                             
			return EPPXMLBase.getAvailResultFor(check_results, contact_id).booleanValue();
		}

		return false;
	}

	private void createContact(String contact_id) throws epp_Exception, IOException, epp_XMLException
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

		EPPContactCreate contact_create = new EPPContactCreate();
		contact_create.setRequestData(contact_create_request);

		contact_create = (EPPContactCreate) epp_client.processAction(contact_create);

		epp_ContactCreateRsp contact_create_response = contact_create.getResponseData();
		System.out.println("ContactCreate results: contact id ["+contact_create_response.getId()+"]");
	}

	private void createDomainPending(String domain) throws epp_Exception, IOException, epp_XMLException
	{
		System.out.println("Creating the Domain Create command -- demostrating pending create in .AERO");

		epp_DomainCreateReq domain_create_request = new epp_DomainCreateReq();

		domain_create_request.setCmd( createEPPCommand() );
		domain_create_request.setName(domain); 

		epp_DomainPeriod period = new epp_DomainPeriod();
		// Note that some openrtk might not accept registration
		// periods by months.
		period.setUnit( epp_DomainPeriodUnitType.YEAR );
		period.setValue( (short) 2 );
		domain_create_request.setPeriod( period );

		domain_create_request.setRegistrant( contact_id1 );
		List domain_contacts = new ArrayList();
		// EPP Domain registries often require at least one
		// of each type of contact.
		domain_contacts.add( new epp_DomainContact( epp_DomainContactType.TECH, contact_id2 ) );
		domain_contacts.add( new epp_DomainContact( epp_DomainContactType.ADMIN, contact_id1 ) );
		domain_contacts.add( new epp_DomainContact( epp_DomainContactType.BILLING, contact_id2 ) );

		domain_create_request.setContacts( (epp_DomainContact[]) EPPXMLBase.convertListToArray((new epp_DomainContact()).getClass(), domain_contacts) );

		System.out.print("Dear registrant, please enter a passphrase for your new domain:(min 6, max 16) ");
		epp_AuthInfo domain_auth_info = getUserInputAuthInfo();
		domain_create_request.setAuthInfo( domain_auth_info );

		AeroDomain aero_domain_extension = new AeroDomain();
		aero_domain_extension.setCommand("create");

        EnsInfo ensInfo = new EnsInfo();
        List classes = new ArrayList();
        classes.add("airport");
        classes.add("default");
                                                                                                                             
        ensInfo.setEnsClass(classes);
        ensInfo.setRegistrantGroup("airport");
        ensInfo.setEnsO("sita");
        ensInfo.setRequestType("manual");
        ensInfo.setRegistrationType("ADA ADB BXN DLM ESB ISE IST NAV TZX");
        ensInfo.setCredentialsType("credentials type");
        ensInfo.setCredentialsValue("credentials value");
        ensInfo.setCodeValue("code value");
        ensInfo.setUniqueIdentifier("unique identifier");

		aero_domain_extension.setEnsInfo(ensInfo);

		epp_Extension[] extensions = {aero_domain_extension};
		domain_create_request.getCmd().setExtensions(extensions);

		// From an EPP perspective, nameserver associations are
		// optional for a domain, so we're not specifying them
		// here.  We will add them later in the domain update.

		EPPDomainCreate domain_create = new EPPDomainCreate();
		domain_create.setRequestData(domain_create_request);

		domain_create = (EPPDomainCreate) epp_client.processAction(domain_create);

		// We don't particularily care about the response here.
		// As long as an expection was not thrown, then the
		// creation was successful.  We'll get the expiration
		// date later in a domain info.
	}

	private void createDomain() throws epp_Exception, IOException, epp_XMLException
	{
		System.out.println("Creating the Domain Create command");
		epp_DomainCreateReq domain_create_request = new epp_DomainCreateReq();

		domain_create_request.setCmd( createEPPCommand() );

		domain_create_request.setName( domain_name );
		epp_DomainPeriod period = new epp_DomainPeriod();
		// Note that some openrtk might not accept registration
		// periods by months.
		period.setUnit( epp_DomainPeriodUnitType.YEAR );
		period.setValue( (short) 2 );
		domain_create_request.setPeriod( period );

		domain_create_request.setRegistrant( contact_id1 );
		List domain_contacts = new ArrayList();
		// EPP Domain registries often require at least one
		// of each type of contact.
		domain_contacts.add( new epp_DomainContact( epp_DomainContactType.TECH, contact_id2 ) );
		domain_contacts.add( new epp_DomainContact( epp_DomainContactType.ADMIN, contact_id1 ) );
		domain_contacts.add( new epp_DomainContact( epp_DomainContactType.BILLING, contact_id2 ) );

		domain_create_request.setContacts( (epp_DomainContact[]) EPPXMLBase.convertListToArray((new epp_DomainContact()).getClass(), domain_contacts) );

		System.out.print("Dear registrant, please enter a passphrase for your new domain:(min 6, max 16) ");
		epp_AuthInfo domain_auth_info = getUserInputAuthInfo();
		domain_create_request.setAuthInfo( domain_auth_info );

		AeroDomain aero_domain_extension = new AeroDomain();
		aero_domain_extension.setCommand("create");
		aero_domain_extension.setEnsAuthID(ens_auth_id1);
		aero_domain_extension.setEnsAuthKey(ens_auth_key1);

		epp_Extension[] extensions = {aero_domain_extension};
		domain_create_request.getCmd().setExtensions(extensions);

		// From an EPP perspective, nameserver associations are
		// optional for a domain, so we're not specifying them
		// here.  We will add them later in the domain update.

		EPPDomainCreate domain_create = new EPPDomainCreate();
		domain_create.setRequestData(domain_create_request);

		domain_create = (EPPDomainCreate) epp_client.processAction(domain_create);

		// We don't particularily care about the response here.
		// As long as an expection was not thrown, then the
		// creation was successful.  We'll get the expiration
		// date later in a domain info.
	}

	private void updateDomainENSConect() throws epp_Exception, IOException, epp_XMLException
    {
        System.out.println("Creating the Domain Update command");

        epp_DomainUpdateReq domain_update_request = new epp_DomainUpdateReq();
        domain_update_request.setCmd( createEPPCommand() );
        domain_update_request.setName( domain_name );

        epp_DomainUpdateChange change = new epp_DomainUpdateChange();
        domain_update_request.setChange(change);

        AeroDomain aero_domain_extension = new AeroDomain();
        aero_domain_extension.setCommand("update");
        aero_domain_extension.setEnsAuthID(ens_auth_id2);
        aero_domain_extension.setEnsAuthKey(ens_auth_key2);

        epp_Extension[] extensions = {aero_domain_extension};
        domain_update_request.getCmd().setExtensions(extensions);

        EPPDomainUpdate domain_update = new EPPDomainUpdate();
        domain_update.setRequestData(domain_update_request);

        // Now ask the EPPClient to process the request and retrieve
        // a response from the server.
        domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);
        // or, alternatively, this method can be used...
        //domain_update.fromXML(epp_client.processXML(domain_update.toXML()));

        epp_DomainUpdateRsp domain_update_response = domain_update.getResponseData();
        epp_Response response = domain_update_response.m_rsp;
        epp_Result[] results = response.m_results;
        System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
    }

	private void getDomainInfo(String domain_name) throws epp_XMLException, epp_Exception
	{
		System.out.println("Creating the Domain Info command");
		epp_DomainInfoReq domain_info_request = new epp_DomainInfoReq();

		domain_info_request.setCmd( createEPPCommand() );

		domain_info_request.setName( domain_name );

		EPPDomainInfo domain_info = new EPPDomainInfo();
		domain_info.setRequestData(domain_info_request);

		domain_info = (EPPDomainInfo) epp_client.processAction(domain_info);

		epp_DomainInfoRsp domain_info_response = domain_info.getResponseData();

		System.out.println("DomainInfo Results: registrant ["+domain_info_response.getRegistrant()+"]");
		System.out.println("DomainInfo Results: status count ["+domain_info_response.getStatus().length+"]");

		for ( int i = 0; i < domain_info_response.getStatus().length; i++ )
		{
			System.out.println("\tstatus["+i+"] string ["+EPPDomainBase.domainStatusToString( domain_info_response.getStatus()[i].getType() )+"]");
			System.out.println("\tstatus["+i+"] note ["+domain_info_response.getStatus()[i].getValue()+"]");
		}

		System.out.println("DomainInfo extension:");
		epp_Response response = domain_info_response.getRsp();
		String[] extensionStrings = response.getExtensionStrings();
		if ( extensionStrings != null ) {
			AeroDomain aero_domain_extension = new AeroDomain();
			aero_domain_extension.fromXML(extensionStrings[0]);
			System.out.println("ENS_Auth_ID ["+aero_domain_extension.getEnsAuthID()+"]");
		} else {
			System.out.println("Domain Info response contained no extension!!!");
		}
	}

    public static void main(String args[])
	{

		System.out.println("Start of the Session example");

		AeroSessionExample sessionExample = new AeroSessionExample();
		sessionExample.init(args);

		try
		{
			sessionExample.login();
			sessionExample.process();
			sessionExample.logout();
		}
		catch ( epp_XMLException xcp )
		{
			System.err.println("epp_XMLException! ["+xcp.getErrorMessage()+"]");
		}
		catch ( epp_Exception xcp )
		{
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.getDetails();
			// We're taking advantage epp_Result's toString() here
			// for debugging.  Take a look at the javadocs for
			// the full list of attributes in the class.
			System.err.println("\tresult: ["+results[0]+"]");
		}
		catch ( Exception xcp )
		{
			System.err.println("Exception! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
			xcp.printStackTrace();
		}
	}

	// generate unique client transaction id
	private String getClientTrid()
	{
		return "ABC:"+epp_client_id+":"+System.currentTimeMillis();
	}

	private epp_Command createEPPCommand()
	{
		epp_Command command_data = new epp_Command();
		command_data.setClientTrid( getClientTrid() );

		return command_data;
	}

	private epp_AuthInfo getUserInputAuthInfo() throws IOException
	{
		epp_AuthInfo authInfo = new epp_AuthInfo();
		BufferedReader buffed_reader = new BufferedReader(new InputStreamReader(System.in));
		while ( authInfo.getValue() == null ||
				authInfo.getValue().length() == 0 )
		{
			authInfo.setValue( buffed_reader.readLine() );
		}
		authInfo.setType( epp_AuthInfoType.PW );

		return authInfo;
	}
}
