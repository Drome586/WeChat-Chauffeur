package com.example.hxds.snm;

import com.example.hxds.snm.entity.NewOrderMessage;
import com.example.hxds.snm.task.NewOrderMessageTask;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class Demo {

    @Resource
    private NewOrderMessageTask task;

    @Test
    public void sendMessageTest(){
        NewOrderMessage message = new NewOrderMessage();
        message.setUserId("9527");
        message.setFrom("大连理工大学");
        message.setTo("大连海事大学");
        message.setDistance("10");
        message.setExpectsFee("46.0");
        message.setMileage("11.6");
        message.setMinute("12.0");
        message.setFavourFee("0.0");


        ArrayList list = new ArrayList(){{
            add(message);
        }};
        task.sendNewOrderMessageAsync(list);
    }

    @Test
    public void receiveMessageTest(){
        List<NewOrderMessage> list = task.receiveNewOrderMessage(9527);
        list.forEach(one->{
            System.out.println(one.getFrom());
            System.out.println(one.getTo());
            System.out.println(one.getDistance());
        });
    }
}
