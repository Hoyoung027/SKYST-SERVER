package skyst.dopamine.domain.member.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skyst.dopamine.domain.member.api.dto.MemberDetailRes;
import skyst.dopamine.domain.member.api.dto.MemberNameRes;
import skyst.dopamine.domain.member.api.dto.MemberReq;
import skyst.dopamine.domain.member.api.service.MemberService;


@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<String> saveInfo(@RequestBody MemberReq memberReq, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");
        memberService.saveInfo(accessToken, memberReq);

        return ResponseEntity.ok("Member information update sucess");
    }

    @GetMapping("/user/register/success")
    public ResponseEntity<MemberNameRes> getUserName(@RequestParam (value = "userId") Long userId, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        MemberNameRes memberNameRes = memberService.getMemberName(accessToken, userId);
        return ResponseEntity.ok(memberNameRes);
    }

    @GetMapping("/user/mypage")
    public ResponseEntity<MemberDetailRes> getMemeberById(@RequestParam (value = "userId") Long userId, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");
        MemberDetailRes memberDetailRes = memberService.getMember(accessToken, userId);
        return ResponseEntity.ok(memberDetailRes);
    }

}
