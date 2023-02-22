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

import java.net.IDN;

/**
 * Wrapper class for the VGRS IDN DSK's Punycode converter.
 *
 * @author Daniel Manley
 * @version $Revision: 1.2 $ $Date: 2004/01/30 22:02:27 $
 * @see com.liberty.rtk.addon.example.IDNGUIExample
**/
public class VGRSPuny
{
    // TODO - remove this really no reason to even have this anymore, just use java.net.IDN directly.

    public static String easyEncodeDomain(String utf8Domain) {
        return IDN.toASCII(utf8Domain);
    }

    public static String easyDecodeDomain(String punyDomain) {
        return IDN.toUnicode(punyDomain);
    }
}
