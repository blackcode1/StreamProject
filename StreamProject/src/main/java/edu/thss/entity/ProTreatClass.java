package edu.thss.entity;

import edu.thss.util.IDGenerator;

/**
 * 协议处理方案 记录与协议相关的处理方案，包括校验、解密、解压缩、协议模板识别等处理方案
 * 
 * @author zhuangxy 2012-12-25
 */

public class ProTreatClass implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public ProTreatClass() {
		this.plt_id = IDGenerator.getPltID();
	}

	public void setProTreatTypeMC(Integer proTreatTypeMC) {
		this.proTreatTypeMC = proTreatTypeMC;
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
	 * 协议处理方案通用名称，显示给用户看
	 */
	private String treatName;

	/**
	 * 协议处理类类名
	 */
	private String className;

	/**
	 * 格式字符串，用于向用户提示构造函数接受的参数的格式
	 */
	private String acceptedParameter;

	/**
	 * 处理类型
	 */
	private Integer treatType;

	/**
	 * 协议处理类型主码
	 */
	private Integer proTreatTypeMC;

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

	public Integer getTreatType() {
		return treatType;
	}

	public void setTreatType(Integer treatType) {
		this.treatType = treatType;
	}

	public Integer getProTreatTypeMC() {
		return proTreatTypeMC;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String value) {
		// TODO Auto-generated method stub
		this.oid=value;
		
	}



}
