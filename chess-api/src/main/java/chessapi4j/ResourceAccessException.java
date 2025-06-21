package chessapi4j;

class ResourceAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceAccessException(String resource, Throwable cause) {
        super(String.format("Can not access resource %s. Error message: %s", resource, cause.getMessage()), cause);
    }
}
