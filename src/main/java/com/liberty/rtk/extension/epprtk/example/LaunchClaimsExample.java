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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;

import org.openrtk.idl.epprtk.epp_AuthInfo;
import org.openrtk.idl.epprtk.epp_AuthInfoType;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Exception;
import org.openrtk.idl.epprtk.epp_Greeting;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateChange;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;

import com.liberty.rtk.extension.epprtk.LaunchCd;
import com.liberty.rtk.extension.epprtk.LaunchCheck;
import com.liberty.rtk.extension.epprtk.LaunchCreate;
import com.liberty.rtk.extension.epprtk.LaunchInfo;
import com.liberty.rtk.extension.epprtk.LaunchNotice;
import com.liberty.rtk.extension.epprtk.LaunchUpdate;
import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.RTKBase;
import com.tucows.oxrs.epprtk.rtk.transport.EPPTransportException;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

public class LaunchClaimsExample {
	private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.LaunchClaimsExample epp_host_name epp_host_port epp_client_id epp_password domain_name noticeID notAfter acceptedDate";
	private static epp_Command command_data = null;
	private static String client_trid;
	private static EPPClient epp_client;

	private static String domain;
	private static String noticeID;
	private static String notAfter;
	private static String acceptedDate;
	
	private static String applicationID;

	epp_AuthInfo domain_auth_info = null;
	Date domain_exp_date = null;

