package org.openflexo.technologyadapter.rest.fml;

import java.util.HashMap;
import java.util.Map;

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.md.MultiValuedMetaData;

public class RestEntity {

	private static final String REST_ENTITY = "RestEntity";
	private static final String GET = "Get";
	private static final String SET = "Set";
	private static final String SEARCH = "Search";
	static final String URL = "url";
	static final String PROTOCOL = "protocol";

	private static Map<FlexoConcept, RestEntity> restEntities = new HashMap<>();

	public static RestEntity getRestEntity(FlexoConcept flexoConcept) {
		RestEntity returned = restEntities.get(flexoConcept);
		if (returned == null) {
			if (flexoConcept.hasMetaData(REST_ENTITY)) {
				returned = new RestEntity(flexoConcept);
				restEntities.put(flexoConcept, returned);
			}
		}
		return returned;
	}

	private final FlexoConcept flexoConcept;

	private GetEndPoint getEndPoint = null;
	private SetEndPoint setEndPoint = null;
	private SearchEndPoint searchEndPoint = null;

	private RestEntity(FlexoConcept flexoConcept) {
		this.flexoConcept = flexoConcept;
		if (flexoConcept.hasMetaData(GET) && flexoConcept.getMetaData(GET) instanceof MultiValuedMetaData) {
			getEndPoint = new GetEndPoint(this, (MultiValuedMetaData) flexoConcept.getMetaData(GET));
		}
		if (flexoConcept.hasMetaData(SET) && flexoConcept.getMetaData(SET) instanceof MultiValuedMetaData) {
			setEndPoint = new SetEndPoint(this, (MultiValuedMetaData) flexoConcept.getMetaData(SET));
		}
		if (flexoConcept.hasMetaData(SEARCH) && flexoConcept.getMetaData(SEARCH) instanceof MultiValuedMetaData) {
			searchEndPoint = new SearchEndPoint(this, (MultiValuedMetaData) flexoConcept.getMetaData(SEARCH));
		}
	}

	public GetEndPoint getGetEndPoint() {
		return getEndPoint;
	}
}
