package truonggg.exception;

import lombok.Getter;
import lombok.Setter;
import truonggg.response.ErrorCode;

@Getter
@Setter
public class UserAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final String domain;
    private final String message;

    public UserAlreadyExistException(ErrorCode errorCode, String domain, String message) {
        super(message);
        this.errorCode = errorCode;
        this.domain = domain;
        this.message = message;
    }
}
