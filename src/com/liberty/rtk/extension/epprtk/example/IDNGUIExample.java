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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/example/IDNGUIExample.java,v 1.2 2010/08/12 17:31:41 dongjinkim Exp $
 * $Revision: 1.2 $
 * $Date: 2010/08/12 17:31:41 $
 */

package com.liberty.rtk.extension.epprtk.example;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

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
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import com.liberty.rtk.extension.epprtk.IDN;
import com.liberty.rtk.util.VGRSPuny;
import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.RTKBase;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Example code for Liberty's IDN extension for the .info TLD.
 * Uses domain check and create to demonstrate its usage.  Also uses Java Swing
 * to facilitate the enty of utf-8 domain names.
 *
 * @author Daniel Manley
 * @version $Revision: 1.2 $ $Date: 2010/08/12 17:31:41 $
 * @see com.tucows.oxrs.epprtk.rtk.EPPClient
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPGreeting
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
**/

public class IDNGUIExample implements ActionListener
{
    private static String USAGE = "Usage: com.liberty.rtk.extension.epprtk.example.IDNGUIExample epp_host_name epp_host_port epp_client_id epp_password";

    private EPPClient epp_client;
    private String epp_host_name;
    private String epp_host_port_string;
    private String epp_client_id;
    private String epp_password;
    
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JTextField domainNameField;
    private JComboBox scriptBox;
    private ButtonGroup whatToDo;
    
    /**
     * Main of the example.
     * Performs Domain renew to demontrate
     * the usage of the RGPRenew class.
    **/
    public static void main(String args[])
    {
        new IDNGUIExample(args);
    }
    
