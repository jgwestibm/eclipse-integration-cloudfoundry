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
package org.cloudfoundry.ide.eclipse.internal.server.ui.wizards;

import org.cloudfoundry.ide.eclipse.internal.server.core.tunnel.ITunnelServiceCommands;
import org.cloudfoundry.ide.eclipse.internal.server.ui.tunnel.ServiceTunnelCommandPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TunnelCommandDefinitionWizardPage extends CloudFoundryAwareWizardPage {
	private final ITunnelServiceCommands originalCommands;

	private ServiceTunnelCommandPart commandPart;

	protected TunnelCommandDefinitionWizardPage(ITunnelServiceCommands originalCommands, ImageDescriptor banner) {
		super("Service Tunnel Commands Page", "Service Tunnel Commands",
				"Add, delete, or edit commands to launch external applications for service tunnels", banner);
		this.originalCommands = originalCommands;
	}

	public void createControl(Composite parent) {
		TunnelCommandDefinitionWizard wizard = (TunnelCommandDefinitionWizard) getWizard();
		commandPart = new ServiceTunnelCommandPart(originalCommands, wizard.getServiceContext());
		Control control = commandPart.createPart(parent);
		setControl(control);

	}

	public ITunnelServiceCommands getExtToolLaunchCommandsServer() {
		return commandPart != null ? commandPart.getUpdatedCommands() : originalCommands;
	}

}
