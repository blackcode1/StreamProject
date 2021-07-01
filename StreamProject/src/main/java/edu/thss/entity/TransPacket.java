package edu.thss.entity;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TransPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 104852240502245447L;

	public TransPacket() {
		this.baseInfoMap = new LinkedHashMap<String, String>();
		this.workStatusMap = new LinkedHashMap<String, Map<Long, String>>();
	}
	
	public TransPacket(long timestamp, String deviceId, String ip, Map<String, String> baseInfoMap,
			Map<String, Map<Long, String>> workStatusMap) {
		super();
		this.timestamp = timestamp;
		this.deviceId = deviceId;
		this.ip = ip;
		this.baseInfoMap = baseInfoMap;
		this.workStatusMap = workStatusMap;
	}



	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * 鏃堕棿鎴�
	 */
	private long timestamp;

	/**
	 * 宸ュ喌鏁版嵁鐨勫彂閫佽澶�
	 */
	private String deviceId;

	/**
	 * 鏁版嵁鏉ユ簮ip
	 */
	private String ip;

	/**
	 * 鏁版嵁鎶ユ枃鍩虹淇℃伅 key = 妯℃澘鍙傛暟锛圱emplatePara锛夌殑鍙傛暟缂栧彿parameterID value = 妯℃澘鍙傛暟瀵瑰簲鐨勬暟鍊�
	 */
	private Map<String, String> baseInfoMap;

	/**
	 * 宸ュ喌鏁版嵁淇℃伅 key = 妯℃澘鍙傛暟锛圱emplatePara锛夌殑鍙傛暟缂栧彿parameterID value = 妯℃澘鍙傛暟瀵瑰簲鐨勬暟鍊�
	 */
	private Map<String, Map<Long, String>> workStatusMap;
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public void addWorkStatus(String workStatus, long time, String value) {
		if (workStatusMap.containsKey(workStatus)) {
			workStatusMap.get(workStatus).put(time, value);
		} else {
			Map<Long, String> tmp = new LinkedHashMap<Long, String>();
			tmp.put(time, value);
			workStatusMap.put(workStatus, tmp);
		}
	}

	public Iterator<Entry<String, String>> getBaseInfoMapIter(){
		return baseInfoMap.entrySet().iterator();
	}

	public class WorkStatusRecord {
		String workStatusId;
		long time;
		String value;

		public String getWorkStatusId() {
			return workStatusId;
		}

		public void setWorkStatusId(String workStatusId) {
			this.workStatusId = workStatusId;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public WorkStatusRecord(String workStatusId, long time, String value) {
			super();
			this.workStatusId = workStatusId;
			this.time = time;
			this.value = value;
		}

	}

}
