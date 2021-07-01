package edu.thss.entity;

import edu.thss.entity.CanID;
import edu.thss.entity.Protocol;
import edu.thss.entity.Template;
import edu.thss.entity.TemplatePara;

import java.util.HashMap;
import java.util.Map;

public class PaserInfo {
    private Map<String, Protocol> ProMap = new HashMap<String, Protocol>();
    //协议oid-协议处理方案类名
    private Map<String,String> vernoToProtreatMap = new HashMap<String,String>();
    //协议oid_信息类型-<模板ID,版本号列表>
    private Map<String,Map<String, String>> regParaToTemIDMap = new HashMap<String,Map<String, String>>();

    //protoOid_regPara, template
    private Map<String, Template> regParaToTempMap = new HashMap<String, Template>();

    private Map<String, Template> templateMap = new HashMap<String, Template>();
    private Map<String, TemplatePara> tempParaMap = new HashMap<String, TemplatePara>();

    //oid， tempPara
    private Map<String, TemplatePara> idParaMap = new HashMap<String, TemplatePara>();

    private Map<String, CanID> canIDMap = new HashMap<String, CanID>();

    public PaserInfo() {
    }

    public PaserInfo(Map<String, Protocol> proMap, Map<String, String> vernoToProtreatMap, Map<String, Map<String, String>> regParaToTemIDMap) {
        this.ProMap = proMap;
        this.vernoToProtreatMap = vernoToProtreatMap;
        this.regParaToTemIDMap = regParaToTemIDMap;
    }

    public Map<String, Protocol> getProMap() {
        return ProMap;
    }

    public void setProMap(Map<String, Protocol> proMap) {
        ProMap = proMap;
    }

    public Map<String, String> getVernoToProtreatMap() {
        return vernoToProtreatMap;
    }

    public void setVernoToProtreatMap(Map<String, String> vernoToProtreatMap) {
        this.vernoToProtreatMap = vernoToProtreatMap;
    }

    public Map<String, Map<String, String>> getRegParaToTemIDMap() {
        return regParaToTemIDMap;
    }

    public void setRegParaToTemIDMap(Map<String, Map<String, String>> regParaToTemIDMap) {
        this.regParaToTemIDMap = regParaToTemIDMap;
    }

    public Map<String, Template> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, Template> templateMap) {
        this.templateMap = templateMap;
    }

    public Map<String, TemplatePara> getTempParaMap() {
        return tempParaMap;
    }

    public void setTempParaMap(Map<String, TemplatePara> tempParaMap) {
        this.tempParaMap = tempParaMap;
    }

    public Map<String, Template> getRegParaToTempMap() {
        return regParaToTempMap;
    }

    public void setRegParaToTempMap(Map<String, Template> regParaToTempMap) {
        this.regParaToTempMap = regParaToTempMap;
    }

    public Map<String, TemplatePara> getIdParaMap() {
        return idParaMap;
    }

    public void setIdParaMap(Map<String, TemplatePara> idParaMap) {
        this.idParaMap = idParaMap;
    }

    public Map<String, CanID> getCanIDMap() {
        return canIDMap;
    }

    public void setCanIDMap(Map<String, CanID> canIDMap) {
        this.canIDMap = canIDMap;
    }
}
