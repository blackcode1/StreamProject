package edu.thss.entity;

import edu.thss.util.IDGenerator;

/**
 * 参数处理方案
 * 
 * @author zhuangxy 2012-12-25
 */

public class TempTreClass implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public TempTreClass() {
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
	 * 处理方案名称
	 */
	private String treatName;

	/**
	 * 处理方案类名
	 */
	private String className;

	/**
	 * 格式字符串，用于向用户提示构造函数接受的参数的格式
	 */
	private String acceptedParameter;

	/**
	 * 参数处理类型主码
	 */
	private Integer tempTreatTypeMC;

	/**
	 * 处理类型
	 */
	private Integer treatType;

	public String getTreatName() {
		return treatName;
	}

	public void setTreatName(String treatName) {
		this.treatName = treatName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAcceptedParameter() {
		return acceptedParameter;
	}

	public void setAcceptedParameter(String acceptedParameter) {
		this.acceptedParameter = acceptedParameter;
	}

	public Integer getTempTreatTypeMC() {
		return tempTreatTypeMC;
	}

	public Integer getTreatType() {
		return treatType;
	}

	public void setTreatType(Integer treatType) {
		this.treatType = treatType;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setTempTreatTypeMC(Integer tempTreatTypeMC) {
		this.tempTreatTypeMC = tempTreatTypeMC;
	}

	
}
