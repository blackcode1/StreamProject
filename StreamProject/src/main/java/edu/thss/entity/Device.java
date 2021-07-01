package edu.thss.entity;

import edu.thss.entity.DeviceSeries;
import edu.thss.util.IDGenerator;

/**
 * 设备表
 * 
 * @author zhuangxy 2012-12-25
 */
public class Device implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public Device() {
		this.plt_id = IDGenerator.getPltID();
	}

	/**
	 * MRO平台特殊需要的属性，对用户不可见
	 */
	private String plt_id;

	/**
	 * oid
	 */
	private String oid;

	/**
	 * 设备编号
	 */
	private String deviceID;

	/**
	 * 设备系列编号
	 */
	private DeviceSeries deviceSeries;

	public String getOid() {
		return oid;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public DeviceSeries getDeviceSeries() {
		return deviceSeries;
	}

	public void setDeviceSeries(DeviceSeries deviceSeries) {
		this.deviceSeries = deviceSeries;
	}

}
