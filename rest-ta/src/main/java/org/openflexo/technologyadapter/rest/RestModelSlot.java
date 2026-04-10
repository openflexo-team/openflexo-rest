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

package org.openflexo.technologyadapter.rest;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFMLRTModelSlot;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFMLRTModelSlotInstance;
import org.openflexo.foundation.resource.StreamIODelegate;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.rest.RestModelSlot.RestModelSlotImpl;
import org.openflexo.technologyadapter.rest.fml.RestVirtualModelInstance;
import org.openflexo.technologyadapter.rest.fml.RestVirtualModelInstanceModelFactory;
import org.openflexo.technologyadapter.rest.model.RestAccessPoint;
import org.openflexo.technologyadapter.rest.rm.RestAccessPointResource;

/**
 * Rest model slot for HTTP technology adapter
 *
 */
@ModelEntity
@XMLElement
@ImplementationClass(RestModelSlotImpl.class)
@DeclareActorReferences({ ReflectedFMLRTModelSlotInstance.class })
/*@DeclareFlexoRoles({ RestObjectRole.class })
@DeclareEditionActions({ CreateHttpRestResource.class })
@DeclareFlexoBehaviours({ HttpInitializer.class, RestObjectRetriever.class, JsonRequestBehaviour.class })
@DeclareActorReferences({ RestObjectActorReference.class })*/
@FML("RestModelSlot")
public interface RestModelSlot
		extends ReflectedFMLRTModelSlot<RestVirtualModelInstance, RestAccessPointResource, RestAccessPoint, RestTechnologyAdapter> {

	abstract class RestModelSlotImpl
			extends ReflectedFMLRTModelSlotImpl<RestVirtualModelInstance, RestAccessPointResource, RestAccessPoint, RestTechnologyAdapter>
			implements RestModelSlot {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(RestModelSlotImpl.class.getPackage().getName());

		@Override
		public Class<RestTechnologyAdapter> getTechnologyAdapterClass() {
			return RestTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			return super.defaultFlexoRoleName(flexoRoleClass);
		}

		@Override
		public RestTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (RestTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		@Override
		public ModelSlotInstance<?, RestVirtualModelInstance> connectTo(RestAccessPointResource resource, FlexoConceptInstance context) {

			try {
				RestVirtualModelInstanceModelFactory factory = new RestVirtualModelInstanceModelFactory(resource,
						getServiceManager().getEditingContext(), getServiceManager().getTechnologyAdapterService());
				RestVirtualModelInstance restVmi = factory.newInstance(RestVirtualModelInstance.class);
				restVmi.setVirtualModel(getAccessedVirtualModel());
				restVmi.setReflectedModelFactory(factory);
				restVmi.setReflectedResource(resource);

				/*System.out.println("Built VMI: " + xmlVmi);
				System.out.println("Factory: " + xmlVmi.getReflectedModelFactory());
				System.out.println("Resource: " + xmlVmi.getReflectedModelFactory().getResource());
				System.out.println("VM: " + getAccessedVirtualModel());*/

				if (restVmi.getReflectedModelFactory().getReflectedResource() != null
						&& restVmi.getReflectedModelFactory().getReflectedResource().getIODelegate() instanceof StreamIODelegate) {

					/*FMLXMLModelBuilder builder = new FMLXMLModelBuilder(factory, getAccessedVirtualModel());
					builder.setModelContext(xmlVmi);
					builder.deserialize(
							((StreamIODelegate) xmlVmi.getReflectedModelFactory().getResource().getIODelegate()).getInputStream());
					builder.resetModelContext();*/
				}

				ReflectedFMLRTModelSlotInstance<RestVirtualModelInstance, RestAccessPointResource, RestAccessPoint, RestTechnologyAdapter> modelSlotInstance;
				modelSlotInstance = makeActorReference(restVmi, context);
				context.addToActors(modelSlotInstance);
				return modelSlotInstance;

			} catch (ModelDefinitionException e) {
				logger.warning("Unexpected ModelDefinitionException: " + e);
				e.printStackTrace();
				return null;
			}

		}

	}
}
