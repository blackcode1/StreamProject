package edu.thss.entity.relate;

import edu.thss.entity.Template;
import edu.thss.entity.TemplatePara;

import java.io.Serializable;


public class Tem2TemParaPK implements Serializable{

	public Tem2TemParaPK() {
	}

	/**
	 * 左类oid, 模板
	 */
	public Template template;

	/**
	 * 右类oid,模板参数
	 */
	public TemplatePara templatePara;

	@Override
	public int hashCode() {
		return (template.getOid() + templatePara.getOid()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof edu.thss.entity.relate.Tem2TemParaPK))
			return false;
		if (obj == null)
			return false;
		edu.thss.entity.relate.Tem2TemParaPK pk = (edu.thss.entity.relate.Tem2TemParaPK) obj;
		return pk.template.equals(this.template)
				&& pk.templatePara.equals(this.templatePara);
	}

}
