package StreamSink;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSinkPublishOptions;
import org.apache.flink.streaming.connectors.rabbitmq.SerializableReturnListener;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;
import java.io.IOException;

public class RichRMQSink<IN> extends RMQSink<IN> {
    @Nullable
    private final RMQSinkPublishOptions<IN> publishOptions;

    public RichRMQSink(RMQConnectionConfig rmqConnectionConfig, String queueName, SerializationSchema<IN> schema) {
        super(rmqConnectionConfig, queueName, schema);
        this.publishOptions = null;
    }

    public RichRMQSink(RMQConnectionConfig rmqConnectionConfig, SerializationSchema<IN> schema, RMQSinkPublishOptions<IN> publishOptions) {
        super(rmqConnectionConfig, schema, publishOptions);
        this.publishOptions = publishOptions;
    }
    /**
     * 此方法必须重写，如果RabbitMQ的queue的durable属性设置为true，则会导致RabbitMQ会一直connection，导致RabbitMQ耗尽资源挂掉
     */
    @Override
    protected void setupQueue() {

    }

    public void invoke(IN value) {
        try {
            byte[] msg = this.schema.serialize(value);
            if(value.getClass().equals(String.class)){
                String jsonS = (String) value;
                JSONObject jsonObject = JSONObject.parseObject(jsonS);
                if(jsonObject.containsKey("resStr")){
                    msg = this.schema.serialize((IN) jsonObject.get("resStr"));
                }
            }

            if (this.publishOptions == null) {
                super.channel.basicPublish("", super.queueName, (AMQP.BasicProperties)null, msg);
            } else {
                boolean mandatory = this.publishOptions.computeMandatory(value);
                boolean immediate = this.publishOptions.computeImmediate(value);
                String rk = this.publishOptions.computeRoutingKey(value);
                String exchange = this.publishOptions.computeExchange(value);
                this.channel.basicPublish(exchange, rk, mandatory, immediate, this.publishOptions.computeProperties(value), msg);
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }

    }
}
