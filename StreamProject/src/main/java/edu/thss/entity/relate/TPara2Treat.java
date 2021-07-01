package edu.thss.entity.relate;

import edu.thss.entity.ParaTreatClass;
import edu.thss.entity.TemplatePara;
import edu.thss.entity.relate.TPara2TreatPK;

/**
 * 模板参数到参数处理方案
 * 
 * @author zhuangxy 2012-12-25
 */

public class TPara2Treat implements java.io.Serializable {

	/**
	 * 内嵌复合主键
	 */
	private TPara2TreatPK pk;

	/**
	 * 处理参数（结构化字符串）
	 */
	private String treatParameter;

	/**
	 * 处理顺序
	 */
	private int treatSequence;
	
	public TPara2Treat() {
		this.pk = new TPara2TreatPK();
	}

	public TemplatePara getTemplatePara() {
		return this.pk.templatePara;
	}

	public ParaTreatClass getParaTreatClass() {
		return this.pk.paraTreatClass;
	}

	public String getTreatParameter() {
		return treatParameter;
	}

	public void setTreatParameter(String treatParameter) {
		this.treatParameter = treatParameter;
	}

	public int getTreatSequence() {
		return treatSequence;
	}

	public void setTreatSequence(int treatSequence) {
		this.treatSequence = treatSequence;
	}

	public void setTemplatePara(TemplatePara templatePara) {
		this.pk.templatePara = templatePara;
	}

	public void setParaTreatClass(ParaTreatClass paraTreatClass) {
		this.pk.paraTreatClass = paraTreatClass;
	}

	public String toString(){
		return pk.paraTreatClass.getOid();
	}

}
