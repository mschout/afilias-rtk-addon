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
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateChange;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;

import com.liberty.rtk.extension.epprtk.LaunchCreate;
import com.liberty.rtk.extension.epprtk.LaunchInfo;
import com.liberty.rtk.extension.epprtk.LaunchUpdate;
import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.RTKBase;
import com.tucows.oxrs.epprtk.rtk.transport.EPPTransportException;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

public class LaunchSunriseExample {
	private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.LaunchSunriseExample epp_host_name epp_host_port epp_client_id epp_password domain_name";
	private static epp_Command command_data = null;
	private static String client_trid;
	private static EPPClient epp_client;

	private static String domain;
	
	private static String applicationID;

	epp_AuthInfo domain_auth_info = null;
	Date domain_exp_date = null;

	public static void main(String args[]) {
		System.out.println("Start of the Launch Claims example");

		try {
			if (args.length != 5) {
				System.err.println(USAGE);
				System.exit(1);
			}

			RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_ONE);

			processInputAndLogin(args);

			makeDomainSunriseCreate();
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

	private static void processPhase(String phase) {
		System.out.println("Phase: " + phase);
	}
	
	private static void makeDomainSunriseCreate() {
		try {
			tryMakeSunriseCreate();
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
	
	private static void tryMakeSunriseCreate() throws epp_Exception,
			epp_XMLException, IOException {
		System.out.println("===============================");
		System.out.println("Sending sunrise create command.");
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
		String signedMark = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNtZDpzaWdu"
				+ "ZWRNYXJrIHhtbG5zOnNtZD0idXJuOmlldGY6cGFyYW1zOnhtbDpuczpzaWduZWRN"
				+ "YXJrLTEuMCIgaWQ9InNpZ25lZE1hcmsiPgogIDxzbWQ6aWQ+MS0yPC9zbWQ6aWQ+"
				+ "CiAgPHNtZDppc3N1ZXJJbmZvIGlzc3VlcklEPSIyIj4KICAgIDxzbWQ6b3JnPkV4"
				+ "YW1wbGUgSW5jLjwvc21kOm9yZz4KICAgIDxzbWQ6ZW1haWw+c3VwcG9ydEBleGFt"
				+ "cGxlLnRsZDwvc21kOmVtYWlsPgogICAgPHNtZDp1cmw+aHR0cDovL3d3dy5leGFt"
				+ "cGxlLnRsZDwvc21kOnVybD4KICAgIDxzbWQ6dm9pY2UgeD0iMTIzNCI+KzEuNzAz"
				+ "NTU1NTU1NTwvc21kOnZvaWNlPgogIDwvc21kOmlzc3VlckluZm8+CiAgPHNtZDpu"
				+ "b3RCZWZvcmU+MjAwOS0wOC0xNlQwOTowMDowMC4wWjwvc21kOm5vdEJlZm9yZT4K"
				+ "ICA8c21kOm5vdEFmdGVyPjIwMTAtMDgtMTZUMDk6MDA6MDAuMFo8L3NtZDpub3RB"
				+ "ZnRlcj4KICA8bWFyazptYXJrIHhtbG5zOm1hcms9InVybjppZXRmOnBhcmFtczp4"
				+ "bWw6bnM6bWFyay0xLjAiPgogICAgPG1hcms6dHJhZGVtYXJrPgogICAgICA8bWFy"
				+ "azppZD4xMjM0LTI8L21hcms6aWQ+CiAgICAgIDxtYXJrOm1hcmtOYW1lPkV4YW1w"
				+ "bGUgT25lPC9tYXJrOm1hcmtOYW1lPgogICAgICA8bWFyazpob2xkZXIgZW50aXRs"
				+ "ZW1lbnQ9Im93bmVyIj4KICAgICAgICA8bWFyazpvcmc+RXhhbXBsZSBJbmMuPC9t"
				+ "YXJrOm9yZz4KICAgICAgICA8bWFyazphZGRyPgogICAgICAgICAgPG1hcms6c3Ry"
				+ "ZWV0PjEyMyBFeGFtcGxlIERyLjwvbWFyazpzdHJlZXQ+CiAgICAgICAgICA8bWFy"
				+ "azpzdHJlZXQ+U3VpdGUgMTAwPC9tYXJrOnN0cmVldD4KICAgICAgICAgIDxtYXJr"
				+ "OmNpdHk+UmVzdG9uPC9tYXJrOmNpdHk+CiAgICAgICAgICA8bWFyazpzcD5WQTwv"
				+ "bWFyazpzcD4KICAgICAgICAgIDxtYXJrOnBjPjIwMTkwPC9tYXJrOnBjPgogICAg"
				+ "ICAgICAgPG1hcms6Y2M+VVM8L21hcms6Y2M+CiAgICAgICAgPC9tYXJrOmFkZHI+"
				+ "CiAgICAgIDwvbWFyazpob2xkZXI+CiAgICAgIDxtYXJrOmp1cmlzZGljdGlvbj5V"
				+ "UzwvbWFyazpqdXJpc2RpY3Rpb24+CiAgICAgIDxtYXJrOmNsYXNzPjM1PC9tYXJr"
				+ "OmNsYXNzPgogICAgICA8bWFyazpjbGFzcz4zNjwvbWFyazpjbGFzcz4KICAgICAg"
				+ "PG1hcms6bGFiZWw+ZXhhbXBsZS1vbmU8L21hcms6bGFiZWw+CiAgICAgIDxtYXJr"
				+ "OmxhYmVsPmV4YW1wbGVvbmU8L21hcms6bGFiZWw+CiAgICAgIDxtYXJrOmdvb2Rz"
				+ "QW5kU2VydmljZXM+RGlyaWdlbmRhcyBldCBlaXVzbW9kaQogICAgICAgIGZlYXR1"
				+ "cmluZyBpbmZyaW5nbyBpbiBhaXJmYXJlIGV0IGNhcnRhbSBzZXJ2aWNpYS4KICAg"
				+ "ICAgPC9tYXJrOmdvb2RzQW5kU2VydmljZXM+IAogICAgICA8bWFyazpyZWdOdW0+"
				+ "MjM0MjM1PC9tYXJrOnJlZ051bT4KICAgICAgPG1hcms6cmVnRGF0ZT4yMDA5LTA4"
				+ "LTE2VDA5OjAwOjAwLjBaPC9tYXJrOnJlZ0RhdGU+CiAgICAgIDxtYXJrOmV4RGF0"
				+ "ZT4yMDE1LTA4LTE2VDA5OjAwOjAwLjBaPC9tYXJrOmV4RGF0ZT4KICAgIDwvbWFy"
				+ "azp0cmFkZW1hcms+CiAgPC9tYXJrOm1hcms+CiAgPFNpZ25hdHVyZSB4bWxucz0i"
				+ "aHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+CiAgICA8U2lnbmVk"
				+ "SW5mbz4KICAgICAgPENhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJo"
				+ "dHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz4KICAgICAg"
				+ "PFNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIw"
				+ "MDEvMDQveG1sZHNpZy1tb3JlI3JzYS1zaGEyNTYiLz4KICAgICAgPFJlZmVyZW5j"
				+ "ZSBVUkk9IiNzaWduZWRNYXJrIj4KICAgICAgICA8VHJhbnNmb3Jtcz4KICAgICAg"
				+ "ICAgIDxUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAw"
				+ "LzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPgogICAgICAgIDwvVHJh"
				+ "bnNmb3Jtcz4KICAgICAgICA8RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDov"
				+ "L3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjc2hhMjU2Ii8+CiAgICAgICAgPERp"
				+ "Z2VzdFZhbHVlPm1pRjRNMmFUZDFZM3RLT3pKdGl5bDJWcHpBblZQblYxSHE3WmF4"
				+ "K3l6ckE9PC9EaWdlc3RWYWx1ZT4KICAgICAgPC9SZWZlcmVuY2U+CiAgICA8L1Np"
				+ "Z25lZEluZm8+CiAgICA8U2lnbmF0dXJlVmFsdWU+TUVMcEhUV0VWZkcxSmNzRzEv"
				+ "YS8vbzU0T25sSjVBODY0K1g1SndmcWdHQkJlWlN6R0hOend6VEtGekl5eXlmbgps"
				+ "R3hWd05Nb0JWNWFTdmtGN29FS01OVnpmY2wvUDBjek5RWi9MSjgzcDNPbDI3L2lV"
				+ "TnNxZ0NhR2Y5WnVwdytNClhUNFEybE9ySXcrcVN4NWc3cTlUODNzaU1MdmtENXVF"
				+ "WWxVNWRQcWdzT2JMVFc4L2RvVFFyQTE0UmN4Z1k0a0cKYTQrdDVCMWNUKzVWYWdo"
				+ "VE9QYjh1VVNFREtqbk9zR2R5OHAyNHdneUs5bjhoMENUU1MyWlE2WnEvUm1RZVQ3"
				+ "RApzYmNlVUhoZVErbWtRV0lsanBNUXFzaUJqdzVYWGg0amtFZ2ZBenJiNmdrWUVG"
				+ "K1g4UmV1UFp1T1lDNFFqSUVUCnl4OGlmTjRLRTNHSWJNWGVGNExEc0E9PTwvU2ln"
				+ "bmF0dXJlVmFsdWU+CiAgICA8S2V5SW5mbz4KICAgICAgPEtleVZhbHVlPgo8UlNB"
				+ "S2V5VmFsdWU+CjxNb2R1bHVzPgpvL2N3dlhoYlZZbDBSRFdXdm95ZVpwRVRWWlZW"
				+ "Y01Db3ZVVk5nL3N3V2ludU1nRVdnVlFGcnoweEEwNHBFaFhDCkZWdjRldmJVcGVr"
				+ "SjVidXFVMWdtUXlPc0NLUWxoT0hUZFBqdmtDNXVwRHFhNTFGbGswVE1hTWtJUWpz"
				+ "N2FVS0MKbUE0Ukc0dFRUR0svRWpSMWl4OC9EMGdIWVZSbGR5MVlQck1QK291NzVi"
				+ "T1ZuSW9zK0hpZnJBdHJJdjRxRXF3TApMNEZUWkFVcGFDYTJCbWdYZnkyQ1NSUWJ4"
				+ "RDVPcjFnY1NhM3Z1cmg1c1BNQ054cWFYbUlYbVFpcFMrRHVFQnFNCk04dGxkYU43"
				+ "UllvalVFS3JHVnNOazVpOXkyLzdzam4xenl5VVBmN3ZMNEdnRFlxaEpZV1Y2MURu"
				+ "WGd4L0pkNkMKV3h2c25ERjZzY3NjUXpVVEVsK2h5dz09CjwvTW9kdWx1cz4KPEV4"
				+ "cG9uZW50PgpBUUFCCjwvRXhwb25lbnQ+CjwvUlNBS2V5VmFsdWU+CjwvS2V5VmFs"
				+ "dWU+CiAgICAgIDxYNTA5RGF0YT4KPFg1MDlDZXJ0aWZpY2F0ZT5NSUlFU1RDQ0F6"
				+ "R2dBd0lCQWdJQkFqQU5CZ2txaGtpRzl3MEJBUXNGQURCaU1Rc3dDUVlEVlFRR0V3"
				+ "SlZVekVMCk1Ba0dBMVVFQ0JNQ1EwRXhGREFTQmdOVkJBY1RDMHh2Y3lCQmJtZGxi"
				+ "R1Z6TVJNd0VRWURWUVFLRXdwSlEwRk8KVGlCVVRVTklNUnN3R1FZRFZRUURFeEpK"
				+ "UTBGT1RpQlVUVU5JSUZSRlUxUWdRMEV3SGhjTk1UTXdNakE0TURBdwpNREF3V2hj"
				+ "Tk1UZ3dNakEzTWpNMU9UVTVXakJzTVFzd0NRWURWUVFHRXdKVlV6RUxNQWtHQTFV"
				+ "RUNCTUNRMEV4CkZEQVNCZ05WQkFjVEMweHZjeUJCYm1kbGJHVnpNUmN3RlFZRFZR"
				+ "UUtFdzVXWVd4cFpHRjBiM0lnVkUxRFNERWgKTUI4R0ExVUVBeE1ZVm1Gc2FXUmhk"
				+ "Rzl5SUZSTlEwZ2dWRVZUVkNCRFJWSlVNSUlCSWpBTkJna3Foa2lHOXcwQgpBUUVG"
				+ "QUFPQ0FROEFNSUlCQ2dLQ0FRRUFvL2N3dlhoYlZZbDBSRFdXdm95ZVpwRVRWWlZW"
				+ "Y01Db3ZVVk5nL3N3CldpbnVNZ0VXZ1ZRRnJ6MHhBMDRwRWhYQ0ZWdjRldmJVcGVr"
				+ "SjVidXFVMWdtUXlPc0NLUWxoT0hUZFBqdmtDNXUKcERxYTUxRmxrMFRNYU1rSVFq"
				+ "czdhVUtDbUE0Ukc0dFRUR0svRWpSMWl4OC9EMGdIWVZSbGR5MVlQck1QK291Nwo1"
				+ "Yk9WbklvcytIaWZyQXRySXY0cUVxd0xMNEZUWkFVcGFDYTJCbWdYZnkyQ1NSUWJ4"
				+ "RDVPcjFnY1NhM3Z1cmg1CnNQTUNOeHFhWG1JWG1RaXBTK0R1RUJxTU04dGxkYU43"
				+ "UllvalVFS3JHVnNOazVpOXkyLzdzam4xenl5VVBmN3YKTDRHZ0RZcWhKWVdWNjFE"
				+ "blhneC9KZDZDV3h2c25ERjZzY3NjUXpVVEVsK2h5d0lEQVFBQm80SC9NSUg4TUF3"
				+ "RwpBMVVkRXdFQi93UUNNQUF3SFFZRFZSME9CQllFRlBaRWNJUWNEL0JqMklGei9M"
				+ "RVJ1bzJBREp2aU1JR01CZ05WCkhTTUVnWVF3Z1lHQUZPMC83a0VoM0Z1RUtTK1Ev"
				+ "a1lIYUQvVzZ3aWhvV2FrWkRCaU1Rc3dDUVlEVlFRR0V3SlYKVXpFTE1Ba0dBMVVF"
				+ "Q0JNQ1EwRXhGREFTQmdOVkJBY1RDMHh2Y3lCQmJtZGxiR1Z6TVJNd0VRWURWUVFL"
				+ "RXdwSgpRMEZPVGlCVVRVTklNUnN3R1FZRFZRUURFeEpKUTBGT1RpQlVUVU5JSUZS"
				+ "RlUxUWdRMEdDQVFFd0RnWURWUjBQCkFRSC9CQVFEQWdlQU1DNEdBMVVkSHdRbk1D"
				+ "VXdJNkFob0IrR0hXaDBkSEE2THk5amNtd3VhV05oYm00dWIzSm4KTDNSdFkyZ3VZ"
				+ "M0pzTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFCMnFTeTd1aSs0M2NlYktVS3dX"
				+ "UHJ6ejl5LwpJa3JNZUpHS2pvNDBuKzl1ZWthdzNESjVFcWlPZi9xWjRwakJEKytv"
				+ "UjZCSkNiNk5RdVFLd25vQXo1bEU0U3N1Cnk1K2k5M29UM0hmeVZjNGdOTUlvSG0x"
				+ "UFMxOWw3REJLcmJ3YnpBZWEvMGpLV1Z6cnZtVjdUQmZqeEQzQVFvMVIKYlU1ZEJy"
				+ "NklqYmRMRmxuTzV4MEcwbXJHN3g1T1VQdXVyaWh5aVVScEZEcHdIOEtBSDF3TWND"
				+ "cFhHWEZSdEdLawp3eWRneVZZQXR5N290a2wvejNiWmtDVlQzNGdQdkY3MHNSNitR"
				+ "eFV5OHUwTHpGNUEvYmVZYVpweFNZRzMxYW1MCkFkWGl0VFdGaXBhSUdlYTlsRUdG"
				+ "TTBMOStCZzdYek5uNG5WTFhva3lFQjNiZ1M0c2NHNlF6blgyM0ZHazwvWDUwOUNl"
				+ "cnRpZmljYXRlPgo8L1g1MDlEYXRhPgogICAgPC9LZXlJbmZvPgogIDwvU2lnbmF0"
				+ "dXJlPgo8L3NtZDpzaWduZWRNYXJrPgo=";
		LaunchCreate launchCreate = new LaunchCreate("sunrise", signedMark);

		return launchCreate;
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
		LaunchInfo launchInfo = new LaunchInfo("claims", applicationID, true);
		
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
		
		if (launchInfoExtension.getMarks() != null)
			processMarks(launchInfoExtension.getMarks());
	}
	
	private static void processLaunchStatus(String status) {
		System.out.println("Launch Status: " + status);
	}
	
	private static void processMarks(Collection<String> marks) {
		for (String mark : marks) {
			System.out.println("Mark: " + mark);
		}
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

