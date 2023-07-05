package com.example.hxds.bff.driver.feign;

import com.example.hxds.bff.driver.controller.form.*;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-odr")
public interface OdrServiceApi {

    @PostMapping("/order/searchDriverTodayBusinessData")
    public R searchDriverTodayBusinessData(SearchDriverTodayBusinessDataForm form);

    @PostMapping("/order/acceptNewOrder")
    public R acceptNewOrder(AcceptNewOrderForm form);

    @PostMapping("/order/searchDriverExecuteOrder")
    public R searchDriverExecuteOrder(SearchDriverExecuteOrderForm form);

    @PostMapping("/order/searchDriverCurrentOrder")
    public R searchDriverCurrentOrder(SearchDriverCurrentOrderForm form);

    @PostMapping("/order/searchOrderForMoveById")
    public R searchOrderForMoveById(SearchOrderForMoveByIdForm form);

    @PostMapping("/order/arriveStartPlace")
    public R arriveStartPlace(ArriveStartPlaceForm form);

    @PostMapping("/order/startDriving")
    public R startDriving(StartDrivingForm form);

    @PostMapping("/order/updateOrderStatus")
    public R updateOrderStatus(UpdateOrderStatusForm form);
}
