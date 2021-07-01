package edu.thss.entity.relate;

import edu.thss.entity.TempTreClass;
import edu.thss.entity.Template;

import java.io.Serializable;


public class Temp2TreatPK implements Serializable{

	public Temp2TreatPK() {
	}

	/**
	 * 左类oid, 模板
	 */
	public Template template;

	/**
	 * 右类oid,模板处理方案
	 */
	public TempTreClass templateTreat;

	@Override
	public int hashCode() {
		return (template.getOid() + templateTreat.getOid()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof edu.thss.entity.relate.Temp2TreatPK))
			return false;
		if (obj == null)
			return false;
		edu.thss.entity.relate.Temp2TreatPK pk = (edu.thss.entity.relate.Temp2TreatPK) obj;
		return pk.template.equals(this.template)
				&& pk.templateTreat.equals(this.templateTreat);
	}
}
