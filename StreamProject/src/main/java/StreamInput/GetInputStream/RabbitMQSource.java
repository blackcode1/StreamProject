package StreamInput.GetInputStream;

import StreamDataPacket.BaseClassDataType.TransPacketRely.ParsedDataPacketDeserializationSchema;
import StreamDataPacket.BaseClassDataType.TransPacketRely.RawPacketDeserializationSchema;
import StreamDataPacket.DataTypeChange.*;
import StreamDataPacket.DataType;
import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketDeserializationSchema;
import edu.thss.entity.ParsedDataPacket;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import ty.pub.RawDataPacket;
import ty.pub.TransPacket;

public class RabbitMQSource {

    public static DataStream<DataType> getRabbitMQStream
            (StreamExecutionEnvironment env, String dataType, String ip, String port, String gropID, String topicName
                    , String pid, String datasetID, String user, String pw){
        if(dataType.equals("JSONObject")){
            return env.addSource(getRabbitMQJsonConsumer(ip, port, gropID, topicName, user, pw))
                    .map(new MapJS2Datatype(pid, true))
                    .map(new ADDDatatyeSource(datasetID, true));
        }
        else if(dataType.equals("TransPacket")){
            return env.addSource(getRabbitMQTranspacketConsumer(ip, port, gropID, topicName, user, pw))
                    .map(new MapTP2DataType(pid))
                    .map(new ADDDatatyeSource(datasetID));
        }
        else if(dataType.equals("RawDataPacket")){
            return env.addSource(getRabbitMQRawpacketConsumer(ip, port, gropID, topicName, user, pw))
                    .map(new MapRP2DataType())
                    .map(new ADDDatatyeSource(datasetID));
        }
        else if(dataType.equals("ParsedDataPacket")){
            return env.addSource(getRabbitMQParsedDataPacketConsumer(ip, port, gropID, topicName, user, pw))
                    .map(new MapPDP2DataType(pid))
                    .map(new ADDDatatyeSource(datasetID));
        }
        return null;
    }

    public static RMQSource<String> getRabbitMQJsonConsumer(String ip, String port, String gropID, String topicName, String user, String pw) {
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(ip)
                .setPort(Integer.valueOf(port))
                .setUserName(user)
                .setPassword(pw)
                .setVirtualHost("/")
                .build();
        RMQSource<String> rmqSource = new RMQSource<String>(connectionConfig, topicName, new SimpleStringSchema());
        return rmqSource;
    }

    public static RMQSource<TransPacket> getRabbitMQTranspacketConsumer(String ip, String port, String gropID, String topicName, String user, String pw) {
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(ip)
                .setPort(Integer.valueOf(port))
                .setUserName(user)
                .setPassword(pw)
                .setVirtualHost("/")
                .build();

        RMQSource<TransPacket> rmqSource = new RMQSource<TransPacket>(connectionConfig, topicName, new TransPacketDeserializationSchema());
        return rmqSource;
    }

    public static RMQSource<RawDataPacket> getRabbitMQRawpacketConsumer(String ip, String port, String gropID, String topicName, String user, String pw) {
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(ip)
                .setPort(Integer.valueOf(port))
                .setUserName(user)
                .setPassword(pw)
                .setVirtualHost("/")
                .build();

        RMQSource<RawDataPacket> rmqSource = new RMQSource<RawDataPacket>(connectionConfig, topicName, new RawPacketDeserializationSchema());
        return rmqSource;
    }

    public static RMQSource<ParsedDataPacket> getRabbitMQParsedDataPacketConsumer(String ip, String port, String gropID, String topicName, String user, String pw) {
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(ip)
                .setPort(Integer.valueOf(port))
                .setUserName(user)
                .setPassword(pw)
                .setVirtualHost("/")
                .build();

        RMQSource<ParsedDataPacket> rmqSource = new RMQSource<ParsedDataPacket>(connectionConfig, topicName, new ParsedDataPacketDeserializationSchema());
        return rmqSource;
    }
}
