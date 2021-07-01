package edu.thss.entity;

import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.Device;
import edu.thss.entity.TemplatePara;
import edu.thss.entity.TransPacket;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 报文解析后的数据封装对象
 * 
 * @author zhuangxy 2012-12-30
 */
public class ParsedDataPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8411189612097114182L;

	/**
	 * 时间戳信息
	 */
	private long timestamp;

	/**
	 * 工况数据的发送设备
	 */
	private Device device;
//	private Train train;

	/**
	 * 协议模板的公有标识，用于进行设备识别
	 */
	private TemplatePara commonKey;

	/**
	 * 协议模板的唯一标识，用于进行设备识别
	 */
	private TemplatePara uniqueKey;

	/**
	 * 数据来源ip
	 */
	private String ip;

	/**
	 * 数据包ID
	 */
	private String id;
	
	/**
	 * 原始数据包ID
	 */
	private String rawDataId;
	
	/**
	 * kafka主题来源
	 */
	private String topic;
	
	/**
	 * 协议类型
	 */
	private String protoId;
	
	/**
	 * IMEI
	 */
	private String IMEI;
	
	/**
	 * 信息类型，属性值是：工况编号_工况值
	 */
	private String MsgType;
	
	/**
	 * 设备号
	 */
	private String deviceID;
	
	/**
	 * 采集时间
	 */
	private long gathertime;
	/**
	 * 存储时间
	 */
	private long storetime;
	/**
	 * 发送时间
	 */
	private long sendtime;
	/**
	 * 接收数据包时间
	 */
	private long recievetime;
	
	
	/**
	 * 数据报文基础信息 key = 模板参数（TemplatePara）的参数编号parameterID value = 模板参数对应的数值
	 */
	private Map<String, String> baseInfoMap;

	/**
	 * 工况数据信息 key = 模板参数（TemplatePara）的参数编号parameterID value = 模板参数对应的数值
	 */
	private Map<String, Map<Long, String>> workStatusMap;
	
	/**
	 * 实时计算工况数据信息 key = 模板参数（TemplatePara）的参数编号parameterID value = 模板参数对应的数值
	 */
	private Map<String, Map<Long, String>> calculationStatusMap;

	private byte[] rawData;
	
	public ParsedDataPacket() {
		this.baseInfoMap = new LinkedHashMap<String, String>();
		this.workStatusMap = new LinkedHashMap<String, Map<Long, String>>();
		this.setCalculationStatusMap(new LinkedHashMap<String, Map<Long, String>>());
	}
	
	public ParsedDataPacket(long timestamp, Device device, TemplatePara commonKey, TemplatePara uniqueKey, String ip, String id,
                            Map<String, String> baseInfoMap, Map<String, Map<Long, String>> workStatusMap, Map<String, Map<Long, String>> calculationStatusMap) {
		super();
		this.timestamp = timestamp;
		this.device = device;
		this.commonKey = commonKey;
		this.uniqueKey = uniqueKey;
		this.ip = ip;
		this.id = id;
		this.baseInfoMap = baseInfoMap;
		this.workStatusMap = workStatusMap;
		this.setCalculationStatusMap(calculationStatusMap);
	}

