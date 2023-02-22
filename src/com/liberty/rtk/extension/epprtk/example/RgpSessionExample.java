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

import com.liberty.rtk.extension.epprtk.Rgp;
import com.liberty.rtk.extension.epprtk.RgpReportData;

import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;

import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;

/**
 * Example code for Liberty's Domain Rgp add-on to the RTK.
 * Uses domain update, info to demonstrate its usage.
 *
 * @author Anna Simbirtsev 
 * @version $Revision: 1.1 $ $Date: 2007/11/19 20:25:47 $
 * @see com.tucows.oxrs.epprtk.rtk.EPPClient
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPGreeting
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
**/
public class RgpSessionExample extends SessionExample
{
    private String domain_name = null;
    private String delete_time = null;
    private String restore_time = null;

    /**
     * Main of the example.
     * Performs Domain info and update and demontrate
     * the usage of the Rgp class in those commands.
     **/

    public static void main(String args[])
    {
        SessionExample example = new RgpSessionExample(args);
        example.session();
    }

    public RgpSessionExample(String args[])
    {
        super(args);
        domain_name = nextArgument();
        delete_time = nextArgument();
        assertNotNull(domain_name);
        assertNotNull(delete_time);
    }

    protected String getUsage()
    {
        return super.getUsage()
            + " domain_name delete_time";
    }

    protected void process() throws epp_Exception, IOException, epp_XMLException
    {
        domainUpdate();
        domainInfo();
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

	System.out.println("Rgp extension:");
	epp_Response response = domain_info_response.getRsp();
	String[] extensionStrings = response.getExtensionStrings();
	if ( extensionStrings != null ) {
		Rgp rgp = new Rgp();
		rgp.fromXML(extensionStrings[0]);
		System.out.println("Rgp Status ["+ rgp.getRgpStatus().toString() +"]");
	} else {
		System.out.println("Domain Info response contained no extension!!!");
	}
     }

	private void domainUpdate() throws epp_XMLException, epp_Exception
        {
        System.out.println("Creating the Domain Update command with request option");

        epp_DomainUpdateRsp domain_update_response = null;
        epp_Response response = null;
        epp_DomainUpdateReq domain_update_request = new epp_DomainUpdateReq();

        domain_update_request.setCmd( createEPPCommand() );
        domain_update_request.setName( domain_name );

        epp_DomainUpdateChange change = new epp_DomainUpdateChange();
        domain_update_request.setChange(change);

        Rgp rgp = new Rgp();
        rgp.setCommand("update");
        rgp.setRestoreOp("request");

        domain_update_request.getCmd().setExtension(rgp);
       
        EPPDomainUpdate domain_update = new EPPDomainUpdate();
        domain_update.setRequestData(domain_update_request);

        domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);

        domain_update_response = domain_update.getResponseData();
        response = domain_update_response.m_rsp;
        epp_Result[] results = response.m_results;
        System.out.println("DomainUpdate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");

        System.out.println("Rgp extension:");
        response = domain_update_response.getRsp();
        String[] extensionStrings = response.getExtensionStrings();
        if ( extensionStrings != null ) {
                Rgp rgp1 = new Rgp();
                rgp1.fromXML(extensionStrings[0]);
                System.out.println("Rgp Status ["+ rgp1.getRgpStatus().toString() +"]");
        } else {
                System.out.println("Domain Update response contained no extension!!!");
        }

        try {
           System.out.print("Please enter restore date and time (ex.2003-07-10T22:00:00.0Z):");
           BufferedReader buffed_reader = new BufferedReader(new InputStreamReader(System.in));
           while ( restore_time == null || restore_time.length() == 0 )
           {
                restore_time = buffed_reader.readLine();
           }
        } catch (IOException e) { System.err.println("IOException! ["+e.getMessage()+"]"); }

        RgpReportData data = new RgpReportData();
        data.setPreData("Pre-delete registration data goes here.Both XML and free text are allowed.");
        data.setPostData("Post-restore registration data goes here.Both XML and free text are allowed.");
        data.setDelTime(delete_time);
        data.setResTime(restore_time);
        data.setResReason("Registrant error.");
        data.setStatement1("This registrar has not restored the Registered Name in order to assume the rights to use or sell the Registered Name for itself or for any third party.");
        data.setStatement2("The information in this report is true to best of this registrar's knowledge, and this registrar acknowledges that intentionally supplying false information in this report shall constitute an incurable material breach of the Registry-Registrar Agreement.");
        data.setOther("Supporting information goes here.");

        Rgp rgp2 = new Rgp();
        rgp2.setCommand("update");
        rgp2.setRestoreOp("report");
        rgp2.setRgpReportData(data);

        epp_DomainUpdateReq domain_update_request1 = new epp_DomainUpdateReq();

        domain_update_request1.setCmd( createEPPCommand() );
        domain_update_request1.setName( domain_name );

        epp_DomainUpdateChange change1 = new epp_DomainUpdateChange();
        domain_update_request1.setChange(change1);

        domain_update_request1.getCmd().setExtension(rgp2);

        EPPDomainUpdate domain_update1 = new EPPDomainUpdate();
        domain_update1.setRequestData(domain_update_request1);

        domain_update1 = (EPPDomainUpdate) epp_client.processAction(domain_update1);

        domain_update_response = domain_update1.getResponseData();
        response = domain_update_response.m_rsp;
        epp_Result[] results1 = response.m_results;
        System.out.println("DomainUpdate results: ["+results1[0].m_code+"] ["+results1[0].m_msg+"]");
    }
}
