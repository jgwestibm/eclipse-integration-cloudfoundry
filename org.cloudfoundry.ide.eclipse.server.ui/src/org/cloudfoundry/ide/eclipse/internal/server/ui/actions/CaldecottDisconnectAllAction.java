/*******************************************************************************
 * Copyright (c) 2012, 2014 Pivotal Software, Inc. 
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
package org.cloudfoundry.ide.eclipse.internal.server.ui.actions;

import org.cloudfoundry.ide.eclipse.internal.server.core.CloudFoundryPlugin;
import org.cloudfoundry.ide.eclipse.internal.server.core.CloudFoundryServer;
import org.cloudfoundry.ide.eclipse.internal.server.core.client.TunnelBehaviour;
import org.cloudfoundry.ide.eclipse.internal.server.ui.CloudFoundryImages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;

public class CaldecottDisconnectAllAction extends Action {

	protected final CloudFoundryServer cloudServer;

	public CaldecottDisconnectAllAction(CloudFoundryServer cloudServer) {
		this.cloudServer = cloudServer;
		setActionValues();
	}

	protected void setActionValues() {
		setText("Disconnect All Tunnels");
		setImageDescriptor(CloudFoundryImages.DISCONNECT);
		setToolTipText("Disconnect All Tunnels");
		setEnabled(true);
	}

	public void run() {

		Job job = new Job("Stopping all tunnels for: " + cloudServer.getDeploymentName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					new TunnelBehaviour(cloudServer).stopAndDeleteAllTunnels(monitor);
				}
				catch (CoreException e) {
					return CloudFoundryPlugin.getErrorStatus(e);
				}
				return Status.OK_STATUS;
			}

		};
		job.setSystem(false);
		job.schedule();
	}
}
