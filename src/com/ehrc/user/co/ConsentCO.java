package com.ehrc.user.co;

public class ConsentCO {

	int tableid;
	String start;
	int userid;
	String end;
	String consentRequestid;
	String consentid;
	String timestamp;
	String type;
	String hospitalname;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTableid() {
		return tableid;
	}

	public void setTableid(int tableid) {
		this.tableid = tableid;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getConsentid() {
		return consentid;
	}

	public void setConsentid(String consentid) {
		this.consentid = consentid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getConsentRequestid() {
		return consentRequestid;
	}

	public void setConsentRequestid(String consentRequestid) {
		this.consentRequestid = consentRequestid;
	}
	public int getUserID() {
		return userid;
	}

	public void setUserID(int userid) {
		this.userid = userid;
	}
	
	public String getHospitalname() {
		return hospitalname;
	}

	public void setHospitalname(String hospitalname) {
		this.hospitalname = hospitalname;
	}
	
}
