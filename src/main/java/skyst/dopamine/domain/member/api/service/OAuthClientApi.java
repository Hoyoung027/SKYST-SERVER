package skyst.dopamine.domain.member.api.service;

public interface OAuthClientApi {
    Object getAccessToken(String redirectUri, String code);
    Object getUserInfo(String token);
    Object unlink(Long userId);
}
