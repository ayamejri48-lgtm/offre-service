
package com.recrutement.offreservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import com.recrutement.offreservice.dto.CvResponse;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/me/cvs")
    List<CvResponse> getMyCvs(
            @RequestHeader("Authorization") String authorization
    );
}
