package com.ehrc.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.tomcat.util.json.ParseException;

import com.ehrc.user.co.ConsentCO;

//import com.google.protobuf.Timestamp;

@Entity
@Table(name = "consentdb")
public class ConsentDb {
//	Timestamp	
//	hospital id
//	start end date
//	consent id
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int tableid;
	
	@Column(name = "start", length = 50)
	String start;
	
	@Column(name = "type", length = 50)
	String type;

	@Column(name = "hospitalname", length = 50)
	String hospitalname;
	
	@Column(name = "userid", length = 50)
	int userid;

	@Column(name = "end", length = 50)
	String end;
	
	@Column(name = "consentRequestid", length = 50)
	String consentRequestid;

	@Column(name = "consentid", length = 50)
	String consentid;

	@Column(name = "timestamp", length = 50)
	String timestamp;
	
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

	public void setTimestamp(String date) {
		this.timestamp = date;
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHospitalname() {
		return hospitalname;
	}

	public void setHospitalname(String hospitalname) {
		this.hospitalname = hospitalname;
	}
	
	public static ConsentDb mapConsentCOToDB(ConsentCO objConsentCO) throws ParseException
	{
		Date someDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String timestamp = df.format(someDate);
		ConsentDb objConsentDb = new ConsentDb();
		objConsentDb.setUserID(objConsentCO.getUserID());
		objConsentDb.setConsentid(objConsentCO.getConsentid());
		objConsentDb.setType(objConsentCO.getType());
		objConsentDb.setConsentRequestid(objConsentCO.getConsentRequestid());
		objConsentDb.setHospitalname(objConsentCO.getHospitalname());
		objConsentDb.setEnd(objConsentCO.getEnd());
		objConsentDb.setStart(objConsentCO.getStart());
		objConsentDb.setTimestamp(timestamp);
		objConsentDb.setType(objConsentCO.getType());
		
//		if(objConsentCO.getCreated_at() != null) {
//			objUserDb.setCreated_at(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(objConsentCO.getCreated_at()));
//		}
//		
//		if(objConsentCO.getUserId() != null )
//		{
//			objConsentCO.setUserId(Integer.parseInt(objUserCO.getUserId()));
//		}
		return objConsentDb;
	}
	
	public static ConsentCO mapConsentDBToCO(ConsentDb objConsentDb) throws ParseException
	{
		ConsentCO objConsentCO = new ConsentCO();
		objConsentCO.setUserID(objConsentDb.getUserID());
		Date someDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String timestamp = df.format(someDate);
		objConsentCO.setUserID(objConsentDb.getUserID());
		objConsentCO.setTimestamp(objConsentDb.getTimestamp());
		objConsentCO.setConsentid(objConsentDb.getConsentid());
		objConsentCO.setEnd(objConsentDb.getEnd());
		objConsentCO.setStart(objConsentDb.getStart());
		objConsentCO.setTimestamp(timestamp);
		objConsentCO.setType(objConsentDb.getType());

		objConsentCO.setConsentRequestid(objConsentDb.getConsentRequestid());
		objConsentCO.setHospitalname(objConsentDb.getHospitalname());
		//objUserCO.setMobileNo(objUserDb.getMobile_no());


//		
//		if(objUserDb.getCreated_at() != null) {
//			//objUserCO.setCreated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getCreated_at()));
//		}
//		objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		
		return objConsentCO;
	}

}