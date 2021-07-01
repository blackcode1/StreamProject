package StreamTest;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSinkPublishOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyRMQSinkOpts<IN> implements RMQSinkPublishOptions{
    String routeKey;
    String exchange;
    public static AMQP.BasicProperties pros;
    String type;

    public MyRMQSinkOpts(String routeKey, String exchange, AMQP.BasicProperties pros) {
        this.routeKey = routeKey;
        this.exchange = exchange;
        this.pros = pros;
        this.type = "";
    }

    public MyRMQSinkOpts(String routeKey, String exchange, String type, AMQP.BasicProperties pros) {
        this.routeKey = routeKey;
        this.exchange = exchange;
        this.type = type;
        this.pros = pros;
    }

    @Override
    public String computeRoutingKey(Object o) {
        if(type.equals("JSONObject")){
            String ress = (String) o;
            JSONObject res = JSONObject.parseObject(ress);
            if(res.containsKey("routeKey")){
                return res.getString("routeKey");
            }
        }
        return routeKey;
    }

    @Override
    public AMQP.BasicProperties computeProperties(Object o) {
        if(type.equals("JSONObject")){
            String ress = (String) o;
            JSONObject res = JSONObject.parseObject(ress);
            if(res.containsKey("delay")){
                Map<String, Object> delayHeaders = new HashMap<String, Object>();
                delayHeaders.put("x-delay", res.getIntValue("delay"));
                AMQP.BasicProperties.Builder delayProps = new AMQP.BasicProperties.Builder().headers(delayHeaders);
                return delayProps.build();
            }
            else if(res.containsKey("exchange")){
                return MessageProperties.PERSISTENT_TEXT_PLAIN;
            }
        }
        return pros;
    }

    @Override
    public String computeExchange(Object o) {
        if(type.equals("JSONObject")){
            String ress = (String) o;
            JSONObject res = JSONObject.parseObject(ress);
            if(res.containsKey("exchange")){
                return res.getString("exchange");
            }
        }
        return exchange;
    }
}
