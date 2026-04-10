package org.openflexo.technologyadapter.rest.model;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.rest.RestTechnologyAdapter;
import org.openflexo.technologyadapter.rest.model.RestAccessPoint.AccessPointImpl;
import org.openflexo.technologyadapter.rest.rm.RestAccessPointResource;

/**
 * Configuration of an access point to a REST API
 */
@ModelEntity
@ImplementationClass(AccessPointImpl.class)
public interface RestAccessPoint extends TechnologyObject<RestTechnologyAdapter>, ResourceData<RestAccessPoint> {

	/*
	 * name : nom de l’API
	 * base_url : ex: https://api.example.com
	 * version : ex: v1, v2
	 * description : optionnel mais utile
	 * authentication :
	 * - type (none, apiKey, oauth2, jwt)
	 * - config associée (header, token, etc.)
	 * headers globaux :
	 * ex: Content-Type, Authorization
	 * rate_limit (si pertinent)
	 * timeout / retry policy
	 */

	String NAME_KEY = "name";
	String BASE_URL_KEY = "baseURL";
	String VERSION_KEY = "version";
	String DESCRIPTION_KEY = "description";
	String AUTHENTICATION_KEY = "authentication";
	String CONTENT_TYPE_KEY = "contentType";
	String AUTHORIZATION_KEY = "authorization";
	String SECRET_REF_KEY = "secretRef";
	String HEADERS_KEY = "headers";

	@Getter(NAME_KEY)
	String getName();

	@Setter(NAME_KEY)
	void setName(String name);

	@Getter(BASE_URL_KEY)
	String getBaseURL();

	@Setter(BASE_URL_KEY)
	void setBaseURL(String basaeURL);

	@Getter(VERSION_KEY)
	String getVersion();

	@Setter(VERSION_KEY)
	void setVersion(String version);

	@Getter(DESCRIPTION_KEY)
	String getDescription();

	@Setter(DESCRIPTION_KEY)
	void setDescription(String description);

	@Getter(AUTHENTICATION_KEY)
	String getAuthentication();

	@Setter(AUTHENTICATION_KEY)
	void setAuthentication(String authentication);

	@Getter(CONTENT_TYPE_KEY)
	String getContentType();

	@Setter(CONTENT_TYPE_KEY)
	void setContentType(String contentType);

	@Getter(AUTHORIZATION_KEY)
	String getAuthorization();

	@Setter(AUTHORIZATION_KEY)
	void setAuthorization(String authorization);

	/**
	 * Name of the secret (managed by the SecretsService) to resolve as a Bearer token for this access point, at runtime. Takes
	 * precedence over {@link #getAuthorization()} when set. This must be a reference to a secret name, never a literal token: real
	 * credentials must never be stored in this project resource.
	 */
	@Getter(SECRET_REF_KEY)
	String getSecretRef();

	@Setter(SECRET_REF_KEY)
	void setSecretRef(String secretRef);

	@Getter(value = HEADERS_KEY, cardinality = Cardinality.LIST)
	List<Header> getHeaders();

	@Adder(HEADERS_KEY)
	void addToHeaders(Header header);

	@Remover(HEADERS_KEY)
	void removeFromHeaders(Header header);

	@Override
	RestAccessPointResource getResource();

	abstract class AccessPointImpl extends FlexoObjectImpl implements RestAccessPoint {

		private static final Logger logger = Logger.getLogger(RestAccessPoint.class.getPackage().getName());

		@Override
		public String toString() {
			return "RestAccessPoint " + getName() + " / " + getDescription() + " headers=" + getHeaders();
		}

	}
}
