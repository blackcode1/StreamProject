package edu.thss.entity.relate;

import edu.thss.entity.Protocol;
import edu.thss.entity.Template;
import edu.thss.entity.relate.Pro2TempPK;


public class Pro2Temp implements java.io.Serializable {

	/**
	 * 内嵌复合主键
	 */
	private Pro2TempPK pk;

	public Pro2Temp(){
		pk=new Pro2TempPK();
	}

	public Pro2TempPK getPk() {
		return pk;
	}

	/**
	 * 设备状态
	 */
	private Integer deviceStatus;

	/**
	 * 设备状态主码
	 */
	private Integer deviceStatusMC;

	public Protocol getProtocol() {
		return this.pk.protocol;
	}

	public Template getTemplate() {
		return this.pk.template;
	}

	public void setProtocol(Protocol p){
		pk.protocol=p;
	}

	public void setTemplate(Template t){
		pk.template=t;
	}

	public Integer getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(Integer deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public Integer getDeviceStatusMC() {
		return deviceStatusMC;
	}

	public void setDeviceStatusMC(Integer deviceStatusMC) {
		this.deviceStatusMC = deviceStatusMC;
	}

	public String toString(){
		if(pk.template==null)return "";
		return pk.template.getOid();
	}
}


