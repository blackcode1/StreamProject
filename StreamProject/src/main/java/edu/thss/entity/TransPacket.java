package edu.thss.entity;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TransPacket implements Serializable {
	private static final long serialVersionUID = 104852240502245447L;
	private long timestamp;
	private String deviceId;
	private String ip;
	private String msgType;
	private String rawDataId;
	private Map<String, String> baseInfoMap;
	private Map<String, Map<Long, String>> workStatusMap;
	private Map<String, Map<Long, String>> calculationStatusMap;

	public TransPacket() {
		this.baseInfoMap = new LinkedHashMap();
		this.workStatusMap = new LinkedHashMap();
		this.calculationStatusMap = new LinkedHashMap();
	}

	public TransPacket(long timestamp, String deviceId, String ip, Map<String, String> baseInfoMap, Map<String, Map<Long, String>> workStatusMap) {
		this.timestamp = timestamp;
		this.deviceId = deviceId;
		this.ip = ip;
		this.baseInfoMap = baseInfoMap;
		this.workStatusMap = workStatusMap;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getRawDataId() {
		return this.rawDataId;
	}

	public void setRawDataId(String rawDataId) {
		this.rawDataId = rawDataId;
	}

	public String getMsgType() {
		return this.msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Map<String, Map<Long, String>> getCalculationStatusMap() {
		return this.calculationStatusMap;
	}

	public void setCalculationStatusMap(Map<String, Map<Long, String>> calculationStatusMap) {
		this.calculationStatusMap = calculationStatusMap;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, String> getBaseInfoMap() {
		return this.baseInfoMap;
	}

	public void setBaseInfoMap(Map<String, String> baseInfoMap) {
		this.baseInfoMap = baseInfoMap;
	}

	public Map<String, Map<Long, String>> getWorkStatusMap() {
		return this.workStatusMap;
	}

	public void setWorkStatusMap(Map<String, Map<Long, String>> workStatusMap) {
		this.workStatusMap = workStatusMap;
	}

	public void addBaseInfo(String workStatus, String value) {
		this.baseInfoMap.put(workStatus, value);
	}

	public void addWorkStatus(String workStatus, long time, String value) {
		if (this.workStatusMap.containsKey(workStatus)) {
			((Map)this.workStatusMap.get(workStatus)).put(time, value);
		} else {
			Map<Long, String> tmp = new LinkedHashMap();
			tmp.put(time, value);
			this.workStatusMap.put(workStatus, tmp);
		}

	}

	public Iterator<TransPacket.WorkStatusRecord> getWorkStatusMapIter() {
		return new TransPacket.NestedIterator();
	}

	public Iterator<Entry<String, String>> getBaseInfoMapIter() {
		return this.baseInfoMap.entrySet().iterator();
	}

	public class NestedIterator implements Iterator<TransPacket.WorkStatusRecord> {
		Iterator<Entry<String, Map<Long, String>>> outter;
		Entry<String, Map<Long, String>> current;
		Iterator<Entry<Long, String>> inner;

		public NestedIterator() {
			this.outter = TransPacket.this.workStatusMap.entrySet().iterator();
			this.current = null;
			this.inner = null;
		}

		public boolean hasNext() {
			return this.outter.hasNext() || this.inner != null && this.inner.hasNext();
		}

		public TransPacket.WorkStatusRecord next() {
			if (this.outter.hasNext() && (this.current == null || !this.inner.hasNext())) {
				this.current = (Entry)this.outter.next();
				this.inner = ((Map)this.current.getValue()).entrySet().iterator();
			}

			if (this.inner.hasNext()) {
				Entry<Long, String> tmp = (Entry)this.inner.next();
				return TransPacket.this.new WorkStatusRecord((String)this.current.getKey(), (Long)tmp.getKey(), (String)tmp.getValue());
			} else {
				return null;
			}
		}
	}

	public class WorkStatusRecord {
		String workStatusId;
		long time;
		String value;

		public String getWorkStatusId() {
			return this.workStatusId;
		}

		public void setWorkStatusId(String workStatusId) {
			this.workStatusId = workStatusId;
		}

		public long getTime() {
			return this.time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public WorkStatusRecord(String workStatusId, long time, String value) {
			this.workStatusId = workStatusId;
			this.time = time;
			this.value = value;
		}
	}
}