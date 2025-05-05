package skyst.dopamine.domain.member;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import skyst.dopamine.exception.ErrorCode;

@Getter
public class MemberBaseException extends AuthenticationException {

    private final ErrorCode errorCode;

    public MemberBaseException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
