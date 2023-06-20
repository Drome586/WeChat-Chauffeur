package com.example.hxds.snm.task;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.snm.entity.NewOrderMessage;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class NewOrderMessageTask {

    @Resource
    private ConnectionFactory factory;

    /*
    同步发送订单消息
     */

    public void sendNewOrderMessage(ArrayList<NewOrderMessage> list){
        int ttl = 10 * 60 * 1000;     //新消息订单缓存过期时间为1分钟
        String exchangeName = "new_order_private";  //交换机名字

        try(
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        ){
            //定义交换机，根据routing key去路由,设置交换机名字跟路由模式
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            HashMap param = new HashMap();
            for(NewOrderMessage message:list){
                //MQ消息的属性
                HashMap map = new HashMap();
                map.put("orderId",message.getOrderId());
                map.put("from",message.getFrom());
                map.put("to",message.getTo());
                map.put("expectsFee",message.getExpectsFee());
                map.put("mileage",message.getMileage());
                map.put("minute",message.getMinute());
                map.put("distance",message.getDistance());
                map.put("favourFee",message.getFavourFee());

                //创建消息属性对象
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().contentEncoding("UTF-8")
                        .headers(map).expiration(ttl + "").build();
                String queueName = "queue_" + message.getUserId();  //队列名字
                String routingKey = message.getUserId();       //routing Key

                //声明队列（持久化缓存消息。 消息接受不加锁，消息完全接受完。    并不删除队列）
                channel.queueDeclare(queueName,true,false,false,param);
                channel.queueBind(queueName,exchangeName,routingKey);

                //向交换机发送消息，并附带routingKey
                channel.basicPublish(exchangeName,routingKey,properties,("新订单" + message.getOrderId()).getBytes());
                //log.debug(message.getUserId() + "新订单消息发送成功");
                log.info(message.getUserId() + "新订单消息发送成功");

            }

        }catch (Exception e){
            log.error("执行异常",e);
            throw new HxdsException("新订单消息发送失败");
        }
    }

    /*
    异步发送消息,单独找一个空闲的线程，去执行发送消息
     */
    @Async
    public void sendNewOrderMessageAsync(ArrayList<NewOrderMessage> list){
        sendNewOrderMessage(list);
    }


    /*
    同步接收消息
     */

    public List<NewOrderMessage> receiveNewOrderMessage(long userId){
        String exchangeName = "new_order_private";  //交换机名字
        String queueName = "queue_" + userId;   //队列名字
        String routingKey = userId +""; //routingKey

        List<NewOrderMessage> list = new ArrayList();

        try(
            Connection connection = factory.newConnection();
            Channel privateChannel = connection.createChannel();
        ){
            //定义交换机，routing key模式
            privateChannel.exchangeDeclare(exchangeName,BuiltinExchangeType.DIRECT);
            //声明队列
            privateChannel.queueDeclare(queueName,true,false,false,null);
            //绑定要接受的队列
            privateChannel.queueBind(queueName,exchangeName,routingKey);
            //为了避免一次性接受太多的消息，我们采用限流的方式，每次接受10条消息，然后循环接受
            privateChannel.basicQos(0,10,true);

            while(true){
                GetResponse response = privateChannel.basicGet(queueName, false);
                if(response != null){
                    //消息对象属性
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> map = properties.getHeaders();
                    String orderId = MapUtil.getStr(map, "orderId");
                    String from = MapUtil.getStr(map, "from");
                    String to = MapUtil.getStr(map, "to");
                    String expectsFee = MapUtil.getStr(map, "expectsFee");
                    String mileage = MapUtil.getStr(map, "mileage");
                    String minute = MapUtil.getStr(map, "minute");
                    String distance = MapUtil.getStr(map, "distance");
                    String favourFee = MapUtil.getStr(map, "favourFee");

                    //把新订单封装到对象中去
                    NewOrderMessage message = new NewOrderMessage();
                    message.setOrderId(orderId);
                    message.setFrom(from);
                    message.setTo(to);
                    message.setExpectsFee(expectsFee);
                    message.setMileage(mileage);
                    message.setMinute(minute);
                    message.setDistance(distance);
                    message.setFavourFee(favourFee);

                    list.add(message);

                    byte[] body = response.getBody();
                    String msg = new String(body);
                    //log.debug("从RabbitMQ接收的订单消息：" + msg);

                    //确定收到消息，让MQ删除该消息
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    privateChannel.basicAck(deliveryTag,false);
                }else{
                    break;
                }
            }
            //消息倒叙，新消息排在前面
            ListUtil.reverse(list);
            return list;
        }catch(Exception e) {
            log.error("执行异常",e);
            throw new HxdsException("接受新订单失败");
        }
    }

    /*
    同步删除消息队列？适用于用户注销的时候，进行消息队列的删除
     */

    public void deleteNewOrderQueue(long userId){
        String exchangeName = "new_order_private";  //交换机名字
        String queueName = "queue_" + userId;   //队列名字
        try(
            Connection connection = factory.newConnection();
            Channel privateChannel = connection.createChannel();
        ){
            //定义交换机
            privateChannel.exchangeDeclare(exchangeName,BuiltinExchangeType.DIRECT);
            //删除队列
            privateChannel.queueDelete(queueName);
            log.debug(userId + "新订单消息队列删除成功");

        }catch(Exception e){
            log.error(userId + "的订单队列删除失败",e);
            throw new HxdsException("新订单队列删除失败");
        }
    }

    /*
    异步删除订单消息队列
     */
    @Async
    public void deleteNewOrderQueueAsync(long userId){
        deleteNewOrderQueue(userId);
    }

    /*
    同步清空新订单消息队列？适用于当用户退出登录的时候，清空一下消息队列中的内容
     */
    public void clearNewOrderQueue(long userId){
        String exchangeName = "new_order_private";
        String queueName = "queue_" + userId;
        try(
            Connection connection = factory.newConnection();
            Channel privateChannel = connection.createChannel();
        ){
            privateChannel.exchangeDeclare(exchangeName,BuiltinExchangeType.DIRECT);
            privateChannel.queuePurge(queueName);

            log.debug(userId + "的新订单消息队列已经清空");

        }catch (Exception e){
            log.debug(userId + "的新订单消息队列未被清空");
            throw new HxdsException("新订单消息队列清空失败");
        }
    }

    /*
    异步清空消息队列
     */
    public void clearNewOrderQueueAsync(long userId){
        clearNewOrderQueue(userId);
    }

}
