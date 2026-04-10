package org.openflexo.technologyadapter.rest.fml;

import org.openflexo.foundation.fml.rt.FMLExecutionException;

/**
 * Thrown when a REST call cannot be authenticated: either a {@code secretRef} could not be resolved to any value, or the remote API
 * rejected the request with an HTTP 401 (the credential it received was invalid, e.g. a revoked token).
 */
@SuppressWarnings("serial")
public class BadCredentialsException extends FMLExecutionException {

	public BadCredentialsException(String message) {
		super(message);
	}

	public BadCredentialsException(Exception exception) {
		super(exception);
	}

}
