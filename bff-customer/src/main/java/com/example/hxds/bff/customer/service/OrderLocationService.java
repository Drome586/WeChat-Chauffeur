package com.example.hxds.bff.customer.service;

import com.example.hxds.bff.customer.controller.form.SearchOrderLocationCacheForm;

import java.util.HashMap;

public interface OrderLocationService {

    public HashMap searchOrderLocationCache(SearchOrderLocationCacheForm form);
}
