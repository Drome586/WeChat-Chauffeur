package com.example.hxds.bff.driver.service.impl;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.example.hxds.bff.driver.controller.form.StartCommentWorkflowForm;
import com.example.hxds.bff.driver.feign.WorkflowServiceApi;
import com.example.hxds.bff.driver.service.OrderCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class OrderCommentServiceImpl implements OrderCommentService {

    @Resource
    private WorkflowServiceApi workflowServiceApi;

    @Override
    @LcnTransaction
    @Transactional
    public void startCommentWorkflow(StartCommentWorkflowForm form) {
        workflowServiceApi.startCommentWorkflow(form);
    }
}
