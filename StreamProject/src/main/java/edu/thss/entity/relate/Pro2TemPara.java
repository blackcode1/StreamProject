package edu.thss.entity.relate;

import java.io.Serializable;

public class Pro2TemPara implements Serializable {

	private Integer offset;
	private Integer order;
	private String proOid, paraOid;

	public Pro2TemPara(String proOid, String paraOid, Integer order, Integer offset) {
		this.proOid = proOid;
		this.paraOid = paraOid;
		this.order = order;
		this.offset = offset;
	}
	
	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getProOid() {
		return this.proOid;
	}

	public void setProOid(String proOid) {
		this.proOid = proOid;
	}
	
	public String getParaOid() {
		return this.paraOid;
	}

	public void setParaOid(String paraOid) {
		this.paraOid = paraOid;
	}
	
	public String toString(){
		return this.paraOid;
	}
}
