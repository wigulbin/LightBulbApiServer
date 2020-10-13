import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper extends Throwable
        implements javax.ws.rs.ext.ExceptionMapper<Throwable> {

    private static final long serialVersionUID = 1L;

    @Override
    public Response toResponse(Throwable e) {
        return Response.status(500).entity(e.getMessage())
                .type("text/plain").build();
    }
}
