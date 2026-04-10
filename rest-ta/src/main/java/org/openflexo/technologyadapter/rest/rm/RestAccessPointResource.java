/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.rest.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.PamelaPropertiesSerializableResource;
import org.openflexo.foundation.resource.PamelaPropertiesSerializableResourceImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.rest.RestTechnologyAdapter;
import org.openflexo.technologyadapter.rest.RestTechnologyContextManager;
import org.openflexo.technologyadapter.rest.model.Header;
import org.openflexo.technologyadapter.rest.model.RestAccessPoint;
import org.openflexo.technologyadapter.rest.model.RestAccessPointModelFactory;
import org.openflexo.technologyadapter.rest.rm.RestAccessPointResource.RestAccessPointResourceImpl;

@ModelEntity
@XMLElement
@ImplementationClass(RestAccessPointResourceImpl.class)
public interface RestAccessPointResource extends PamelaPropertiesSerializableResource<RestAccessPoint, RestAccessPointModelFactory>,
		TechnologyAdapterResource<RestAccessPoint, RestTechnologyAdapter> {

	String TECHNOLOGY_CONTEXT_MANAGER = "technologyContextManager";

	@Override
	@Getter(value = TECHNOLOGY_CONTEXT_MANAGER, ignoreType = true)
	RestTechnologyContextManager getTechnologyContextManager();

	@Setter(TECHNOLOGY_CONTEXT_MANAGER)
	void setTechnologyContextManager(RestTechnologyContextManager contextManager);

	// TODO connect to model
	@Getter("model")
	RestAccessPoint getModel();

	abstract class RestAccessPointResourceImpl extends
			PamelaPropertiesSerializableResourceImpl<RestAccessPoint, RestAccessPointModelFactory> implements RestAccessPointResource {

		private static final Logger logger = FlexoLogger.getLogger(RestAccessPointResourceImpl.class.getPackage().toString());

		@Override
		public RestTechnologyAdapter getTechnologyAdapter() {
			if (getServiceManager() != null) {
				return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(RestTechnologyAdapter.class);
			}
			return null;
		}

		@Override
		public Class<RestAccessPoint> getResourceDataClass() {
			return RestAccessPoint.class;
		}

		@Override
		public FlexoObject findObject(String objectIdentifier, String userIdentifier, String typeIdentifier) {
			logger.warning("Don't know how to find object for RestAccessPoint");
			return null;
		}

		@Override
		public FlexoObject findObject(String objectIdentifier, String userIdentifier) {

			logger.warning("Don't know how to find object for RestAccessPoint");
			return null;
		}

		@Override
		protected void unhandledProperty(String key, String value, RestAccessPoint resourceData,
				org.openflexo.pamela.model.ModelEntity<RestAccessPoint> modelEntity) {
			if (key.startsWith("header.")) {
				Header header = getFactory().makeHeader(key.substring(7), value);
				resourceData.addToHeaders(header);
			}
		}
	}

}
