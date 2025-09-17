package com.rest1.global.rq;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.service.MemberService;
import com.rest1.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Rq {

    private final MemberService memberService;
    private final HttpServletRequest request;

    public Member getActor() {

        String authorization = request.getHeader("Authorization");

        if(authorization == null || authorization.isEmpty()) {
            throw new ServiceException("401-1", "헤더에 인증 정보가 없습니다.");
        }

        if(!authorization.startsWith("Bearer ")) {
            throw new ServiceException("401-2", "헤더의 인증 정보 형식이 올바르지 않습니다.");
        }

        Member actor = memberService.findByApiKey(authorization.replace("Bearer ", ""))
                .orElseThrow(() -> new ServiceException("401-3", "API 키가 올바르지 않습니다."));


        return actor;
    }

}
