package com.example.hxds.bff.customer.feign;

import com.example.hxds.bff.customer.controller.form.*;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-odr")
public interface OdrServiceApi {

    @PostMapping("/order/insertOrder")
    public R insertOrder(InsertOrderForm form);


    @PostMapping("/order/searchOrderStatus")
    public R searchOrderStatus(SearchOrderStatusForm form);

    @PostMapping("/order/deleteUnAcceptOrder")
    public R deleteUnAcceptOrder(DeleteUnAcceptOrderForm form);

    @PostMapping("/order/hasCustomerCurrentOrder")
    public R hasCustomerCurrentOrder(HasCustomerCurrentOrderForm form);

    @PostMapping("/order/searchOrderForMoveById")
    public R searchOrderForMoveById(SearchOrderForMoveByIdForm form);

    @PostMapping("/order/confirmArriveStartPlace")
    public R confirmArriveStartPlace(ConfirmArriveStartPlaceForm form);

    @PostMapping("/order/searchOrderById")
    public R searchOrderById(SearchOrderByIdForm form);

    @PostMapping("/order/validCanPayOrder")
    public R validCanPayOrder(ValidCanPayOrderForm form);

    @PostMapping("/bill/updateBillPayment")
    public R updateBillPayment(UpdateBillPaymentForm form);

    @PostMapping("/order/updateOrderPrepayId")
    public R updateOrderPrepayId(UpdateOrderPrepayIdForm form);

    @PostMapping("/order/updateOrderAboutPayment")
    public R updateOrderAboutPayment(UpdateOrderAboutPaymentForm form);

    @PostMapping("/comment/insertComment")
    public R insertComment(InsertCommentForm form);

    @PostMapping("/order/searchCustomerOrderByPage")
    public R searchCustomerOrderByPage(SearchCustomerOrderByPageForm form);

    @PostMapping("/comment/searchCommentByOrderId")
    public R searchCommentByOrderId(SearchCommentByOrderIdForm form);
}

