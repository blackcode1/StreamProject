package edu.thss.entity.relate;

import edu.thss.entity.ParaTreatClass;
import edu.thss.entity.TemplatePara;

import java.io.Serializable;


public class TPara2TreatPK implements Serializable{
	
	public TPara2TreatPK() {
	}
	
	/**
	 * 模板参数
	 */
	public TemplatePara templatePara;

	/**
	 * 模板参数处理方案
	 */
	public ParaTreatClass paraTreatClass;

	@Override
	public int hashCode() {
		return (templatePara.getOid() + paraTreatClass.getOid()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof edu.thss.entity.relate.TPara2TreatPK))
			return false;
		if (obj == null)
			return false;
		edu.thss.entity.relate.TPara2TreatPK pk = (edu.thss.entity.relate.TPara2TreatPK) obj;
		return pk.templatePara.equals(this.templatePara)
				&& pk.paraTreatClass.equals(this.paraTreatClass);
	}

}
