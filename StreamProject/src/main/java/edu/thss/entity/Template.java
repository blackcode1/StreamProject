package edu.thss.entity;

import edu.thss.entity.TempTreClass;
import edu.thss.entity.TemplatePara;
import edu.thss.entity.relate.Tem2TemPara;
import edu.thss.entity.relate.Temp2Treat;
import edu.thss.util.IDGenerator;

import java.util.List;

/**
 * 模板
 * 
 * @author zhuangxy 2012-12-25
 */

public class Template implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public Template() {
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
	 * 模板编号
	 */
	private String templateID;
	
	/**
	 * 模板名称
	 */
	private String templateName;

	/**
	 * 模板解析类
	 */
	private TempTreClass parseClassName;

	/**
	 * 解析参数
	 */
	private String parseParameter;

	/**
	 * 模板类型
	 */
//	@Column(name = "plt_templateType", columnDefinition = "NUMBER(1) DEFAULT 0")
//	private Integer templateType;	
//	
	private Boolean templateType;
	
	/**
	 * 报文的固定长度，若非固定取-1
	 */
	private Integer fixedLength;

	/**
	 * 模板长度对应的模板参数
	 */
	private TemplatePara lengthParameter;
	
	/**
	 * 公有标识参数
	 */
	private TemplatePara commonKey;
	
	/**
	 * 唯一标识参数
	 */
	private TemplatePara uniqueKey;

	/**
	 * 本模板在Tem2TemPara中的关联记录列表,按照tpOder升序排列
	 */
	private List<Tem2TemPara> tem2TemParaList;

	private String versionList;

	private Integer tempType;
	
	private boolean isStoreRawData;

	/**
	 * 本模板在Temp2Treat中的关联记录列表,按照treatSequence升序排列
	 */
	private List<Temp2Treat> temp2TreatList;
	
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public TempTreClass getParseClassName() {
		return parseClassName;
	}

	public void setParseClassName(TempTreClass parseClassName) {
		this.parseClassName = parseClassName;
	}

	public String getParseParameter() {
		return parseParameter;
	}

	public void setParseParameter(String parseParameter) {
		this.parseParameter = parseParameter;
	}

	public Integer getFixedLength() {
		return fixedLength;
	}

	public void setFixedLength(Integer fixedLength) {
		this.fixedLength = fixedLength;
	}

	public TemplatePara getLengthParameter() {
		return lengthParameter;
	}

	public void setLengthParameter(TemplatePara lengthParameter) {
		this.lengthParameter = lengthParameter;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setTemplateType(Boolean templateType) {
		this.templateType = templateType;
	}

	public Boolean getTemplateType() {
		return templateType;
	}

	public List<Tem2TemPara> getTem2TemParaList() {
		return tem2TemParaList;
	}

	public void setTem2TemParaList(List<Tem2TemPara> tem2TemParaList) {
		this.tem2TemParaList = tem2TemParaList;
	}

	public List<Temp2Treat> getTemp2TreatList() {
		return temp2TreatList;
	}

	public void setTemp2TreatList(List<Temp2Treat> temp2TreatList) {
		this.temp2TreatList = temp2TreatList;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public TemplatePara getCommonKey() {
		return commonKey;
	}

	public void setCommonKey(TemplatePara commonKey) {
		this.commonKey = commonKey;
	}

	public TemplatePara getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(TemplatePara uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String toString(){
		return this.oid;
	}

	public String getVersionList() {
		return versionList;
	}

	public void setVersionList(String versionList) {
		this.versionList = versionList;
	}

	public Integer getTempType() {
		return tempType;
	}

	public void setTempType(Integer tempType) {
		this.tempType = tempType;
	}

	public boolean isStoreRawData() {
		return isStoreRawData;
	}

	public void setStoreRawData(boolean isStoreRawData) {
		this.isStoreRawData = isStoreRawData;
	}
}