	public static void main(String args[]) {
		System.out.println("Start of the Launch Claims example");

		try {
			if (args.length != 8) {
				System.err.println(USAGE);
				System.exit(1);
			}

			RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_ONE);

			processInputAndLogin(args);

			makeClaimsCheck();
			makeDomainClaimsCreate();
			makeDomainInfo();
			makeDomainUpdate();

			System.out.println("===============================");
			System.out.println("Logging out from the EPP Server");
			epp_client.logout(client_trid);

			System.out.println("Disconnecting from the EPP Server");
			epp_client.disconnect();

		} catch (epp_XMLException xcp) {
			System.err.println("epp_XMLException! [" + xcp.m_error_message
					+ "]");
		} catch (epp_Exception xcp) {
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.m_details;
			System.err.println("\tcode: [" + results[0].m_code + "] lang: ["
					+ results[0].m_lang + "] msg: [" + results[0].m_msg + "]");
			if (results[0].m_values != null && results[0].m_values.length > 0) {
				System.err.println("\tvalue: [" + results[0].m_values[0] + "]");
			}
		} catch (Exception xcp) {
			System.err.println("Exception! [" + xcp.getClass().getName()
					+ "] [" + xcp.getMessage() + "]");
			xcp.printStackTrace();
		}

	}

	private static void processInputAndLogin(String[] args)
			throws epp_Exception, IOException, epp_XMLException,
			EPPTransportException {
		String epp_host_name = args[0];
		String epp_host_port_string = args[1];
		String epp_client_id = args[2];
		String epp_password = args[3];
		domain = args[4];
		noticeID = args[5];
		notAfter = args[6];
		acceptedDate = args[7];

		makeEppClient(epp_host_name, epp_client_id, epp_password, epp_host_port_string);

		setDebugLevel();

		connectAndGetGreeding();

		setClientTrid(epp_client_id);

		loginEppServer();
	}	

	private static void makeEppClient(String epp_host_name,
			String epp_client_id, String epp_password, String epp_host_port_string) {
		int epp_host_port = Integer.parseInt(epp_host_port_string);
		
		epp_client = new EPPClient(epp_host_name, epp_host_port,
				epp_client_id, epp_password);

		epp_client.setLang("en");
	}

	private static void setDebugLevel() {
		RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_THREE);
	}

	private static void connectAndGetGreeding() throws epp_Exception,
			IOException, epp_XMLException, EPPTransportException {
		System.out
		.println("Connecting to the EPP Server and getting the greeting");
		
		epp_Greeting greeting = epp_client.connectAndGetGreeting();

		System.out.println("greeting's server: [" + greeting.m_server_id
				+ "]");
		System.out.println("greeting's server-date: ["
				+ greeting.m_server_date + "]");
	}
	
	private static void setClientTrid(String epp_client_id) {
		client_trid = "ABC:" + epp_client_id + ":123";
	}
	
	private static void loginEppServer() throws epp_XMLException, epp_Exception {
		System.out.println("===============================");
		System.out.println("Logging into the EPP Server");
		epp_client.login(client_trid);
	}

	private static void makeClaimsCheck() {
		try {
			tryMakeClaimsCheck();
		} catch (epp_XMLException xcp) {
			// Either the request was missing some required data in
			// validation before sending to the server, or the server's
			// response was either unparsable or missing some required data.
			System.err.println("epp_XMLException! [" + xcp.m_error_message
					+ "]");
		} catch (epp_Exception xcp) {
			// The EPP Server has responded with an error code with
			// some optional messages to describe the error.
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.m_details;
			System.err.println("\tcode: [" + results[0].m_code + "] lang: ["
					+ results[0].m_lang + "] msg: [" + results[0].m_msg + "]");
			if (results[0].m_values != null && results[0].m_values.length > 0) {
				System.err.println("\tvalue: [" + results[0].m_values[0] + "]");
			}
		} catch (Exception xcp) {
			// Other unexpected exceptions
			System.err
					.println("Domain Check failed! ["
							+ xcp.getClass().getName() + "] ["
							+ xcp.getMessage() + "]");
			xcp.printStackTrace();
		}
	}

	private static void tryMakeClaimsCheck() throws epp_Exception,
			epp_XMLException {
		System.out.println("===============================");
		System.out.println("Sending claims check command.");
		EPPDomainCheck domainCheck = makeDomainCheck();

		domainCheck = (EPPDomainCheck) epp_client.processAction(domainCheck);

		processCheckResponse(domainCheck);
	}

	private static EPPDomainCheck makeDomainCheck() {
		epp_DomainCheckReq domainCheckRequest = makeDomainCheckRequest();

		EPPDomainCheck domainCheck = new EPPDomainCheck();
		domainCheck.setRequestData(domainCheckRequest);
		return domainCheck;
	}	

	@SuppressWarnings("deprecation")
	private static epp_DomainCheckReq makeDomainCheckRequest() {
		epp_DomainCheckReq domainCheckRequest = new epp_DomainCheckReq();

		command_data = new epp_Command();
		// The client trid is optional. it's main use
		// is for registrar tracking and logging of requests,
		// especially for data creation or modification requests
		command_data.m_client_trid = client_trid;
		domainCheckRequest.m_cmd = command_data;

		String[] names = { domain };
		domainCheckRequest.m_names = names;

		LaunchCheck launchCheck = new LaunchCheck("claims", "claims");

		domainCheckRequest.getCmd().setExtension(launchCheck);
		return domainCheckRequest;
	}

	private static void processCheckResponse(EPPDomainCheck domainCheck)
			throws epp_XMLException {
		epp_DomainCheckRsp domainCheckResponse = domainCheck.getResponseData();
		epp_Response response = domainCheckResponse.m_rsp;

		String[] extensionStrings = response.getExtensionStrings();
        if ( extensionStrings != null ) {
             processCheckExtension(extensionStrings[0]);
        }
	}

	private static void processCheckExtension(String extensionString)
			throws epp_XMLException {
		LaunchCheck launchCheckExtension = new LaunchCheck();
		launchCheckExtension.fromXML(extensionString);

		processPhase(launchCheckExtension.getPhase());
	
		processlaunchCds(launchCheckExtension.getLaunchCd());
	}

	private static void processPhase(String phase) {
		System.out.println("Phase: " + phase);
	}

	private static void processlaunchCds(Collection<LaunchCd> launchCds) {
		for (LaunchCd launchCd : launchCds) {
			if (launchCd.getClaimsKey() != null && !launchCd.getClaimsKey().isEmpty()) {
				System.out.println("Name: " + launchCd.getName() + ", claimsKey: "
					+ launchCd.getClaimsKey() + ", exists: "
					+ launchCd.getExists());
			} else {
				System.out.println("Name: " + launchCd.getName() + ", exists: "
						+ launchCd.getExists());
			}
		}
	}

	private static void makeDomainClaimsCreate() {
		try {
			tryMakeClaimsCreate();
		} catch (epp_XMLException xcp) {
			// Either the request was missing some required data in
			// validation before sending to the server, or the server's
			// response was either unparsable or missing some required data.
			System.err.println("epp_XMLException! [" + xcp.m_error_message
					+ "]");
		} catch (epp_Exception xcp) {
			// The EPP Server has responded with an error code with
			// some optional messages to describe the error.
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.m_details;
			System.err.println("\tcode: [" + results[0].m_code + "] lang: ["
					+ results[0].m_lang + "] msg: [" + results[0].m_msg + "]");
			if (results[0].m_values != null && results[0].m_values.length > 0) {
				System.err.println("\tvalue: [" + results[0].m_values[0] + "]");
			}
		} catch (Exception xcp) {
			// Other unexpected exceptions
			System.err
					.println("Domain Create failed! ["
							+ xcp.getClass().getName() + "] ["
							+ xcp.getMessage() + "]");
			xcp.printStackTrace();
		}

	}
	
	private static void tryMakeClaimsCreate() throws epp_Exception,
			epp_XMLException, IOException {
		System.out.println("===============================");
		System.out.println("Sending claims create command.");
		EPPDomainCreate domainCreate = makeDomainCreate();

		domainCreate = (EPPDomainCreate) epp_client.processAction(domainCreate);

		processCreateResponse(domainCreate);
	}
	
	private static EPPDomainCreate makeDomainCreate() throws IOException {
		epp_DomainCreateReq domainCreateRequest = makeDomainCreateRequest();

		EPPDomainCreate domainCreate = new EPPDomainCreate();
		domainCreate.setRequestData(domainCreateRequest);
		return domainCreate;
	}	

	@SuppressWarnings("deprecation")
	private static epp_DomainCreateReq makeDomainCreateRequest() throws IOException {
		epp_DomainCreateReq domainCreateRequest = new epp_DomainCreateReq();

		command_data = new epp_Command();
		// The client trid is optional. it's main use
		// is for registrar tracking and logging of requests,
		// especially for data creation or modification requests
		command_data.m_client_trid = client_trid;
		domainCreateRequest.m_cmd = command_data;

		domainCreateRequest.m_name = domain;
		makePeriod(domainCreateRequest);
		
		makeAuthInfo(domainCreateRequest);
				
		LaunchCreate launchCreate = makeLaunchCreate();

		domainCreateRequest.getCmd().setExtension(launchCreate);
		return domainCreateRequest;
	}

	private static void makePeriod(epp_DomainCreateReq domainCreateRequest) {
		epp_DomainPeriod period = new epp_DomainPeriod();
		// Note that some openrtk might not accept registration
		// periods by months.
		period.setUnit( epp_DomainPeriodUnitType.YEAR );
		period.setValue( (short) 2 );
		domainCreateRequest.m_period = period;
	}
	
	private static void makeAuthInfo(epp_DomainCreateReq domainCreateRequest)
			throws IOException {
		System.out.print("Dear registrant, please enter a passphrase for your new domain:(min 6, max 16) ");
		epp_AuthInfo domain_auth_info = getUserInputAuthInfo();
		domainCreateRequest.m_auth_info = domain_auth_info;
	}
	
	private static epp_AuthInfo getUserInputAuthInfo() throws IOException
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

	private static LaunchCreate makeLaunchCreate() {
		LaunchNotice launchNotice = makeLaunchNotice();
		LaunchCreate launchCreate = new LaunchCreate("claims", launchNotice);
		
		return launchCreate;
	}
	
	private static LaunchNotice makeLaunchNotice() {
		LaunchNotice launchNotice = new LaunchNotice(noticeID, notAfter, acceptedDate);

		return launchNotice;
	}	
	
	private static void processCreateResponse(EPPDomainCreate domainCreate)
			throws epp_XMLException {
		epp_DomainCreateRsp domainCreateResponse = domainCreate.getResponseData();
		epp_Response response = domainCreateResponse.m_rsp;

		String[] extensionStrings = response.getExtensionStrings();
        if ( extensionStrings != null ) {
             processCreateExtension(extensionStrings[0]);
        }
	}
	
	private static void processCreateExtension(String extensionString)
			throws epp_XMLException {
		LaunchCreate launchCreateExtension = new LaunchCreate();
		launchCreateExtension.fromXML(extensionString);

		if (launchCreateExtension.getPhase() != null && !launchCreateExtension.getPhase().isEmpty()) 
			processPhase(launchCreateExtension.getPhase());
	
		if (launchCreateExtension.getApplicationID() != null && !launchCreateExtension.getApplicationID().isEmpty())
			processApplicationID(launchCreateExtension.getApplicationID());
	}
	
	private static void processApplicationID(String id) {
		applicationID = id;
		System.out.println("Application ID: " + id);
	}

	private static void makeDomainInfo() {
		try {
			tryMakeInfoCommand();
		} catch (epp_XMLException xcp) {
			// Either the request was missing some required data in
			// validation before sending to the server, or the server's
			// response was either unparsable or missing some required data.
			System.err.println("epp_XMLException! [" + xcp.m_error_message
					+ "]");
		} catch (epp_Exception xcp) {
			// The EPP Server has responded with an error code with
			// some optional messages to describe the error.
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.m_details;
			System.err.println("\tcode: [" + results[0].m_code + "] lang: ["
					+ results[0].m_lang + "] msg: [" + results[0].m_msg + "]");
			if (results[0].m_values != null && results[0].m_values.length > 0) {
				System.err.println("\tvalue: [" + results[0].m_values[0] + "]");
			}
		} catch (Exception xcp) {
			// Other unexpected exceptions
			System.err
					.println("Domain Info failed! ["
							+ xcp.getClass().getName() + "] ["
							+ xcp.getMessage() + "]");
			xcp.printStackTrace();
		}
	}
	
	private static void tryMakeInfoCommand() throws epp_Exception,
			epp_XMLException, IOException {
		System.out.println("===============================");
		System.out.println("Sending claims info command.");
		EPPDomainInfo domainInfo = makeDomainInfoReq();

		domainInfo = (EPPDomainInfo) epp_client.processAction(domainInfo);

		processInfoResponse(domainInfo);
	}
	
	private static EPPDomainInfo makeDomainInfoReq() throws IOException {
		epp_DomainInfoReq domainCreateRequest = makeDomainInfoRequest();

		EPPDomainInfo domainInfo = new EPPDomainInfo();
		domainInfo.setRequestData(domainCreateRequest);
		return domainInfo;
	}	

	@SuppressWarnings("deprecation")
	private static epp_DomainInfoReq makeDomainInfoRequest() throws IOException {
		epp_DomainInfoReq domainInfoRequest = new epp_DomainInfoReq();

		command_data = new epp_Command();
		// The client trid is optional. it's main use
		// is for registrar tracking and logging of requests,
		// especially for data creation or modification requests
		command_data.m_client_trid = client_trid;
		domainInfoRequest.m_cmd = command_data;

		domainInfoRequest.m_name = domain;
				
		LaunchInfo launchInfo = makeLaunchInfo();

		domainInfoRequest.getCmd().setExtension(launchInfo);
		return domainInfoRequest;
	}
	
	private static LaunchInfo makeLaunchInfo() {
		LaunchInfo launchInfo = new LaunchInfo("claims", applicationID, false);
		
		return launchInfo;
	}
	
	private static void processInfoResponse(EPPDomainInfo domainInfo)
			throws epp_XMLException {
		epp_DomainInfoRsp domainInfoResponse = domainInfo.getResponseData();
		epp_Response response = domainInfoResponse.m_rsp;

		String[] extensionStrings = response.getExtensionStrings();
        if ( extensionStrings != null ) {
             processInfoExtension(extensionStrings[0]);
        }
	}
	
	private static void processInfoExtension(String extensionString)
			throws epp_XMLException {
		LaunchInfo launchInfoExtension = new LaunchInfo();
		launchInfoExtension.fromXML(extensionString);

		if (launchInfoExtension.getPhase() != null && !launchInfoExtension.getPhase().isEmpty()) 
			processPhase(launchInfoExtension.getPhase());
	
		if (launchInfoExtension.getApplicationID() != null && !launchInfoExtension.getApplicationID().isEmpty())
			processApplicationID(launchInfoExtension.getApplicationID());
		
		if (launchInfoExtension.getLaunchStatus() != null && !launchInfoExtension.getLaunchStatus().isEmpty())
			processLaunchStatus(launchInfoExtension.getLaunchStatus());
	}
	
	private static void processLaunchStatus(String status) {
		System.out.println("Launch Status: " + status);
	}

	private static void makeDomainUpdate() {
		try {
			tryMakeUpdateCommand();
		} catch (epp_XMLException xcp) {
			// Either the request was missing some required data in
			// validation before sending to the server, or the server's
			// response was either unparsable or missing some required data.
			System.err.println("epp_XMLException! [" + xcp.m_error_message
					+ "]");
		} catch (epp_Exception xcp) {
			// The EPP Server has responded with an error code with
			// some optional messages to describe the error.
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.m_details;
			System.err.println("\tcode: [" + results[0].m_code + "] lang: ["
					+ results[0].m_lang + "] msg: [" + results[0].m_msg + "]");
			if (results[0].m_values != null && results[0].m_values.length > 0) {
				System.err.println("\tvalue: [" + results[0].m_values[0] + "]");
			}
		} catch (Exception xcp) {
			// Other unexpected exceptions
			System.err
					.println("Domain Update failed! ["
							+ xcp.getClass().getName() + "] ["
							+ xcp.getMessage() + "]");
			xcp.printStackTrace();
		}
	}
	
	private static void tryMakeUpdateCommand() throws epp_Exception,
			epp_XMLException, IOException {
		System.out.println("===============================");
		System.out.println("Sending claims Update command.");
		EPPDomainUpdate domainUpdate = makeDomainUpdateReq();

		domainUpdate = (EPPDomainUpdate) epp_client.processAction(domainUpdate);
	}

	private static EPPDomainUpdate makeDomainUpdateReq() throws IOException {
		epp_DomainUpdateReq domainUpdateRequest = makeDomainUpdateRequest();

		EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
		domainUpdate.setRequestData(domainUpdateRequest);
		return domainUpdate;
	}

	@SuppressWarnings("deprecation")
	private static epp_DomainUpdateReq makeDomainUpdateRequest() throws IOException {
		epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();

		command_data = new epp_Command();
		// The client trid is optional. it's main use
		// is for registrar tracking and logging of requests,
		// especially for data creation or modification requests
		command_data.m_client_trid = client_trid;
		domainUpdateRequest.m_cmd = command_data;

		domainUpdateRequest.m_name = domain;
		
		epp_DomainUpdateChange change = makeDomainUpdateChange();
        
        domainUpdateRequest.setChange(change);

		LaunchUpdate launchUpdate = makeLaunchUpdate();

		domainUpdateRequest.getCmd().setExtension(launchUpdate);
		return domainUpdateRequest;
	}

	private static epp_DomainUpdateChange makeDomainUpdateChange() {
		epp_DomainUpdateChange change = new epp_DomainUpdateChange();

		return change;
	}
	
	private static LaunchUpdate makeLaunchUpdate() {
		LaunchUpdate launchUpdate = new LaunchUpdate("claims", applicationID);
		
		return launchUpdate;
	}
}
