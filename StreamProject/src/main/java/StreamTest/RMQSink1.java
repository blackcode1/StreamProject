package StreamTest;

import com.rabbitmq.client.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * 继承RichSinkFunction<String>类，其中String为source端传到sink的数据类型，这个视Source端数据类型而定。
 */
public class RMQSink1 extends RichSinkFunction<String> {


    Connection connection=null;
    Channel channel=null;
    /**
     * open方法在sink第一次启动时调用，一般用于sink的初始化操作
     */
    @Override
    public void open(Configuration parameters) throws Exception {

        super.open(parameters);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("qh");
        factory.setPassword("qh");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setAutomaticRecoveryEnabled(true); //设置网络异常重连
        factory.setNetworkRecoveryInterval(1000);
        Address[] addresses = {new Address("192.168.3.32")};
        connection=factory.newConnection(addresses);
        channel=connection.createChannel();
    }

    /**
     * invoke方法是sink数据处理逻辑的方法，source端传来的数据都在invoke方法中进行处理
     * 其中invoke方法中第一个参数类型与RichSinkFunction<String>中的泛型对应。第二个参数
     * 为一些上下文信息
     */
    @Override
    public void invoke(String value, Context context) throws Exception {
        System.out.println("my  sink  msg ===="+value);
        String message="my custom sink msg "+value;

        //channel.basicPublish("amp.test","mytest", MessageProperties.TEXT_PLAIN,message.getBytes("utf-8"));
        Map<String, Object> delayHeaders = new HashMap<String, Object>();
//延迟时间多少毫秒，示例为10秒
        delayHeaders.put("x-delay", 10000);
//延时消息所用配置
        AMQP.BasicProperties.Builder delayProps = new AMQP.BasicProperties.Builder().headers(delayHeaders);
//设置消息持久化
        delayProps.deliveryMode(2);
        channel.basicPublish("amp.test2","mytest.te2",delayProps.build(), message.getBytes("utf-8"));
    }

    /**
     * close方法在sink结束时调用，一般用于资源的回收操作
     */
    @Override
    public void close() throws Exception {
        super.close();
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }

    }

}
