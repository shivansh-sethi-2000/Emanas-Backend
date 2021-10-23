package com.ehrc.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hospitaldb")
public class HospitalDb {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int tableid;

	@Column(name = "hospitalname", length = 50)
	String hospitalname;
	
	@Column(name = "hospitalid", length = 50)
	String hospitalid;

	public void setTableid(int tableid) {
		this.tableid = tableid;
	}

	public String getHospitalid() {
		return hospitalid;
	}

	public void setHospitalid(String hospitalid) {
		this.hospitalid = hospitalid;
	}

	public String getHospitalname() {
		return hospitalname;
	}

	public void setHospitalname(String hospitalname) {
		this.hospitalname = hospitalname;
	}
}
