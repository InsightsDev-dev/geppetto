/**
 * Copyright (c) 2011 Cloudsmith Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Cloudsmith
 * 
 */
package org.cloudsmith.geppetto.forge.util;

import java.io.File;
import java.util.List;

import org.cloudsmith.geppetto.diagnostic.Diagnostic;
import org.cloudsmith.geppetto.forge.v2.model.Dependency;
import org.cloudsmith.geppetto.forge.v2.model.Metadata;
import org.cloudsmith.geppetto.forge.v2.model.ModuleName;
import org.cloudsmith.geppetto.semver.Version;
import org.cloudsmith.geppetto.semver.VersionRange;
import org.jrubyparser.SourcePosition;
import org.jrubyparser.ast.RootNode;

/**
 * A Modulefile parser that only accepts strict entries and adds them
 * to a Metadata instance
 */
public class StrictModulefileParser extends ModulefileParser {

	private final Metadata md;

	public StrictModulefileParser(Metadata md) {
		this.md = md;
	}

	private void addDependency(SourcePosition pos, String name, String versionRequirement) {
		Dependency dep = new DependencyWithPosition(
			pos.getStartOffset(), pos.getEndOffset() - pos.getStartOffset(), pos.getStartLine(),
			new File(pos.getFile()));
		dep.setName(createModuleName(name, pos));
		if(versionRequirement != null)
			try {
				dep.setVersionRequirement(VersionRange.create(versionRequirement));
			}
			catch(IllegalArgumentException e) {
				addError(pos, e.getMessage());
			}
		md.getDependencies().add(dep);

	}

	@Override
	protected void call(CallSymbol key, SourcePosition pos, List<Argument> args) {
		int nargs = args.size();
		switch(nargs) {
			case 1:
				String arg = args.get(0).toStringOrNull();
				switch(key) {
					case author:
						md.setAuthor(arg);
						break;
					case dependency:
						addDependency(pos, arg, null);
						break;
					case description:
						md.setDescription(arg);
						break;
					case license:
						md.setLicense(arg);
						break;
					case name:
						md.setName(createModuleName(arg, pos));
						break;
					case project_page:
						md.setProjectPage(arg);
						break;
					case source:
						md.setSource(arg);
						break;
					case summary:
						md.setSummary(arg);
						break;
					case version:
						try {
							md.setVersion(Version.create(arg));
						}
						catch(IllegalArgumentException e) {
							addError(pos, e.getMessage());
						}
				}
				break;
			case 2:
			case 3:
				if(key == CallSymbol.dependency) {
					addDependency(pos, args.get(0).toStringOrNull(), args.get(1).toStringOrNull());
					if(nargs == 3)
						addWarning(pos, "Ignoring third argument to dependency");
					break;
				}
				// Fall through
			default:
				noResponse(key.name(), pos, 0);
		}
	}

	private ModuleName createModuleName(String name, SourcePosition pos) {
		if(name == null)
			return null;

		name = name.trim();
		if(name.length() == 0)
			return null;

		ModuleName m = null;
		try {
			m = new ModuleName(name, true);
		}
		catch(IllegalArgumentException e1) {
			try {
				m = new ModuleName(name, false);
				addWarning(pos, e1.getMessage());
			}
			catch(IllegalArgumentException e2) {
				addError(pos, e2.getMessage());
			}
		}
		return m;
	}

	@Override
	public void parseRubyAST(RootNode root, Diagnostic diagnostics) {
		md.getDependencies().clear();
		md.getTypes().clear();
		md.getChecksums().clear();
		super.parseRubyAST(root, diagnostics);
	}
}