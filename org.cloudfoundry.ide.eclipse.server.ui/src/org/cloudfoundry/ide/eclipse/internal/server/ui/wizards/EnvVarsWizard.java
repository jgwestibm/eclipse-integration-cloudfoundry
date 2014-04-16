/*******************************************************************************
 * Copyright (c) 2013, 2014 Pivotal Software, Inc. 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, 
 * Version 2.0 (the "License�); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 *  Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 ********************************************************************************/
package org.cloudfoundry.ide.eclipse.internal.server.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.cloudfoundry.ide.eclipse.internal.server.core.CloudFoundryPlugin;
import org.cloudfoundry.ide.eclipse.internal.server.core.CloudFoundryServer;
import org.cloudfoundry.ide.eclipse.internal.server.core.client.CloudFoundryApplicationModule;
import org.cloudfoundry.ide.eclipse.internal.server.core.client.DeploymentInfoWorkingCopy;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

/**
 * Allows an application's environment variables to be edited and set in the
 * app's deployment info. The environment variables are also set in the server.
 */
public class EnvVarsWizard extends Wizard {

	private final CloudFoundryServer cloudServer;

	private final CloudFoundryApplicationModule appModule;

	private DeploymentInfoWorkingCopy infoWorkingCopy;

	private CloudFoundryApplicationEnvVarWizardPage envVarPage;

	public EnvVarsWizard(CloudFoundryServer server, CloudFoundryApplicationModule appModule,
			DeploymentInfoWorkingCopy workingCopy) {

		Assert.isNotNull(server);
		Assert.isNotNull(appModule);
		Assert.isNotNull(workingCopy);

		this.infoWorkingCopy = workingCopy;
		this.cloudServer = server;
		setWindowTitle(server.getServer().getName());
		setNeedsProgressMonitor(true);
		this.appModule = appModule;
	}

	@Override
	public void addPages() {

		envVarPage = new CloudFoundryApplicationEnvVarWizardPage(cloudServer, infoWorkingCopy);
		envVarPage.setWizard(this);
		addPage(envVarPage);
	}

	@Override
	public boolean performFinish() {
		infoWorkingCopy.save();
		final IStatus[] result = new IStatus[1];
		try {

			envVarPage.setMessage("Updating environment variables. Please wait while the process completes.");
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						cloudServer.getBehaviour().updateEnvironmentVariables(appModule, monitor);

					}
					catch (CoreException e) {
						result[0] = e.getStatus();
					}
				}
			});
			envVarPage.setMessage(null);
		}
		catch (InvocationTargetException e) {
			result[0] = CloudFoundryPlugin.getErrorStatus(e);
		}
		catch (InterruptedException e) {
			result[0] = CloudFoundryPlugin.getErrorStatus(e);

		}
		if (result[0] != null && !result[0].isOK()) {
			envVarPage.setErrorMessage("Environment variables may not have changed correctly due to: "
					+ result[0].getMessage());
			return false;
		}
		else {
			return true;
		}
	}

}