    public IDNGUIExample(String args[])
    {

        System.out.println("Start of the IDN extension example -- GUI will appear when EPP login is complete");

        try
        {
            if (args.length != 4)
            {
                System.err.println(USAGE);
                System.exit(1);
            }

            RTKBase.setDebugLevel(RTKBase.DEBUG_LEVEL_ONE);

            epp_host_name        = args[0];
            epp_host_port_string = args[1];
            epp_client_id        = args[2];
            epp_password         = args[3];

            int epp_host_port = Integer.parseInt(epp_host_port_string);

            epp_client = new EPPClient(epp_host_name,
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

            System.out.println("Starting GUI...");
            
            mainFrame = new JFrame("Liberty IDN GUI Example");
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            addWidgets();
            mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

            mainFrame.addWindowListener( new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {

                        String client_trid = "ABC:"+epp_client_id+":123";
                        try {

                            System.out.println("Stopping GUI...");

                            System.out.println("Logging out from the EPP Server");
                            epp_client.logout(client_trid);

                            System.out.println("Disconnecting from the EPP Server");
                            epp_client.disconnect();

                            System.exit(0);

                        } catch ( epp_XMLException xcp ) {
                            System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
                        } catch ( epp_Exception xcp ) {
                            System.err.println("epp_Exception!");
                            epp_Result[] results = xcp.m_details;
                            System.err.println("\tcode: ["+results[0].m_code+"] lang: ["+results[0].m_lang+"] msg: ["+results[0].m_msg+"]");
                            if ( results[0].m_values != null && results[0].m_values.length > 0 )
                            {
                                System.err.println("\tvalue: ["+results[0].m_values[0]+"]");
                            }
                        } catch ( Exception xcp ) {
                            System.err.println("Exception! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
                            xcp.printStackTrace();
                        }
                    }
                }
            );
            mainFrame.pack();
            mainFrame.setVisible(true);



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

    private void addWidgets() {
	// Create widgets.

        JPanel line1 = new JPanel();
        line1.setLayout(new BoxLayout(line1, BoxLayout.LINE_AXIS));
        line1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        JLabel domainLabel = new JLabel("Domain Name");
        line1.add(domainLabel);
        domainLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        domainNameField = new JTextField();
        domainNameField.addActionListener(this);
        line1.add(domainNameField);
        mainPanel.add(line1);


        JPanel line1dot1 = new JPanel();
        line1dot1.setLayout(new BoxLayout(line1dot1, BoxLayout.LINE_AXIS));
        line1dot1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        JLabel scriptLabel = new JLabel("Script");
        line1dot1.add(scriptLabel);
        scriptLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        // just some sample scripts for now.
        String[] scriptStrings = { "de", "se", "fr", "ru", "ko" };
        scriptBox = new JComboBox(scriptStrings);
        line1dot1.add(scriptBox);
        mainPanel.add(line1dot1);


        whatToDo = new ButtonGroup();
        final int numButtons = 3;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        
        JPanel line2 = new JPanel();
        line2.setLayout(new BoxLayout(line2, BoxLayout.LINE_AXIS));
        line2.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        radioButtons[0] = new JRadioButton("Domain Check");
        radioButtons[0].setActionCommand("check");
        radioButtons[1] = new JRadioButton("Domain Create");
        radioButtons[1].setActionCommand("create");
        radioButtons[2] = new JRadioButton("Domain Info");
        radioButtons[2].setActionCommand("info");
        for (int i = 0; i < numButtons; i++) {
            whatToDo.add(radioButtons[i]);
            line2.add(radioButtons[i]);
        }
        radioButtons[0].setSelected(true);

        mainPanel.add(line2);

	JButton doIt = new JButton("Execute Command");

	// Listen to events from Convert button.
	doIt.addActionListener(this);
        
        mainPanel.add(doIt);
	
    }

    /**
     * Implementation of ActionListener interface for the click of the "Execute Command" button.
     */
    public void actionPerformed(ActionEvent event) {

        String domainName = domainNameField.getText();
        String domainAction = whatToDo.getSelection().getActionCommand();
        String selectedScript = (String)scriptBox.getSelectedItem();

        String client_trid = "ABC:"+epp_client_id+":123";
	
        System.out.println("The domain name is: "+domainName);
        System.out.println("The domain script is: "+selectedScript);
        System.out.println("The action command is: "+domainAction);
        
        if ( domainName == null ||
             domainName.length() == 0 ) {
            return;
        }
        
        try {
        
            if ( domainAction.equals("check") ) {

                // ***************************
                // Domain Check
                // ***************************
                System.out.println("Creating the Domain Check command");
                epp_DomainCheckReq domain_check_request = new epp_DomainCheckReq();
                
                epp_Command command_data = new epp_Command();
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
                domain_list.add(VGRSPuny.easyEncodeDomain(domainName));

                domain_check_request.setNames(EPPXMLBase.convertListToStringArray(domain_list));
                
                // ********************************
                //
                // Domain IDN extension
                //
                // ********************************
                IDN idn_extension = new IDN();
                idn_extension.setCommand(domainAction);
                idn_extension.setScript(selectedScript);

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
                // method getCheckResultFor() can be used.  This method returns
                // a Boolean object or null if the value was not found in the
                // epp_CheckResult array.
                epp_CheckResult[] check_results = domain_check_response.getResults();
                System.out.println("DomainCheck results: domain ["+domainName+"] exists? ["+EPPXMLBase.getAvailResultFor(check_results, VGRSPuny.easyEncodeDomain(domainName))+"]");

            } else if ( domainAction.equals("create") ) {


                // ***************************
                // Domain Create
                // ***************************
                System.out.println("Creating the Domain Create command");
                epp_DomainCreateReq domain_create_request = new epp_DomainCreateReq();
                
                epp_Command command_data = new epp_Command();
                // The client trid is optional.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests
                command_data.setClientTrid(client_trid);
                domain_create_request.setCmd(command_data);

                domain_create_request.setName(VGRSPuny.easyEncodeDomain(domainName));
                // The domain's period is optional.  It is specified with
                // an object that contains the unit of measurement (years or 
                // months) and the actual period value.
                domain_create_request.setPeriod(new epp_DomainPeriod());
                domain_create_request.getPeriod().setUnit(epp_DomainPeriodUnitType.YEAR);
                domain_create_request.getPeriod().setValue((short) 2);
                
                // At domain creation, you can specify another domain's
                // nameserver's in the request.
                List name_server_list = new ArrayList();
                name_server_list.add("ns1.valid.info");
                name_server_list.add("ns2.valid.info");
                domain_create_request.setNameServers(EPPXMLBase.convertListToStringArray(name_server_list));
                                
                epp_AuthInfo domain_auth_info = new epp_AuthInfo();
                domain_auth_info.setValue("123123");

                // For the current spec of EPP, PW is the only allowed type
                // of auth info.  So, the type can be left null and the RTK will
                // fill in the value for you.
                domain_auth_info.setType(epp_AuthInfoType.PW);
                domain_create_request.setAuthInfo(domain_auth_info);

                // ********************************
                //
                // Domain IDN extension
                //
                // ********************************
                IDN idn_extension = new IDN();
                idn_extension.setCommand(domainAction);
                idn_extension.setScript(selectedScript);

                epp_Extension[] extensions = {idn_extension};
                domain_create_request.getCmd().setExtensions(extensions);
                

                EPPDomainCreate domain_create = new EPPDomainCreate();
                domain_create.setRequestData(domain_create_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_create = (EPPDomainCreate) epp_client.processAction(domain_create);
                // or, alternatively, this method can be used...
                //domain_create.fromXML(epp_client.processXML(domain_create.toXML()));

                epp_DomainCreateRsp domain_create_response = domain_create.getResponseData();
                epp_Response response = domain_create_response.getRsp();

                epp_Result[] results = response.getResults();
                System.out.println("DomainCreate results: ["+results[0].getCode()+"] ["+results[0].getMsg()+"]");
                // The domain's name and expiration date are returned on a
                // successful domain creation.
                System.out.println("DomainCreate results: domain name ["+domain_create_response.getName()+"] exp date ["+domain_create_response.getExpirationDate()+"]");

                // no point in checking for extension because it will only appear when
                // there is an error response, in which case the RTK will throw epp_Exception
                // and the extension will be unretrievable -- not to worry though... the
                // exception captures the generic error message which gives a big 
                // hint of the problem.
            } else if ( domainAction.equals("info") ) {

                // ***************************
                // Domain Info
                // ***************************
                System.out.println("Creating the Domain Info command");
                epp_DomainInfoReq domain_info_request = new epp_DomainInfoReq();

                epp_Command command_data = new epp_Command();
                // The client trid is optional.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests
                command_data.setClientTrid(client_trid);
                domain_info_request.setCmd(command_data);

                // The only domain-specific parameter is the domain name itself.
                domain_info_request.setName(VGRSPuny.easyEncodeDomain(domainName));
                
                EPPDomainInfo domain_info = new EPPDomainInfo();
                domain_info.setRequestData(domain_info_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_info = (EPPDomainInfo) epp_client.processAction(domain_info);
                // or, alternatively, this method can be used...
                //domain_info.fromXML(epp_client.processXML(domain_info.toXML()));

                epp_DomainInfoRsp domain_info_response = domain_info.getResponseData();
                epp_Response response = domain_info_response.getRsp();
                
                String[] extensionStrings = response.getExtensionStrings();
                if ( extensionStrings[0] != null ) {
                    IDN idn_extension = new IDN();
                    idn_extension.fromXML(extensionStrings[0]);
                    System.out.println("Command ["+idn_extension.getCommand()+"]");
                    System.out.println("Script  ["+idn_extension.getScript()+"]");
                } else {
                    System.out.println("Domain Info response contained no extension!!!");
                }
            }
            else if ( domainAction.equals("update") ) {
                // ***************************
                // Domain Update
                // ***************************
                System.out.println("Creating the Domain Update command");
                epp_DomainUpdateReq domain_update_request = new epp_DomainUpdateReq();

                epp_Command command_data = new epp_Command();
                // The client trid is optional.  Its main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests
                command_data.setClientTrid(client_trid);
                domain_update_request.setCmd(command_data);

                // The only domain-specific parameter is the domain name itself.
                domain_update_request.setName(VGRSPuny.easyEncodeDomain(domainName));
                
                EPPDomainUpdate domain_update = new EPPDomainUpdate();
                
                domain_update.setRequestData(domain_update_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_update = (EPPDomainUpdate) epp_client.processAction(domain_update);
                // or, alternatively, this method can be used...
                //domain_info.fromXML(epp_client.processXML(domain_info.toXML()));

                epp_DomainUpdateRsp domain_update_response = domain_update.getResponseData();
                epp_Response response = domain_update_response.getRsp();
                
                String[] extensionStrings = response.getExtensionStrings();
                if ( extensionStrings[0] != null ) {
                    IDN idn_extension = new IDN();
                    idn_extension.fromXML(extensionStrings[0]);
                    System.out.println("Command ["+idn_extension.getCommand()+"]");
                    System.out.println("Script  ["+idn_extension.getScript()+"]");
                } else {
                    System.out.println("Domain Info response contained no extension!!!");
                }
            }

        } catch ( epp_XMLException xcp ) {
            // Either the request was missing some required data in
            // validation before sending to the server, or the server's
            // response was either unparsable or missing some required data.
            System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
        } catch ( epp_Exception xcp ) {
            // The EPP Server has responded with an error code with
            // some optional messages to describe the error.
            System.err.println("epp_Exception!");
            epp_Result[] results = xcp.m_details;
            System.err.println("\tcode: ["+results[0].m_code+"] lang: ["+results[0].m_lang+"] msg: ["+results[0].m_msg+"]");
            if ( results[0].m_values != null && results[0].m_values.length > 0 ) {
                System.err.println("\tvalue: ["+results[0].m_values[0]+"]");
            }
        } catch ( Exception xcp ) {
            // Other unexpected exceptions
            System.err.println("Domain Check failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
            xcp.printStackTrace();
        }

        domainNameField.grabFocus();
    }

}
