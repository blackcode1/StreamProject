package edu.thss.entity.relate;

import edu.thss.entity.Protocol;
import edu.thss.entity.Template;

import java.io.Serializable;


public class Pro2TempPK implements Serializable {

	public Pro2TempPK() {
	}

	/**
	 * 协议
	 */
	public Protocol protocol;

	/**
	 * 模板
	 */

	public Template template;

	@Override
	public int hashCode() {
		return (protocol.getOid() + template.getOid()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof edu.thss.entity.relate.Pro2TempPK))
			return false;
		if (obj == null)
			return false;
		edu.thss.entity.relate.Pro2TempPK pk = (edu.thss.entity.relate.Pro2TempPK) obj;
		return pk.protocol.equals(this.protocol)
				&& pk.template.equals(this.template);
	}

}
