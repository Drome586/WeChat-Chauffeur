package com.example.hxds.mis.api.service;

import com.example.hxds.common.util.PageUtils;
import com.example.hxds.mis.api.controller.form.AcceptCommentAppealForm;
import com.example.hxds.mis.api.controller.form.HandleCommentAppealForm;
import com.example.hxds.mis.api.controller.form.SearchCommentByPageForm;

public interface OrderCommentService {
    public PageUtils searchCommentByPage(SearchCommentByPageForm form);

    public void acceptCommentAppeal(AcceptCommentAppealForm form);

    public void handleCommentAppeal(HandleCommentAppealForm form);
}
