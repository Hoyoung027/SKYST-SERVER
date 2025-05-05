package skyst.dopamine.domain.member.api.dto;

public record DeleteSearchReq(
        Long userId,
        Long searchId
) {
}
