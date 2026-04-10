package org.openflexo.technologyadapter.rest;

import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceFactories;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.rest.binding.RestBindingFactory;
import org.openflexo.technologyadapter.rest.rm.RestAccessPointResourceFactory;

@DeclareModelSlots({ RestModelSlot.class })
@DeclareResourceFactories({ RestAccessPointResourceFactory.class })
public class RestTechnologyAdapter extends TechnologyAdapter<RestTechnologyAdapter> {

	private RestBindingFactory bindingFactory;

	@Override
	public String getIdentifier() {
		return "REST";
	}

	@Override
	public String getName() {
		return "REST Technology Adapter";
	}

	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/RestTechnologyAdapter";
	}

	@Override
	public TechnologyContextManager<RestTechnologyAdapter> createTechnologyContextManager(
			FlexoResourceCenterService flexoResourceCenterService) {
		return new RestTechnologyContextManager(this, flexoResourceCenterService);
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		if (bindingFactory == null) {
			bindingFactory = new RestBindingFactory();
		}
		return bindingFactory;
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> flexoResourceCenter, I i) {
		return false;
	}

}
