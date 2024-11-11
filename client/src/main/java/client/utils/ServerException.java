package client.utils;

public class ServerException extends Exception {
    private ErrorCode errorCode;
    private String message;

    public ServerException() {

    }

    public ServerException(ErrorCode errorCode, String message) {
        super();
        this.errorCode = errorCode;
        this.message = message;
    }

    public ServerException(int code, String message) {
        super();
        this.errorCode = ErrorCode.getErrorCode(code);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public enum ErrorCode {
        NOT_FOUND(404),
        METHOD_NOT_ALLOWED(405),
        INTERNAL_ERROR(500);

        private int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public static ErrorCode getErrorCode(int code) {
            for (ErrorCode e : ErrorCode.values()) {
                if (e.code == code) {
                    return e;
                }
            }
            return null;
        }
    }
}
