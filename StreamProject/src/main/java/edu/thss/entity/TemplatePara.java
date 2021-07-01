package edu.thss.entity;

import edu.thss.entity.relate.TPara2Treat;
import edu.thss.util.IDGenerator;

import java.util.List;

/**
 * 模板参数
 * 
 * @author zhuangxy 2012-12-25
 */
public class TemplatePara implements java.io.Serializable {
	
	/**
	 * 默认构造函数，自动生成plt_id的时间。若重写构造函数需包含以下代码。
	 */
	public TemplatePara() {
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
	 * 参数编号
	 */
	private String parameterID;

	/**
	 * 参数名字
	 */
	private String parameterName;

	/**
	 * 参数类型： 1=基础信息参数 2=工况参数 3=子模板参数4保留参数
	 */
	private Integer parameterType;
	
	/**
	 * 参数类型主码
	 */
	private Integer parameterTypeMC;

	/**
	 * 长度，单位为bit
	 */
	private Integer length;
	
	/**
	 * 参数单位
	 */
	private Integer parameterUnit;
	
	/**
	 * 参数单位主码
	 */
	private Integer parameterUnitMC;
	

	/**
	 * 分辨率
	 */	
	private Float resolution;
	
	/**
	 * 偏移量
	 */	
	private Float offset;

	/**
	 * 数据类型
	 */
	private Integer infoType;

	/**
	 * 扩展位
	 */
	private String extension;
	
	private boolean bigEndianData;

	public boolean isBigEndianData() {
		return bigEndianData;
	}

	public void setBigEndianData(boolean bigEdianData) {
		this.bigEndianData = bigEdianData;
	}

	/**
	 * 本模板参数在TPara2Treat中的关联记录的列表
	 */
	private List<TPara2Treat> tPara2TreatList;

	public TemplatePara(String plt_id, String oid, String parameterID, String parameterName, Integer parameterType,
                        Integer parameterTypeMC, Integer length, Integer parameterUnit, Integer parameterUnitMC, float resolution,
                        float offset, Integer infoType, String extension) {
		super();
		this.plt_id = plt_id;
		this.oid = oid;
		this.parameterID = parameterID;
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.parameterTypeMC = parameterTypeMC;
		this.length = length;
		this.parameterUnit = parameterUnit;
		this.parameterUnitMC = parameterUnitMC;
		this.resolution = resolution;
		this.offset = offset;
		this.infoType = infoType;
		this.extension = extension;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public Integer getParameterType() {
		return parameterType;
	}

	public void setParameterType(Integer parameterType) {
		this.parameterType = parameterType;
	}

	public Integer getParameterTypeMC() {
		return parameterTypeMC;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getOid() {
		return oid;
	}

	public List<TPara2Treat> gettPara2TreatList() {
		return tPara2TreatList;
	}

	public void settPara2TreatList(List<TPara2Treat> tPara2TreatList) {
		this.tPara2TreatList = tPara2TreatList;
	}

	public String getParameterID() {
		return parameterID;
	}

	public void setParameterID(String parameterID) {
		this.parameterID = parameterID;
	}

	public Integer getParameterUnit() {
		return parameterUnit;
	}

	public void setParameterUnit(Integer parameterUnit) {
		this.parameterUnit = parameterUnit;
	}
	
	public Integer getParameterUnitMC() {
		return parameterUnitMC;
	}

	
	public float getResolution() {
		return resolution;
	}
	public void setResolution(Float resolution) {
		this.resolution = resolution;
	}
	
	public float getOffset() {
		return offset;
	}
	public void setOffset(Float offset) {
		this.offset = offset;
	}
	
	public Integer getInfoType() {
		return infoType;
	}
	public void setInfoType(Integer infoType) {
		this.infoType = infoType;
	}	
	
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}			
	
	public edu.thss.entity.TemplatePara clone(){
		return new edu.thss.entity.TemplatePara(plt_id, oid, parameterID, parameterName, parameterType, parameterTypeMC, length, parameterUnit, parameterUnitMC, resolution, offset, infoType, extension);
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setParameterTypeMC(Integer parameterTypeMC) {
		this.parameterTypeMC = parameterTypeMC;
	}

	public void setParameterUnitMC(Integer parameterUnitMC) {
		this.parameterUnitMC = parameterUnitMC;
	}
	
	
}
