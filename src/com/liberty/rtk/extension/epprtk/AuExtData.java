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
 * $Header: /cvsroot/epp-rtk/liberty-rtk-addon/java/src/com/liberty/rtk/extension/epprtk/FeeData.java,v 1.2 2007/06/14 14:22:56 asimbirt Exp $
 * $Revision: 1.2 $
 * $Date: 2007/06/14 14:22:56 $
 */

package com.liberty.rtk.extension.epprtk;

public class AuExtData
{
	private String registrantName;
    private String registrantId;
    private String registrantIdType;
    private String eligibitlityType;
    private String eligibilityName;
    private String eligibilityId;
    private String eligibilityIdType;
    private int policyReason;
    private String explanation;
    private String expiryDate;
    private String period;
    
    public String getRegistrantName() {
		return registrantName;
	}
	public void setRegistrantName(String registrantName) {
		this.registrantName = registrantName;
	}
	public String getRegistrantId() {
		return registrantId;
	}
	public void setRegistrantId(String registrantId) {
		this.registrantId = registrantId;
	}
	public String getEligibitlityType() {
		return eligibitlityType;
	}
	public void setEligibitlityType(String eligibitlityType) {
		this.eligibitlityType = eligibitlityType;
	}
	public String getEligibilityName() {
		return eligibilityName;
	}
	public void setEligibilityName(String eligibilityName) {
		this.eligibilityName = eligibilityName;
	}
	public String getEligibilityId() {
		return eligibilityId;
	}
	public void setEligibilityId(String eligibilityId) {
		this.eligibilityId = eligibilityId;
	}
	public int getPolicyReason() {
		return policyReason;
	}
	public void setPolicyReason(int policyReason) {
		this.policyReason = policyReason;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public String getRegistrantIdType() {
		return registrantIdType;
	}
	public void setRegistrantIdType(String registrantIdType) {
		this.registrantIdType = registrantIdType;
	}
	public String getEligibilityIdType() {
		return eligibilityIdType;
	}
	public void setEligibilityIdType(String eligibilityIdType) {
		this.eligibilityIdType = eligibilityIdType;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

}
