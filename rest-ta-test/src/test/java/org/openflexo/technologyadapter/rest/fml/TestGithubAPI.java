/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.rest.fml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.rest.RestTechnologyAdapter;
import org.openflexo.technologyadapter.rest.rm.RestAccessPointResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

@RunWith(OrderedRunner.class)
public class TestGithubAPI extends OpenflexoProjectAtRunTimeTestCase {

	private static FlexoEditor editor;
	private static FlexoProject<File> project;

	private static VirtualModel virtualModel;

	private static FMLRTVirtualModelInstance vmi;
	private static RestAccessPointResource githubAccessPointResource;

	/**
	 * Retrieve the ViewPoint
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testLoadViewPoint() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager(RestTechnologyAdapter.class);

		// XMLTechnologyAdapter xmlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		// serviceManager.activateTechnologyAdapter(xmlTA, true);

		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			System.out.println("RC: " + rc.getDefaultBaseURI() + " " + rc);
		}

		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://openflexo.org/rest-test");
		System.out.println("resourceCenter=" + resourceCenter);
		assertNotNull(resourceCenter);

		/*for (FlexoResource<?> flexoResource : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println("> " + flexoResource + " loaded: " + flexoResource.isLoaded() + " uri=" + flexoResource.getURI());
		}*/

		/*CompilationUnitResource cur = (CompilationUnitResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/xml-test/FML/reflect/TestFMLReflect.fml");
		System.out.println("Hop: " + cur);*/

		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		virtualModel = vpLib.getVirtualModel("http://openflexo.org/rest-test/GithubAPITest.fml");

		assertNotNull(virtualModel);

		System.out.println(virtualModel.getCompilationUnit().getFMLPrettyPrint());

		assertCompilationUnitIsValid(virtualModel.getCompilationUnit());

		System.out.println(virtualModel.getCompilationUnit().getNormalizedFML());

		githubAccessPointResource = (RestAccessPointResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/rest-test/AccessPoints/GithubAPIAccessPoint.rest");
		assertNotNull(githubAccessPointResource);

		System.out.println("githubAccessPointResource=" + githubAccessPointResource);
		System.out.println("RD:" + githubAccessPointResource.getResourceData());

	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testCreateProject() {
		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void testCreateInstance() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		System.err.println("testCreateInstance()");
		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		action.setNewVirtualModelInstanceName("TestGithubAPI");
		action.setNewVirtualModelInstanceTitle("TestGithubAPI");
		action.setVirtualModel(virtualModel);

		CreationScheme creationScheme = virtualModel.getCreationSchemes().get(0);
		action.setCreationScheme(creationScheme);

		action.setParameterValue(creationScheme.getParameters().get(0), githubAccessPointResource);

		System.err.println("On execute " + creationScheme.getFMLPrettyPrint());

		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		vmi = action.getNewVirtualModelInstance();
		assertNotNull(vmi);
		assertNotNull(vmi.getResource());

		System.err.println("Github: " + vmi + " in " + vmi.getResource().getIODelegate());

		RestVirtualModelInstance githubAPI = vmi.getFlexoPropertyValue("github");
		System.err.println("githubAPI=" + githubAPI);
		assertNotNull(githubAPI);

		/*XMLVirtualModelInstance<?> library = vmi.getFlexoPropertyValue("library");
		assertNotNull(library);
		assertEquals("A Library", library.getFlexoPropertyValue("name"));
		assertSame(library.getVirtualModel(),
				serviceManager.getVirtualModelLibrary().getVirtualModel("http://openflexo.org/xml-test/FML/reflect/Library1"));
		
		assertEquals(2, library.getFlexoConceptInstances().size());
		XMLFlexoConceptInstance book1 = (XMLFlexoConceptInstance) library.getFlexoConceptInstances().get(0);
		assertNotNull(book1);
		assertEquals("Book", book1.getFlexoConcept().getName());
		assertEquals("toto", book1.getFlexoPropertyValue("title"));
		assertEquals(35, (int) book1.getFlexoPropertyValue("pages"));
		assertEquals("Mystery", book1.getFlexoPropertyValue("category"));
		assertEquals("Le Lapin", book1.getFlexoPropertyValue("author"));
		
		XMLFlexoConceptInstance book2 = (XMLFlexoConceptInstance) library.getFlexoConceptInstances().get(1);
		assertNotNull(book2);
		
		assertEquals(2, ((List) library.getFlexoPropertyValue("books")).size());*/

	}

}
