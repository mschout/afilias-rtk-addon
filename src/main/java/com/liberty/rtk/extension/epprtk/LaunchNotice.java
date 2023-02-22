package com.liberty.rtk.extension.epprtk;

import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LaunchNotice 
{
    private String noticeID;
    private String notAfter;
    private String acceptedDate;

    public LaunchNotice () {}

    public LaunchNotice (String noticeID, String notAfter, String acceptedDate) {
	    this.noticeID = noticeID;
        this.notAfter = notAfter;
        this.acceptedDate = acceptedDate;
    }

    public void setNoticeID(String value) { this.noticeID = value; }
    public String getNoticeID() { return noticeID; }

    public void setNotAfter(String value) { this.notAfter = value; }
    public String getNotAfter() { return notAfter; }

    public void setAcceptedDate(String value) { this.acceptedDate = value; }
    public String getAcceptedDate() { return acceptedDate; }

    public Element getElement(Document doc) throws epp_XMLException
    {
        Element e = doc.createElement("launch:notice");

        if (noticeID == null || noticeID.isEmpty()) throw new epp_XMLException("launch:noticeID invalid ("+noticeID+")");
        if (notAfter == null || notAfter.isEmpty()) throw new epp_XMLException("launch:notAfter invalid ("+notAfter+")");
        if (acceptedDate == null || acceptedDate.isEmpty()) throw new epp_XMLException("launch:acceptedDate invalid ("+acceptedDate+")");

        ExtUtils.addXMLElement(doc, e, "launch:noticeID", noticeID);
        ExtUtils.addXMLElement(doc, e, "launch:notAfter", notAfter);
        ExtUtils.addXMLElement(doc, e, "launch:acceptedDate", acceptedDate);

        return e;
    } 

    public String toString()
    {
        return "[noticeID:" + noticeID + "|notAfter:" + notAfter + "|acceptedDate:" + acceptedDate + "]";
    }
}
