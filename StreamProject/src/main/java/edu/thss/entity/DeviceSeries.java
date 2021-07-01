package edu.thss.entity;

import edu.thss.util.IDGenerator;

/**
 * 设备系列
 * 
 * @author zhuangxy 2012-12-25
 */
public class DeviceSeries implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public DeviceSeries() {
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
	 * 设备系列名称
	 */
	private String seriesName;

	/**
	 * 设备系列命名规则
	 */
	private String seriesRule;


	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getSeriesRule() {
		return seriesRule;
	}

	public void setSeriesRule(String seriesRule) {
		this.seriesRule = seriesRule;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

}
