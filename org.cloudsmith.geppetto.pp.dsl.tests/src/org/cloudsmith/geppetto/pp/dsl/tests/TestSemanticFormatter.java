/**
 * Copyright (c) 2012 Cloudsmith Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Cloudsmith
 * 
 */
package org.cloudsmith.geppetto.pp.dsl.tests;

import org.cloudsmith.geppetto.pp.AssignmentExpression;
import org.cloudsmith.geppetto.pp.Expression;
import org.cloudsmith.geppetto.pp.LiteralList;
import org.cloudsmith.geppetto.pp.PPFactory;
import org.cloudsmith.geppetto.pp.PuppetManifest;
import org.cloudsmith.geppetto.pp.dsl.xt.dommodel.DomModelUtils;
import org.cloudsmith.geppetto.pp.dsl.xt.dommodel.IDomNode;
import org.cloudsmith.geppetto.pp.dsl.xt.dommodel.formatter.IDomModelFormatter;
import org.cloudsmith.geppetto.pp.dsl.xt.dommodel.formatter.IFormattingContext;
import org.cloudsmith.geppetto.pp.dsl.xt.dommodel.formatter.OneWhitespaceDomFormatter;
import org.cloudsmith.geppetto.pp.dsl.xt.serializer.DomBasedSerializer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.ReplaceRegion;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

/**
 * @author henrik
 * 
 */
public class TestSemanticFormatter extends AbstractPuppetTests {
	public static class DebugFormatter implements IDomModelFormatter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.cloudsmith.geppetto.pp.dsl.xt.dommodel.formatter.IDomModelFormatter#format(org.cloudsmith.geppetto.pp.dsl.xt.dommodel.IDomNode,
		 * org.eclipse.xtext.util.ITextRegion, org.cloudsmith.geppetto.pp.dsl.xt.dommodel.formatter.IFormattingContext,
		 * org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor)
		 */
		@Override
		public ReplaceRegion format(IDomNode dom, ITextRegion regionToFormat, IFormattingContext formattingContext,
				Acceptor errors) {
			System.err.println(DomModelUtils.compactDump(dom, true));
			return new ReplaceRegion(0, 3, "TBD");
		}

	}

	public static class TestSetup extends PPTestSetup {
		public static class TestModule extends PPTestModule {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.xtext.service.AbstractGenericModule#configure(com.google.inject.Binder)
			 */
			@Override
			public void configure(Binder binder) {
				super.configure(binder);
				binder.bind(ISerializer.class).to(DomBasedSerializer.class);
				binder.bind(IIndentationInformation.class).to(IIndentationInformation.Default.class);
				binder.bind(IDomModelFormatter.class).to(OneWhitespaceDomFormatter.class);
			}
		}

		@Override
		public Injector createInjector() {
			return Guice.createInjector(Modules.override(new org.cloudsmith.geppetto.pp.dsl.PPRuntimeModule()).with(
				new TestModule()));
		}
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		// with(PPStandaloneSetup.class);
		with(TestSetup.class);
	}

	public void test_Serialize_assignArray() throws Exception {
		PuppetManifest pp = pf.createPuppetManifest();
		EList<Expression> statements = pp.getStatements();
		AssignmentExpression assignment = PPFactory.eINSTANCE.createAssignmentExpression();
		assignment.setLeftExpr(createVariable("a"));
		LiteralList pplist = PPFactory.eINSTANCE.createLiteralList();
		assignment.setRightExpr(pplist);
		pplist.getElements().add(createSqString("10"));
		pplist.getElements().add(createSqString("20"));
		pp.getStatements().add(assignment);
		String fmt = "$a = [10, 20]\n";
		String s = serialize(pp);
		assertEquals("serialization should produce same result", fmt, s);
	}

}