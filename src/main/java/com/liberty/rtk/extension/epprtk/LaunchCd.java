package com.liberty.rtk.extension.epprtk;

public class LaunchCd 
{
    private String name;
    private String exists;
    private String claimsKey;

    public LaunchCd () {}

    public LaunchCd (String name, String exists) {
	    this.name = name;
        this.exists = exists;
    }

    public void setName(String value) { name = value; }
    public String getName() { return name; }

    public void setClaimsKey(String value) { claimsKey = value; }
    public String getClaimsKey() { return claimsKey; }

    public void setExists(String value) { exists = value; }
    public String getExists() { return exists; }
    
    public String toString()
    {
        return "[name:" + name + "|exists:" + exists + "|claimKey:" + claimsKey + "]";
    }
}
