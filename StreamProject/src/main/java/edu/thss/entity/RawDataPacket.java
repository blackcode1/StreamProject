package edu.thss.entity;

import java.io.Serializable;

/**
 * 鍘熷鏁版嵁鍖�
 * @author ZWX
 *
 */
public class RawDataPacket implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -488894059909768006L;
	
	private String rawDataId;

	private String Topic;
	
	private String sourceIp;
	
	private String sourcePort;
	
	private String serverIp;
	
	private String serverPort;
	
	private String IMEI;
	
	/**
	 * 鏁版嵁鏉ユ簮
	 */
	private Object packetData;
	
	/**
	 * 鏃堕棿鎴�
	 */
	private long timestamp;

	public String getRawDataId() {
		return rawDataId;
	}

	public void setRawDataId(String rawDataId) {
		this.rawDataId = rawDataId;
	}

	public String getTopic() {
		return Topic;
	}

	public void setTopic(String topic) {
		Topic = topic;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public Object getPacketData() {
		return packetData;
	}

	public void setPacketData(Object packetData) {
		this.packetData = packetData;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public RawDataPacket(String rawDataId, String topic, String sourceIp, String sourcePort, String serverIp, String serverPort,
			String iMEI, Object packetData, long timestamp) {
		super();
		this.rawDataId = rawDataId;
		Topic = topic;
		this.sourceIp = sourceIp;
		this.sourcePort = sourcePort;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		IMEI = iMEI;
		this.packetData = packetData;
		this.timestamp = timestamp;
	}

	public RawDataPacket() {
		// TODO Auto-generated constructor stub
	}

	
}
