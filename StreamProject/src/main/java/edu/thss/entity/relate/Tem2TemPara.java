package edu.thss.entity.relate;

import edu.thss.entity.Template;
import edu.thss.entity.TemplatePara;
import edu.thss.entity.relate.Tem2TemParaPK;

/**
 * 模板到模板参数
 * 
 * @author zhuangxy 2012-12-25
 */

public class Tem2TemPara implements java.io.Serializable {

	/**
	 * 内嵌复合主键
	 */
	private Tem2TemParaPK pk;

	public Tem2TemParaPK getPk() {
		return pk;
	}

	/**
	 * 偏移量
	 */
	private String offset;

	/**
	 * 模板参数的排列顺序
	 */
	private Integer tpOrder;

	public Tem2TemPara() {
		this.pk = new Tem2TemParaPK();
	}
	
	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public Integer getTpOrder() {
		return tpOrder;
	}

	public void setTpOrder(Integer tpOrder) {
		this.tpOrder = tpOrder;
	}

	public Template getTemplate() {
		return this.pk.template;
	}

	public TemplatePara getTemplatePara() {
		return this.pk.templatePara;
	}
	
	
	public void setTemplate(Template template) {
		this.pk.template = template;
	}

	public void setTemplatePara(TemplatePara templatePara) {
		this.pk.templatePara = templatePara;
	}
	
	public String toString(){
		if(pk.templatePara==null)
			return "error";
		return pk.templatePara.getOid();
	}
}
