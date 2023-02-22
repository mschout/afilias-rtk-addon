package com.liberty.rtk.extension.epprtk;

import junit.framework.TestCase;

import org.openrtk.idl.epprtk.epp_XMLException;

import com.liberty.rtk.extension.epprtk.IDValidation;

public class TestIDValidation extends TestCase
{
    public void testCreate() 
	{
		String xml = "<validation:creData xmlns:validation='urn:afilias:params:xml:ns:validation-1.0'>" +
					 "<validation:claimID>94ec05ea-4cfc-4123-a2d3-62130b685acd</validation:claimID>" +
					 "</validation:creData>";
		
		IDValidation idValidation = new IDValidation();

        try 
        {
        	IDValidation.setDebugLevel(2);
			idValidation.fromXML(xml);
		} 
        catch (epp_XMLException e) 
		{
			e.printStackTrace();
		}
    }

    public void testRequestUpdate() 
	{
    	/*
		String xml = "<validation:updData xmlns:validation='urn:afilias:params:xml:ns:validation-1.0'>" +
					 "<validation:claimID>94ec05ea-4cfc-4123-a2d3-62130b685acd</validation:claimID>" +
					 "</validation:updData>";
		String xml = "<validation:update xmlns:validation='urn:afilias:params:xml:ns:validation-1.0'>" +
				     "<validation:chg><validation:ownership/></validation:chg>" +
				     "</validation:update>";
		*/
		
		IDValidation idValidation = new IDValidation();

        try 
        {
        	IDValidation.setDebugLevel(2);
        	idValidation.setCommand("update");
			String validation_xml = idValidation.toXML();
			
			System.out.println("validation_xml = " + validation_xml);
		} 
        catch (epp_XMLException e) 
		{
			e.printStackTrace();
		}
    }


    public void testUpdate() 
	{
		String xml = "<validation:updData xmlns:validation='urn:afilias:params:xml:ns:validation-1.0'>" +
					 "<validation:claimID>94ec05ea-4cfc-4123-a2d3-62130b685acd</validation:claimID>" +
					 "</validation:updData>";
		
		IDValidation idValidation = new IDValidation();

        try 
        {
        	IDValidation.setDebugLevel(2);
			idValidation.fromXML(xml);
		} 
        catch (epp_XMLException e) 
		{
			e.printStackTrace();
		}
    }


}
