package skyst.dopamine.domain.member.core.exception;

import skyst.dopamine.domain.member.MemberBaseException;
import skyst.dopamine.exception.ErrorCode;

public class MemberCoreException extends MemberBaseException {
    public MemberCoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}



