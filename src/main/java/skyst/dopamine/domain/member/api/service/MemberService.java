package skyst.dopamine.domain.member.api.service;

import org.springframework.stereotype.Service;
import skyst.dopamine.domain.member.api.dto.*;
import skyst.dopamine.domain.member.core.Member;
import skyst.dopamine.domain.member.core.MemberRepository;
import skyst.dopamine.domain.member.core.exception.MemberCoreException;
import skyst.dopamine.exception.ErrorCode;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public LoginRes findOrCreateUser(MemberRes memberInfo) {

        // kakao user id 기반으로 서비스 가입 여부 판단 후 유저 조회 or 생성
        return memberRepository.findByKakaoUserId(memberInfo.userId())
                .map(existingMember -> new LoginRes(existingMember.getId(), existingMember.getNickname()))
                .orElseGet(() -> {
                    Member newMember = new Member(
                            memberInfo.userId(),
                            memberInfo.nickname(),
                            memberInfo.email()
                    );
                    memberRepository.save(newMember);
                    return new LoginRes(newMember.getId(), newMember.getNickname());
                });
    }

    public void saveInfo(String accessToken, MemberReq memberReq){

        Member member = memberRepository.findById(memberReq.userId())
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        member.setNickname(member.getNickname());
        member.setEmail(member.getEmail());

        memberRepository.save(member);
    }

    public MemberDetailRes getMember(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        MemberDetailRes memberDetailRes = new MemberDetailRes(
                member.getId(),
                member.getNickname(),
                member.getEmail()
        );

        return memberDetailRes;
    }

    public MemberNameRes getMemberName(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        MemberNameRes memberNameRes = new MemberNameRes(member.getNickname());
        return memberNameRes;
    }

    public Long getUserKakaoId(Long userId){
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));
        return member.getKakaoUserId();
    }

    public void deleteMemberById(Long userId){
        if (!memberRepository.existsById(userId)) {
            throw new MemberCoreException(ErrorCode.NOT_FOUND_USER);
        }
        memberRepository.deleteById(userId);
    }
}
