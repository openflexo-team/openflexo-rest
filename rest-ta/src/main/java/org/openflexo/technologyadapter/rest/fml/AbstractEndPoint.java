package org.openflexo.technologyadapter.rest.fml;

import java.util.Optional;

import org.openflexo.foundation.fml.md.MetaDataKeyValue;
import org.openflexo.foundation.fml.md.MultiValuedMetaData;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.secrets.SecretsService;
import org.openflexo.technologyadapter.rest.model.Header;
import org.openflexo.technologyadapter.rest.model.RestAccessPoint;
import org.openflexo.toolbox.StringUtils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public abstract class AbstractEndPoint {

	private final RestEntity restEntity;
	private final String url;
	private final String protocol;

	AbstractEndPoint(RestEntity restEntity, MultiValuedMetaData md) {
		this.restEntity = restEntity;
		MetaDataKeyValue<String> urlValue = md.getKeyValue(RestEntity.URL);
		url = urlValue != null ? urlValue.getValue(String.class) : null;
		MetaDataKeyValue<String> protocolValue = md.getKeyValue(RestEntity.PROTOCOL);
		protocol = protocolValue != null ? protocolValue.getValue(String.class) : null;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}

	/**
	 * Build the REST-assured request specification for <code>accessPoint</code>: base URL, declared headers, and the Authorization
	 * header resolved from the access point's secret reference (or its literal {@code authorization} value as a fallback).
	 *
	 * @throws FMLExecutionException
	 *             if <code>accessPoint</code> references a secret (via {@link RestAccessPoint#getSecretRef()}) that cannot be resolved
	 */
	protected RequestSpecification buildRequestSpecification(RestAccessPoint accessPoint) throws FMLExecutionException {
		RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder().setBaseUri(accessPoint.getBaseURL());
		String authorizationHeader = resolveAuthorizationHeader(accessPoint);
		for (Header header : accessPoint.getHeaders()) {
			// A secretRef/authorization-resolved value takes precedence over any literal "Authorization" header declared on the access
			// point, so that configuring a secret always wins over a stale or accidentally-committed literal value.
			if (authorizationHeader != null && "Authorization".equalsIgnoreCase(header.getKey())) {
				continue;
			}
			requestSpecBuilder.addHeader(header.getKey(), header.getValue());
		}
		if (authorizationHeader != null) {
			requestSpecBuilder.addHeader("Authorization", authorizationHeader);
		}
		return requestSpecBuilder.build();
	}

	/**
	 * Resolve the Authorization header value for <code>accessPoint</code>.
	 *
	 * @throws FMLExecutionException
	 *             if a {@code secretRef} is set on <code>accessPoint</code> but the referenced secret cannot be found (neither in the
	 *             {@link SecretsService} local store, nor as an environment variable or system property override): silently sending
	 *             the request without the intended credentials would only surface as a confusing technical error further down (e.g. an
	 *             HTTP 401 from the remote API), so we fail fast with a clear message instead.
	 */
	private String resolveAuthorizationHeader(RestAccessPoint accessPoint) throws FMLExecutionException {
		if (StringUtils.isNotEmpty(accessPoint.getSecretRef())) {
			SecretsService secretsService = accessPoint.getResource().getServiceManager().getService(SecretsService.class);
			Optional<String> secret = secretsService.getSecret(accessPoint.getSecretRef());
			if (secret.isPresent()) {
				return "Bearer " + secret.get();
			}
			throw new BadCredentialsException("Secret '" + accessPoint.getSecretRef() + "' referenced by access point '"
					+ accessPoint.getName() + "' could not be resolved: configure it in Preferences > Secrets, or supply it as the "
					+ "OPENFLEXO_SECRET_" + accessPoint.getSecretRef().toUpperCase().replaceAll("[^A-Z0-9]", "_")
					+ " environment variable");
		}
		if (StringUtils.isNotEmpty(accessPoint.getAuthorization())) {
			return accessPoint.getAuthorization();
		}
		return null;
	}
}
