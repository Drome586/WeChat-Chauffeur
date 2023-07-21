package com.example.hxds.mis.api.feign;

import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchCommentByPageForm;
import com.example.hxds.mis.api.controller.form.SearchOrderByPageForm;
import com.example.hxds.mis.api.controller.form.SearchOrderContentForm;
import com.example.hxds.mis.api.controller.form.SearchOrderStatusForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-odr")
public interface OdrServiceApi {

    @PostMapping("/order/searchOrderByPage")
    public R searchOrderByPage(SearchOrderByPageForm form);

    @PostMapping("/order/searchOrderContent")
    public R searchOrderContent(SearchOrderContentForm form);

    @PostMapping("/order/searchOrderStatus")
    public R searchOrderStatus(SearchOrderStatusForm form);

    @PostMapping("/order/searchOrderStartLocationIn30Days")
    public R searchOrderStartLocationIn30Days();

    @PostMapping("/comment/searchCommentByPage")
    public R searchCommentByPage(SearchCommentByPageForm form);
}

