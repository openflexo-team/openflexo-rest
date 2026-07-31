package org.openflexo.technologyadapter.rest.fml;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Expression;
import org.openflexo.foundation.fml.FMLUtils;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.editionaction.FetchRequestCondition;
import org.openflexo.foundation.fml.md.MultiValuedMetaData;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.technologyadapter.rest.model.RestAccessPoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GetEndPoint extends AbstractEndPoint {

	GetEndPoint(RestEntity restEntity, MultiValuedMetaData md) {
		super(restEntity, md);
	}

	/**
	 * Perform the {@code @Get} REST call for <code>conceptType</code> and build the resulting {@link RestFlexoConceptInstance}.
	 * <p>
	 * Only single-object {@code @Get} endpoints identified by exactly one equality condition are supported (e.g.
	 * {@code select unique User from github where (selected.login=="...")}, matching a URL template such as
	 * {@code /users/{login}}) : conditions are turned into REST-assured path parameters via {@link FMLUtils#getIndexableTerm}/
	 * {@link FMLUtils#getOppositeTerm}. Listing/searching several instances is not implemented (that's the {@code @Search} endpoint,
	 * a separate, still-stubbed code path).
	 */
	public List<RestFlexoConceptInstance> selectFlexoConceptInstances(RestVirtualModelInstance owner, FlexoConcept conceptType,
			RestAccessPoint accessPoint, List<FetchRequestCondition> conditions, RunTimeEvaluationContext evaluationContext)
			throws FMLExecutionException {

		Map<String, Object> pathParams = new HashMap<>();
		for (FetchRequestCondition condition : conditions) {
			if (!FMLUtils.isIndexableCondition(condition)) {
				throw new FMLExecutionException(
						"Condition '" + condition.getCondition() + "' is not a simple equality on a property of the selected object, "
								+ "which is required to build the REST call for '" + getUrl() + "'");
			}
			Expression indexableTerm = FMLUtils.getIndexableTerm(condition);
			Expression oppositeTerm = FMLUtils.getOppositeTerm(condition);
			String propertyName = indexableTerm.toString().replaceFirst("^selected\\.", "");
			DataBinding<?> valueBinding = new DataBinding<>(oppositeTerm.toString(), condition, Object.class,
					BindingDefinitionType.GET);
			try {
				pathParams.put(propertyName, valueBinding.getBindingValue(evaluationContext));
			} catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
				throw new FMLExecutionException("Could not evaluate condition '" + condition.getCondition() + "'", e);
			}
		}

		RequestSpecification requestSpecification = buildRequestSpecification(accessPoint);
		Response response = given(requestSpecification).pathParams(pathParams).when().get(getUrl());

		if (response.getStatusCode() == 401) {
			throw new BadCredentialsException("REST call to '" + accessPoint.getBaseURL() + getUrl() + "' (access point '"
					+ accessPoint.getName() + "') was rejected as unauthenticated (HTTP 401): " + response.getBody().asString());
		}
		if (response.getStatusCode() == 404) {
			// No matching resource: a valid, empty result for a "select" - not a technical failure.
			return Collections.emptyList();
		}
		if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
			throw new FMLExecutionException("REST call to '" + accessPoint.getBaseURL() + getUrl() + "' (access point '"
					+ accessPoint.getName() + "') failed with HTTP status " + response.getStatusCode() + ": "
					+ response.getBody().asString());
		}

		ObjectNode source;
		try {
			JsonNode node = new ObjectMapper().readTree(response.getBody().asString());
			if (!(node instanceof ObjectNode)) {
				throw new FMLExecutionException(
						"REST call to '" + accessPoint.getBaseURL() + getUrl() + "' did not return a JSON object: " + node);
			}
			source = (ObjectNode) node;
		} catch (IOException e) {
			throw new FMLExecutionException("Could not parse JSON response from '" + accessPoint.getBaseURL() + getUrl() + "'", e);
		}

		String identifier = pathParams.isEmpty() ? getUrl() : String.valueOf(pathParams.values().iterator().next());
		JsonSupport support = new JsonSupport(owner, identifier, source, null);

		RestFlexoConceptInstance fci = owner.getFactory().newInstance(RestFlexoConceptInstance.class, owner, support, conceptType);
		// getFactory() on a VirtualModelInstanceObject normally derives the factory from its owning VMI's FMLRTVirtualModelInstanceResource,
		// which doesn't apply here (a RestVirtualModelInstance is backed by a RestAccessPointResource) - it falls back to localFactory,
		// which must be set explicitly (same pattern as FlexoConceptInstanceImpl/VirtualModelInstanceImpl cloning).
		fci.setLocalFactory(owner.getFactory());
		if (!pathParams.isEmpty()) {
			// RestFlexoConceptInstance.initializeIdentifiers() only supports a single-property key for now.
			fci.initializeIdentifiers(String.valueOf(pathParams.values().iterator().next()));
		}
		owner.addToFlexoConceptInstances(fci);

		return Collections.singletonList(fci);
	}
}
