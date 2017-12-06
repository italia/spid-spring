package it.italia.developers.spid.integration.exception;

/**
 * @author Gianluca Pindinelli
 *
 */
public class IntegrationServiceException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -2318767908443056286L;

	public IntegrationServiceException() {
		super();
	}

	public IntegrationServiceException(String message) {
		super(message);
	}

	public IntegrationServiceException(Throwable t) {
		super(t);
	}

	public IntegrationServiceException(String message, Throwable t) {
		super(message, t);
	}

}
