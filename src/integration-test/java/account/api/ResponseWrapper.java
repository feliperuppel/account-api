package account.api;

public class ResponseWrapper {

    public ResponseWrapper() {

    }

    public ResponseWrapper(Integer statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public Integer statusCode;
    public String body;
}
