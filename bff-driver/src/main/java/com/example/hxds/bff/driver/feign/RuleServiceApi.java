package com.example.hxds.bff.driver.feign;

import com.example.hxds.bff.driver.controller.form.CalculateIncentiveFeeForm;
import com.example.hxds.bff.driver.controller.form.CalculateOrderChargeForm;
import com.example.hxds.bff.driver.controller.form.CalculateProfitsharingForm;
import com.example.hxds.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "hxds-rule")
public interface RuleServiceApi {

    @PostMapping("/charge/calculateOrderCharge")
    public R calculateOrderCharge(CalculateOrderChargeForm form);

    @PostMapping("/award/calculateIncentiveFee")
    public R calculateIncentiveFee(CalculateIncentiveFeeForm form);

    @PostMapping("/profitsharing/calculateProfitsharing")
    public R calculateProfitsharing(CalculateProfitsharingForm form);
}

