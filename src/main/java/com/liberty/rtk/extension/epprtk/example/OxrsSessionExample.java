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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/OxrsSessionExample.java,v 1.2 2010/08/12 17:31:41 dongjinkim Exp $
 * $Revision: 1.2 $
 * $Date: 2010/08/12 17:31:41 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrtk.idl.epprtk.epp_AuthInfo;
import org.openrtk.idl.epprtk.epp_CheckResult;
import org.openrtk.idl.epprtk.epp_Exception;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.contact.epp_ContactAddress;
import org.openrtk.idl.epprtk.contact.epp_ContactCheckReq;
import org.openrtk.idl.epprtk.contact.epp_ContactCheckRsp;
import org.openrtk.idl.epprtk.contact.epp_ContactCreateReq;
import org.openrtk.idl.epprtk.contact.epp_ContactCreateRsp;
import org.openrtk.idl.epprtk.contact.epp_ContactNameAddress;
import org.openrtk.idl.epprtk.contact.epp_ContactPhone;
import org.openrtk.idl.epprtk.contact.epp_ContactPostalInfoType;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainContact;
import org.openrtk.idl.epprtk.domain.epp_DomainContactType;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import com.liberty.rtk.extension.epprtk.Ipr;
import com.liberty.rtk.extension.epprtk.IprData;
import com.liberty.rtk.extension.epprtk.OxrsDomain;
import com.tucows.oxrs.epprtk.rtk.xml.EPPContactCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPContactCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainBase;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Example code for a typical logical EPP sessions to demonstrate 
 * usage of com.liberty.rtk.extension.epprtk.OxrsDomain.
 *
 * @author Anna Simbirtsev 
 * @version $Revision: 1.2 $ $Date: 2010/08/12 17:31:41 $
**/
public class OxrsSessionExample extends SessionExample
{
    private String domain_name = null;
    private String contact_id1 = epp_client_id + "001";
    private String contact_id2 = epp_client_id + "002";
    private String maintainerURL = null;
    private String iprType = null;
    private String domainRoid = null;

	public OxrsSessionExample(String args[])
	{
        super(args);
        domain_name = nextArgument();
        maintainerURL = nextArgument();
        iprType = nextArgument();
        
        assertNotNull(domain_name);
	}

    public static void main(String args[])
    {
        SessionExample example = new OxrsSessionExample(args);
        example.session();
    }

    protected String getUsage()
    {
        return super.getUsage()
            + " domain_name maintainerURL iprType";
    }

	protected void process() throws epp_Exception, IOException, epp_XMLException
    {
        if (!checkDomain())
        {
			System.out.println("domain " + domain_name + " exists in the registry, please choose another domain");
			System.exit(1);
        }

        if (checkContact(contact_id1))
            createContact(contact_id1);

        if (checkContact(contact_id2))
            createContact(contact_id2);
        
        createDomain();
        domainInfo();
        domainUpdate();
    }

	private boolean checkDomain() throws epp_Exception, epp_XMLException
	{
		System.out.println("Creating the Domain Check command");
		epp_DomainCheckReq domain_check_request = new epp_DomainCheckReq();

		domain_check_request.setCmd( createEPPCommand() );

		List domain_list = new ArrayList();
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

		List contact_list = new ArrayList();
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

		OxrsDomain oxrs_domain_extension = new OxrsDomain();
		oxrs_domain_extension.setCommand("create");
		
        oxrs_domain_extension.setMaintainerURL(
            maintainerURL != null ? maintainerURL : "www.afilias.info");

        IprData data = new IprData();
        data.setName("My Ipr");
        data.setCountry("CA");
        data.setNumber("19232");
        data.setAppDate("1700-11-25");
        data.setRegDate("1999-01-18");

        if (iprType != null ) data.setType(iprType);
        else  data.setType("SUNRISE");

        Ipr ipr_t = new Ipr();
        ipr_t.setCommand("create");
        ipr_t.setTld("oxrs");
        ipr_t.setIprData(data);                

        domain_create_request.m_cmd.m_extensions = new epp_Extension[2];
        domain_create_request.m_cmd.m_extensions[0] = oxrs_domain_extension;
        domain_create_request.m_cmd.m_extensions[1] = ipr_t;

		// From an EPP perspective, nameserver associations are
		// optional for a domain, so we're not specifying them
		// here.  We will add them later in the domain update.

		EPPDomainCreate domain_create = new EPPDomainCreate();
		domain_create.setRequestData(domain_create_request);

		domain_create = (EPPDomainCreate) epp_client.processAction(domain_create);

        epp_DomainCreateRsp domain_create_response = domain_create.getResponseData();
  
        epp_Response response = domain_create_response.getRsp();
        String[] extensionStrings = response.getExtensionStrings();
        
        if ( extensionStrings != null ) {
             OxrsDomain oxrs_domain_extension2 = new OxrsDomain();
             oxrs_domain_extension2.fromXML(extensionStrings[0]);

             domainRoid = oxrs_domain_extension2.getDomainRoid();
             System.out.println("DomainRoid: " + domainRoid);
        }
	}

	private void domainInfo() throws epp_XMLException, epp_Exception
	{
		System.out.println("Creating the Domain Info command");
		epp_DomainInfoReq domain_info_request = new epp_DomainInfoReq();

		domain_info_request.setCmd( createEPPCommand() );

		domain_info_request.setName(domain_name);

        OxrsDomain oxrs_domain_extension = new OxrsDomain();
        oxrs_domain_extension.setCommand("info");
        oxrs_domain_extension.setDomainRoid(domainRoid != null ? domainRoid : "D123-LRMS"); 
 
        epp_Extension[] extensions = {oxrs_domain_extension};
        domain_info_request.getCmd().setExtensions(extensions); 

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
			OxrsDomain oxrs_domain_extension1 = new OxrsDomain();
			oxrs_domain_extension1.fromXML(extensionStrings[0]);
			System.out.println("MaintainerURL [" + oxrs_domain_extension1.getMaintainerURL() + "]");
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

        OxrsDomain oxrs_domain_extension = new OxrsDomain();
        oxrs_domain_extension.setCommand("update");

        oxrs_domain_extension.setDomainRoid(domainRoid != null ? domainRoid : "D123-LRMS");

        oxrs_domain_extension.setChg(true);
        oxrs_domain_extension.setMaintainerURL(maintainerURL != null ? maintainerURL : "www.afilias.info");

        //Collection extensions = new ArrayList();
        //extensions.add(oxrs_domain_extension);
        //domain_update_request.getCmd().setExtensions((epp_Extension[]) extensions.toArray());

	domain_update_request.m_cmd.m_extensions = new epp_Extension[1];
        domain_update_request.m_cmd.m_extensions[0] = oxrs_domain_extension;

        EPPDomainUpdate domain_update = new EPPDomainUpdate();
        domain_update.setRequestData(domain_update_request);

        domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);

        epp_DomainUpdateRsp domain_update_response = domain_update.getResponseData();
        epp_Response response = domain_update_response.getRsp();
        epp_Result[] results = response.m_results;
        System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
    }
}
