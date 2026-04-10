package org.openflexo.technologyadapter.rest.model;

import java.util.logging.Logger;

import org.openflexo.foundation.InnerResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.rest.RestTechnologyAdapter;
import org.openflexo.technologyadapter.rest.model.Header.HeaderImpl;

/**
 * Header of an access point of a REST API
 */
@ModelEntity
@ImplementationClass(HeaderImpl.class)
public interface Header extends TechnologyObject<RestTechnologyAdapter>, InnerResourceData<RestAccessPoint> {

	String KEY_KEY = "key";
	String VALUE_KEY = "value";

	@Getter(KEY_KEY)
	String getKey();

	@Setter(KEY_KEY)
	void setKey(String name);

	@Getter(VALUE_KEY)
	String getValue();

	@Setter(VALUE_KEY)
	void setValue(String baseURL);

	abstract class HeaderImpl extends FlexoObjectImpl implements Header {

		private static final Logger logger = Logger.getLogger(Header.class.getPackage().getName());

		@Override
		public String toString() {
			return "Header(" + getKey() + ":" + getValue() + ")";
		}

	}
}
