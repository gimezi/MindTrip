package com.a303.postitms.global.client;

import com.a303.postitms.global.api.response.BaseResponse;
import com.a303.postitms.global.client.dto.response.MemberBaseRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="memberms")
public interface MemberClient {

    @GetMapping("api/members/v1/{memberId}")
    BaseResponse<MemberBaseRes> getMember(@PathVariable int memberId);

}