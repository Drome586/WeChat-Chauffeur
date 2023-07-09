package com.example.hxds.mis.api.feign;

import com.example.hxds.common.util.R;
import com.example.hxds.mis.api.controller.form.SearchCancelRuleByIdForm;
import com.example.hxds.mis.api.controller.form.SearchChargeRuleByIdForm;
import com.example.hxds.mis.api.controller.form.SearchProfitsharingRuleByIdForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-rule")
public interface RuleServiceApi {
    @PostMapping("/charge/searchChargeRuleById")
    public R searchChargeRuleById(SearchChargeRuleByIdForm form);

    @PostMapping("/cancel/searchCancelRuleById")
    public R searchCancelRuleById(SearchCancelRuleByIdForm form);

    @PostMapping("/profitsharing/searchProfitsharingRuleById")
    public R searchProfitsharingRuleById(SearchProfitsharingRuleByIdForm form);
}

