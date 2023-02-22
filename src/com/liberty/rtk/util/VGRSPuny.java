/*
**
** EPP RTK Java
** Copyright (C) 2001, Afilias Limited
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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/util/VGRSPuny.java,v 1.2 2004/01/30 22:02:27 tubadanm Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/30 22:02:27 $
 */

package com.liberty.rtk.util;

// This class uses the Verisign IDN SDK for punycode domains.
// It can be obtained from http://www.verisigninc.com/en_US/products-and-services/domain-name-services/registry-products/idn-sdk/index.xhtml?loc=en_US
// The Jar is included in the afilias-rtk-addon distribution.
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.common.Unicode;

/**
 * Wrapper class for the VGRS IDN DSK's Punycode converter.
 *
 * @author Daniel Manley
 * @version $Revision: 1.2 $ $Date: 2004/01/30 22:02:27 $
 * @see com.liberty.rtk.addon.example.IDNGUIExample
**/
public class VGRSPuny
{

    public static String easyEncodeDomain(String utf8Domain) throws com.vgrs.xcode.util.XcodeException {

        System.out.println("The UTF8 domain is: "+utf8Domain);
        String punyDomain;
        Idna idna = new Idna(new Punycode(), true, true);
        char[] theChars = utf8Domain.toCharArray();
        int[] theInts = Unicode.encode(theChars);
        
        punyDomain = new String(idna.domainToAscii(theInts));
        System.out.println("The puny domain is: "+punyDomain);
        return punyDomain;
    }

    public static String easyDecodeDomain(String punyDomain) throws com.vgrs.xcode.util.XcodeException {

        System.out.println("The punycode domain is: "+punyDomain);
        String utf8Domain;
        Idna idna = new Idna(new Punycode(), true, true);
        char[] theChars = punyDomain.toCharArray();
        int[] theInts = idna.domainToUnicode(theChars);
        
        utf8Domain = new String(Unicode.decode(theInts));
        System.out.println("The utf8 domain is: "+utf8Domain);
        return utf8Domain;
    }

}
