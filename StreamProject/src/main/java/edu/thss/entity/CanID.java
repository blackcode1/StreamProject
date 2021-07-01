package edu.thss.entity;

public class CanID implements java.io.Serializable {
	private String canID, tempOid, railLineOid;
	private int carriage;
	public CanID() {}
	public CanID(String canID, String tempOid, String railLineOid, int carriage)
	{
		this.canID = canID;
		this.tempOid = tempOid;
		this.railLineOid = railLineOid;
		this.carriage =  carriage;
	}
	public String getCanID() {
		return this.canID;
	}
	public void setCanID(String canID) {
		this.canID = canID;
	}
	public String getTempOid() {
		return this.tempOid;
	}
	public void setTempOid(String tempOid) {
		this.tempOid = tempOid;
	}
	public String getRailLineOid() {
		return this.railLineOid;
	}
	public void setRailLineOid(String railLineOid) {
		this.railLineOid = railLineOid;
	}
	public int getCarriage() {
		return this.carriage;
	}
	public void setCarriage(int carriage) {
		this.carriage = carriage;
	}
}
