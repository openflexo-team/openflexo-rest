package org.openflexo.technologyadapter.rest.rm;

import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.TechnologySpecificPamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.rest.RestTechnologyAdapter;
import org.openflexo.technologyadapter.rest.model.RestAccessPoint;
import org.openflexo.technologyadapter.rest.model.RestAccessPointModelFactory;
import org.openflexo.technologyadapter.rest.rm.RestAccessPointResource.RestAccessPointResourceImpl;

/**
 *
 */
public class RestAccessPointResourceFactory extends
		TechnologySpecificPamelaResourceFactory<RestAccessPointResource, RestAccessPoint, RestTechnologyAdapter, RestAccessPointModelFactory> {

	private static final Logger logger = FlexoLogger.getLogger(RestAccessPointResourceImpl.class.getPackage().toString());

	public static final String REST_EXTENSION = "rest";

	public RestAccessPointResourceFactory() throws ModelDefinitionException {
		super(RestAccessPointResource.class);
	}

	@Override
	public RestAccessPoint makeEmptyResourceData(RestAccessPointResource resource) {
		return resource.getFactory().makeRestAccessPoint();
	}

	@Override
	public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		String name = resourceCenter.retrieveName(serializationArtefact);
		return FilenameUtils.isExtension(name, REST_EXTENSION);
	}

	public <I> RestAccessPointResource makeAccessPointResource(String baseName, RepositoryFolder<RestAccessPointResource, I> folder)
			throws SaveResourceException, ModelDefinitionException {

		FlexoResourceCenter<I> rc = folder.getResourceRepository().getResourceCenter();
		String artefactName = baseName.endsWith(REST_EXTENSION) ? baseName : baseName + REST_EXTENSION;
		I serializationArtefact = rc.createEntry(artefactName, folder.getSerializationArtefact());
		RestAccessPointResource newAccessPointResource = makeResource(serializationArtefact, rc, true);

		return newAccessPointResource;
	}

	@Override
	public <I> RestAccessPointResource registerResource(RestAccessPointResource resource, FlexoResourceCenter<I> resourceCenter) {
		super.registerResource(resource, resourceCenter);

		// Register the resource in the repository of supplied resource center
		// registerResourceInResourceRepository(resource,
		// getTechnologyAdapter(resourceCenter.getServiceManager()).getDSLResourceRepository(resourceCenter));

		return resource;
	}

	@Override
	public RestAccessPointModelFactory makeModelFactory(RestAccessPointResource resource,
			TechnologyContextManager<RestTechnologyAdapter> technologyContextManager) throws ModelDefinitionException {
		return new RestAccessPointModelFactory(resource, technologyContextManager.getServiceManager().getEditingContext());
	}

}