//	public Train getTrain() { return  train; }
//	public void setTrain(Train train) { this.train = train; }

	public byte[] getRawData() { return rawData; }
	public void setRawData(byte[] rawData) { this.rawData = rawData; }

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getBaseInfoMap() {
		return baseInfoMap;
	}

	public void setBaseInfoMap(Map<String, String> baseInfoMap) {
		this.baseInfoMap = baseInfoMap;
	}

	public Map<String, Map<Long, String>> getWorkStatusMap() {
		return workStatusMap;
	}

	public void setWorkStatusMap(Map<String, Map<Long, String>> workStatusMap) {
		this.workStatusMap = workStatusMap;
	}
	
	public void addBaseInfo(String workStatus, String value) {
		baseInfoMap.put(workStatus, value);
	}

	public void addWorkStatus(String workStatus, Long time, String value) {
		if (workStatusMap.containsKey(workStatus)) {
			workStatusMap.get(workStatus).put(time, value);
		} else {
			Map<Long, String> tmp = new LinkedHashMap<Long, String>();
			tmp.put(time, value);
			workStatusMap.put(workStatus, tmp);
		}
	}
	
	public void removeBaseInfo(String workStatus){
		baseInfoMap.remove(workStatus);
	}
	
	public void removeWorkStatus(String workStatus, Long time){
	    Map<Long, String> tmp = workStatusMap.get(workStatus);
	    if(tmp.size() == 1)
	    	workStatusMap.remove(workStatus);
	    else {
	    	tmp.remove(time);
		}    
	}
	
	public Iterator<WorkStatusRecord> getWorkStatusMapIter(){
		return new NestedIterator();
	}
	
	public Iterator<Entry<String, String>> getBaseInfoMapIter(){
		return baseInfoMap.entrySet().iterator();
	}
	
	public TemplatePara getCommonKey() {
		return commonKey;
	}

	public void setCommonKey(TemplatePara commonKey) {
		this.commonKey = commonKey;
	}

	public TemplatePara getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(TemplatePara uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public class WorkStatusRecord {
		String workStatusId;
		Long time;
		String value;

		public String getWorkStatusId() {
			return workStatusId;
		}

		public void setWorkStatusId(String workStatusId) {
			this.workStatusId = workStatusId;
		}

		public Long getTime() {
			return time;
		}

		public void setTime(Long time) {
			this.time = time;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public WorkStatusRecord(String workStatusId, Long time, String value) {
			super();
			this.workStatusId = workStatusId;
			this.time = time;
			this.value = value;
		}

	}

	public class NestedIterator implements Iterator<WorkStatusRecord> {
		Iterator<Entry<String, Map<Long, String>>> outter = workStatusMap.entrySet().iterator();
		Entry<String, Map<Long, String>> current = null;
		Iterator<Entry<Long, String>> inner = null;

		public boolean hasNext() {
			return (outter.hasNext() || (inner != null && inner.hasNext()));
		}

		public WorkStatusRecord next() {
			if (outter.hasNext() && (current == null || !inner.hasNext())) {
				current = outter.next();
				inner = current.getValue().entrySet().iterator();
			}
			if (inner.hasNext()) {
				Entry<Long, String> tmp = inner.next();
				return new WorkStatusRecord(current.getKey(), tmp.getKey(), tmp.getValue());
			}
			return null;
		}

		public void remove(){
			if(inner != null){
				inner.remove();
				if(current.getValue().size() == 0)
					outter.remove();
			}
		}
	}

	public TransPacket getTransPacket() {
		String id = "";
		if (device != null)
			id = device.getDeviceID();
		return new TransPacket(timestamp, id, ip, baseInfoMap, workStatusMap);
	}

	public String getRawDataId() {
		return rawDataId;
	}

	public void setRawDataId(String rawDataId) {
		this.rawDataId = rawDataId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getProtoId() {
		return protoId;
	}

	public void setProtoId(String protoId) {
		this.protoId = protoId;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public long getGathertime() {
		return gathertime;
	}

	public void setGathertime(long gathertime) {
		this.gathertime = gathertime;
	}

	public long getStoretime() {
		return storetime;
	}

	public void setStoretime(long storetime) {
		this.storetime = storetime;
	}

	public long getSendtime() {
		return sendtime;
	}

	public void setSendtime(long sendtime) {
		this.sendtime = sendtime;
	}

	public long getRecievetime() {
		return recievetime;
	}

	public void setRecievetime(long recievetime) {
		this.recievetime = recievetime;
	}

	public Map<String, Map<Long, String>> getCalculationStatusMap() {
		return calculationStatusMap;
	}

	public void setCalculationStatusMap(Map<String, Map<Long, String>> calculationStatusMap) {
		this.calculationStatusMap = calculationStatusMap;
	}



//  for Si Fang
	private String line;

	private String car;

	private String terminal;

	public String getLine() { return line; }

	public void setLine(String line) { this.line = line; }

	public String getCar() { return car; }

	public void setCar(String car) { this.car = car; }

	public String getTerminal() { return terminal; }

	public void setTerminal(String terminal) { this.terminal = terminal; }

	public JSONObject map2Json(Map<String, Map<Long, String>> map){
		JSONObject jsonObject = new JSONObject();
		for(Entry<String, Map<Long, String>> entry: map.entrySet()){
			JSONObject jsonObject1 = new JSONObject();
			for(Entry<Long, String> entry1: entry.getValue().entrySet()){
				jsonObject1.put(entry1.getKey().toString(), entry1.getValue());
			}
			jsonObject.put(entry.getKey(), jsonObject1);
		}
		return jsonObject;

	}

	public JSONObject toJson(){
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp);
		res.put("device", device);
		res.put("commonKey", commonKey);
		res.put("uniqueKey", uniqueKey);
		res.put("ip", ip);
		res.put("id", id);
		res.put("rawDataId", rawDataId);
		res.put("topic", topic);
		res.put("protoId", protoId);
		res.put("IMEI", IMEI);
		res.put("MsgType", MsgType);
		res.put("deviceID", deviceID);
		res.put("gathertime", gathertime);
		res.put("storetime", storetime);
		res.put("sendtime", sendtime);
		res.put("recievetime", recievetime);
		res.put("baseInfoMap", baseInfoMap);
		res.put("workStatusMap", map2Json(workStatusMap));
		res.put("calculationStatusMap", map2Json(calculationStatusMap));
		res.put("rawData", Arrays.toString(rawData));
		res.put("line", line);
		res.put("car", car);
		res.put("terminal", terminal);
		return res;
	}


}
