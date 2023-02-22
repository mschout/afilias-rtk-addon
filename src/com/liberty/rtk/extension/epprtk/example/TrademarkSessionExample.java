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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/TrademarkSessionExample.java,v 1.2 2008/01/02 19:02:51 asimbirt Exp $
 * $Revision: 1.2 $
 * $Date: 2008/01/02 19:02:51 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.util.*;
import java.io.*;

import com.liberty.rtk.extension.epprtk.Trademark;
import com.liberty.rtk.extension.epprtk.TrademarkData;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;
import org.openrtk.idl.epprtk.contact.*;


/**
 * Example code for Liberty's Domain Trademark add-on to the RTK.
 * Uses domain create, info and update to demonstrate its usage.
 * The Domain Trademark is only required during the Sunrise period
 * of the .info registry.
 *
 * @author Daniel Manley
 * @version $Revision: 1.2 $ $Date: 2008/01/02 19:02:51 $
 * @see com.tucows.oxrs.epprtk.rtk.EPPClient
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPGreeting
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
**/
public class TrademarkSessionExample extends SessionExample
{
    private String domain_name = null;
    private String tld_name = null;
    private String contact_id1 = epp_client_id + "001";
    private String contact_id2 = epp_client_id + "002";

    /**
     * Main of the example.
     * Performs Domain create, info and update and demontrate
     * the usage of the DomainTrademark class in those commands.
     **/

    public static void main(String args[])
    {
        SessionExample example = new TrademarkSessionExample(args);
        example.session();
    }

    public TrademarkSessionExample(String args[])
    {
        super(args);
        domain_name = nextArgument();
        assertNotNull(domain_name);
        tld_name = getTld(domain_name);
        assertNotNull(tld_name);
    }

    private String getTld(String domainName)
    {
        int tldStart = domainName.lastIndexOf('.');
        if (tldStart < 0) 
            return null;

        return domainName.substring(tldStart + 1);
    }

    protected String getUsage()
    {
        return super.getUsage()
            + " domain_name";
    }

    protected void process() throws epp_Exception, IOException, epp_XMLException
    {
        if (!checkDomain())
        {
            System.out.println("domain " + domain_name + " exists in ." + tld_name + " registry, please choose another domain");
            System.exit(1);
        }
                                                                                                                              
        if (checkContact(contact_id1))
            createContact(contact_id1);
                                                                                                                                       if (checkContact(contact_id2))
            createContact(contact_id2);
                                                                                                                              
        createDomain();
        domainInfo();
        domainUpdate();
        // verify Domain Update result
        domainInfo();
    }

	private boolean checkDomain() throws epp_Exception, epp_XMLException
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

		if ( EPPXMLBase.getAvailResultFor(check_results, domain_name) == null )
            return false;

        return EPPXMLBase.getAvailResultFor(check_results, domain_name).booleanValue();
	}

	private boolean checkContact(String contact_id) throws epp_Exception, epp_XMLException
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

		TrademarkData data = new TrademarkData();
        data.setName("My Trademark");
        data.setCountry("CA");
        data.setNumber("19232");
        if ("mobi".equals(tld_name))
        {
            data.setAppDate("1700-11-25");
            data.setRegDate("1999-01-18");
        }
        else if("info".equals(tld_name))
        {
            data.setDate("2001-01-11");
        }
        else if("in".equals(tld_name))         
        {
            data.setDate("2001-01-11");
            data.setOwnerCountry("ca");
        }
        else 
        {
            throw new epp_XMLException("unsupported tld " + tld_name);
        }

        Trademark trademark = new Trademark();
        trademark.setTld(tld_name);
        trademark.setCommand("create");
        trademark.setTrademarkData(data);

		epp_Extension[] extensions = {trademark};
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

	private void domainInfo() throws epp_XMLException, epp_Exception
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

		System.out.println("Trademark extension:");
		epp_Response response = domain_info_response.getRsp();
		String[] extensionStrings = response.getExtensionStrings();
		if ( extensionStrings != null ) {
			Trademark trademark = new Trademark();
			trademark.fromXML(extensionStrings[0]);
			System.out.println("Trademark Data ["+ trademark.getTrademarkData() +"]");
		} else {
			System.out.println("Domain Info response contained no extension!!!");
		}
	}

	private void domainUpdate() throws epp_XMLException, epp_Exception
    {
        System.out.println("Creating the Domain Update command");
        epp_DomainUpdateReq domain_update_request = new epp_DomainUpdateReq();

        domain_update_request.setCmd( createEPPCommand() );
        domain_update_request.setName( domain_name );

		TrademarkData data = new TrademarkData();
        // if you want to remove some item please set its value to be ""
        // if you want to remove whole trademark, use data.setRemove(true);
        data.setName("My Trademark");
        data.setCountry("");
        data.setNumber("19232");
        if ("mobi".equals(tld_name))
        {
            data.setAppDate("1700-11-25");
            data.setRegDate("1999-01-18");
        }
        else if("info".equals(tld_name))
        {
            data.setDate("2001-01-11");
        }
        else if("in".equals(tld_name))
        {
            data.setDate("2001-01-11");
            data.setOwnerCountry("ca");
        }
        else 
        {
            throw new epp_XMLException("unsupported tld " + tld_name);
        }

        Trademark trademark = new Trademark();
        trademark.setTld(tld_name);
        trademark.setCommand("update");
        trademark.setTrademarkData(data);

        domain_update_request.getCmd().setExtension(trademark);

        EPPDomainUpdate domain_update = new EPPDomainUpdate();
        domain_update.setRequestData(domain_update_request);

        domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);

        epp_DomainUpdateRsp domain_update_response = domain_update.getResponseData();
        epp_Response response = domain_update_response.m_rsp;
        epp_Result[] results = response.m_results;
        System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
    }
}
