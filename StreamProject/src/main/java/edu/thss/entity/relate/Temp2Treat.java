package edu.thss.entity.relate;

import edu.thss.entity.TempTreClass;
import edu.thss.entity.Template;
import edu.thss.entity.TemplatePara;
import edu.thss.entity.relate.Temp2TreatPK;

import java.io.Serializable;

/**
 * 模板到模板处理方案
 * @author zhuangxy
 * 2013-1-15
 */
public class Temp2Treat implements Serializable{

	/**
	 * 内嵌复合主键
	 */
	private Temp2TreatPK pk;
	
	/**
	 * 处理方案相关模板参数
	 */
	private TemplatePara templatePara;
	
	/**
	 * 处理参数（结构化字符串）
	 */
	private String treatParameter;

	/**
	 * 处理顺序
	 */
	private int treatSequence;
	
	public Temp2Treat() {
		this.pk = new Temp2TreatPK();
	}

	public Template getTemplate() {
		return pk.template;
	}

	public TempTreClass getTemplateTreat() {
		return pk.templateTreat;
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

	public TemplatePara getTemplatePara() {
		return templatePara;
	}

	public void setTemplatePara(TemplatePara templatePara) {
		this.templatePara = templatePara;
	}

	public void setTemplate(Template template) {
		this.pk.template = template;
	}

	public void setTemplateTreat(TempTreClass templateTreat) {
		this.pk.templateTreat = templateTreat;
	}
	
}
