package security.model;

import java.util.Date;

public class CertificateForAdding {
	private String fullName;
	private String surname;
	private String givenName;
	private String email;
	private String speciality;
	private String uid;
	private Date validFrom;
	private Date validTo;
	private String alias;
	
	
	public CertificateForAdding(String fullName, String surname, String givenName, String email, String speciality,
			String uid, Date validFrom, Date validTo, String alias) {
		super();
		this.fullName = fullName;
		this.surname = surname;
		this.givenName = givenName;
		this.email = email;
		this.speciality = speciality;
		this.uid = uid;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.alias = alias;
	}
	public CertificateForAdding() {
		
	}
	@Override
	public String toString() {
		return "CertificateForAdding [fullName=" + fullName + ", surname=" + surname + ", givenName=" + givenName
				+ ", email=" + email + ", speciality=" + speciality + ", uid=" + uid + ", validFrom=" + validFrom
				+ ", validTo=" + validTo + ", alias=" + alias + "]";
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	
}
