package org.openflexo.technologyadapter.rest.fml;

import org.openflexo.foundation.fml.rt.error.FMLTechnologyError;

/**
 * Thrown when a REST call cannot be authenticated: either a {@code secretRef} could not be resolved to any value, or the remote API
 * rejected the request with an HTTP 401 (the credential it received was invalid, e.g. a revoked token). A foreseen failure of the REST
 * technology, typed for FML (CORE-F-11 phase 6).
 */
@SuppressWarnings("serial")
public class BadCredentialsException extends FMLTechnologyError {

	public BadCredentialsException(String message) {
		super(message);
	}

	public BadCredentialsException(Exception exception) {
		super(exception.getMessage() != null ? exception.getMessage() : exception.toString(), exception);
	}

}
