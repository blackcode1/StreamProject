package edu.thss.entity;

import edu.thss.entity.ProTreatClass;
import edu.thss.entity.relate.Pro2TemPara;
import edu.thss.entity.relate.Pro2Temp;
import edu.thss.util.IDGenerator;

import java.util.List;


public class Protocol implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public Protocol() {
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
	 * 协议名称
	 */
	private String protocalName;

	/**
	 * 协议识别方式,举例： file: c://documents//123.txt port:1235
	 */
	private String packetSource;

	/**
	 * 协议模板识别方式. 定位到协议识别方式类
	 */

	private ProTreatClass templateRecogClass;
	
	/**
	 * 解析方式
	 */
	private Integer parseMethod;
	
	/**
	 * 模板识别算法参数
	 */
    private String tempRegParameter;
    
	/**
	 * 解析方式主码
	 */
	private Integer parseMethodMC;
	
	/**
	 * 本协议在Pro2Temp关联表中的记录
	 */
	private List<Pro2Temp> pro2TempList;
	

	private String proRegPosition;

	private String proRegLength;

	private String verifyClass;

	private String linePara;
	private String carPara;
	private String terminalPara;
	private List<Pro2TemPara> paraList;

	public String getLinePara() { return linePara; }
	public void setLinePara(String linePara) { this.linePara = linePara; }

	public String getCarPara() { return carPara; }
	public void setCarPara(String carPara) { this.carPara = carPara; }

	public String getTerminalPara() { return terminalPara; }
	public void setTerminalPara(String terminalPara) { this.terminalPara = terminalPara; }

	public List<Pro2TemPara> getParaList() {
		return paraList;
	}
	public void setParaList(List<Pro2TemPara> paraList) {
		this.paraList = paraList;
	}


	public String getProtocalName() {
		return protocalName;
	}

	public void setProtocalName(String protocalName) {
		this.protocalName = protocalName;
	}

	public String getPacketSource() {
		return packetSource;
	}

	public void setPacketSource(String packetSource) {
		this.packetSource = packetSource;
	}

	public String getOid() {
		return oid;
	}
	
	public Integer getParseMethod() {
		return parseMethod;
	}

	public void setParseMethod(Integer parseMethod) {
		this.parseMethod = parseMethod;
	}

	public Integer getParseMethodMC() {
		return parseMethodMC;
	}

	public ProTreatClass getTemplateRecogClass() {
		return templateRecogClass;
	}

	public void setTemplateRecogClass(ProTreatClass templateRecogClass) {
		this.templateRecogClass = templateRecogClass;
	}

	public List<Pro2Temp> getPro2TempList() {
		return pro2TempList;
	}

	public void setPro2TempList(List<Pro2Temp> pro2TempList) {
		this.pro2TempList = pro2TempList;
	}

	public String getTempRegParameter() {
		return tempRegParameter;
	}

	public void setTempRegParameter(String tempRegParameter) {
		this.tempRegParameter = tempRegParameter;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setParseMethodMC(Integer parseMethodMC) {
		this.parseMethodMC = parseMethodMC;
	}

	public String getProRegPosition() {
		return proRegPosition;
	}

	public void setProRegPosition(String proRegPosition) {
		this.proRegPosition = proRegPosition;
	}

	public String getProRegLength() {
		return proRegLength;
	}

	public void setProRegLength(String proRegLength) {
		this.proRegLength = proRegLength;
	}

	public String getVerifyClass() {
		return verifyClass;
	}

	public void setVerifyClass(String verifyClass) {
		this.verifyClass = verifyClass;
	}
}
