/*
**
** EPP RTK Java
** Copyright (C) 2001-2003, Liberty Registry Management Services, Inc.
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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/EnsInfo.java,v 1.1 2006/08/23 20:26:47 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2006/08/23 20:26:47 $
 */

package com.liberty.rtk.extension.epprtk;

import java.util.Collection;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EnsInfo
{
    private Collection ens_class_ = null;
    private String ens_org_ = null;
    private String registrant_group_ = null;
    private String request_type_ = null;
    private String registration_type_ = null;
    private String credentials_type_ = null;
    private String credentials_value_ = null;
    private String code_value_ = null;
    private String unique_identifier_ = null;
    private String last_checked_date_ = null;

    public EnsInfo() { }

    public EnsInfo(Collection ens_class, 
            String ens_org, 
            String registrant_group, 
            String request_type,
            String registration_type, 
            String credentials_type,
            String credentials_value,
            String code_value,
            String unique_identifier,
            String last_checked_date
            )
    {
        ens_class_ = ens_class;
        registrant_group_ = registrant_group;
        request_type_ = request_type;
        registration_type_ = registration_type;
        credentials_type_ = credentials_type;
        credentials_value_ = credentials_value;
        code_value_ = code_value;
        unique_identifier_ = unique_identifier;
        last_checked_date_ = last_checked_date;
    }

    public EnsInfo(Node a_node)
    {
        if (!a_node.hasChildNodes())
        {
            return;
        }

        Collection ensClasses = null;

        NodeList childs = a_node.getChildNodes();

        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if ( child.getNodeName().equals("aero:ensClass") )
            {
                if (ensClasses == null)
                {
                    ensClasses = new ArrayList();
                }

                ensClasses.add( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:registrantGroup") )
            {
                setRegistrantGroup( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:ensO") )
            {
                setEnsO( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:requestType") )
            {
                setRequestType( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:registrationType") )
            {
                setRegistrationType( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:credentialsType") )
            {
                setCredentialsType( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:credentialsValue") )
            {
                setCredentialsValue( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:codeValue") )
            {
                setCodeValue( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:uniqueIdentifier") )
            {
                setUniqueIdentifier( child.getFirstChild().getNodeValue() );
            }
            if ( child.getNodeName().equals("aero:lastCheckedDate") )
            {
                setLastCheckedDate( child.getFirstChild().getNodeValue() );
            }
        }

        if (ensClasses != null)
        {
            setEnsClass(ensClasses);
        }
    }

    public void setEnsClass(Collection ensClass) { ens_class_ = ensClass; }
    public Collection getEnsClass() { return ens_class_; } 

    public void setRegistrantGroup(String registrantGroup) { registrant_group_ = registrantGroup; }
    public String getRegistrantGroup() { return registrant_group_; }

    public void setEnsO(String ensO) { ens_org_ = ensO; }
    public String getEnsO() { return ens_org_; }

    public void setRequestType(String requestType) { request_type_ = requestType; }
    public String getRequestType() { return request_type_; }

    public void setRegistrationType(String registration_type) { registration_type_ = registration_type; }
    public String getRegistrationType() { return registration_type_; }

    public void setCredentialsType(String credentialsType) { credentials_type_ = credentialsType; }
    public String getCredentialsType() { return credentials_type_; }

    public void setCredentialsValue(String cdentialsValue) { credentials_value_ = cdentialsValue; }
    public String getCredentialsValue() { return credentials_value_; }

    public void setCodeValue(String codeValue) { code_value_ = codeValue; }
    public String getCodeValue() { return code_value_; }

    public void setUniqueIdentifier(String uniqueIdentifier) { unique_identifier_= uniqueIdentifier; }
    public String getUniqueIdentifier() { return unique_identifier_; }

    public void setLastCheckedDate(String last_checked_date) { last_checked_date_ = last_checked_date; }
    public String getLastCheckedDate() { return last_checked_date_; }

    public String toString()
    {
        return "[ensClass: " + ens_class_ + "|ensO: " + ens_org_ + "|registrantGroup: " + registrant_group_ + "|requestType: " + request_type_ + "|registrationType: " + registration_type_ + "|credentialsType: " + credentials_type_ + "|cdentialsValue:" + credentials_value_ + "|codeValue: " + code_value_ + "|uniqueIdentifier: " + unique_identifier_ + "|lastCheckedDate: " + last_checked_date_ + "]";
    }
}
