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
package org.cloudsmith.geppetto.pp.dsl.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

import com.google.inject.Singleton;

/**
 * A facade that helps with preference checking.
 * (The idea is to not litter the code with specifics about how preferences are stated, where they are stored etc.)
 * 
 */
@Singleton
public class PPPreferencesHelper implements IPreferenceStoreInitializer, IPropertyChangeListener {

	private int autoInsertOverrides = 0;

	private final static String OVERRIDE_AUTO_INSERT = "org.cloudsmith.geppetto.override.autoinsert";

	public final static int AUTO_INSERT_BRACKETS = 0x01;

	public final static int AUTO_INSERT_BRACES = 0x02;

	public final static int AUTO_INSERT_PARENTHESES = 0x04;

	public final static int AUTO_INSERT_COMMENT = 0x08;

	public final static int AUTO_INSERT_SQ = 0x10;

	public final static int AUTO_INSERT_DQ = 0x20;

	public PPPreferencesHelper() {
		configureAutoInsertOverride();
	}

	private void configureAutoInsertOverride() {
		String propValue = System.getProperty(OVERRIDE_AUTO_INSERT, "0");

		try {
			autoInsertOverrides = Integer.parseInt(propValue);
		}
		catch(NumberFormatException e) {
			autoInsertOverrides = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer#initialize(org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess)
	 */
	@Override
	public void initialize(IPreferenceStoreAccess access) {
		IPreferenceStore store = access.getWritablePreferenceStore();
		store.setDefault(PPPreferenceConstants.AUTO_EDIT_STRATEGY, 0);
		autoInsertOverrides = (int) store.getLong(PPPreferenceConstants.AUTO_EDIT_STRATEGY);
		access.getWritablePreferenceStore().addPropertyChangeListener(this);

	}

	public boolean isAutoBraceInsertWanted() {
		return (autoInsertOverrides & AUTO_INSERT_BRACES) == 0;
	}

	public boolean isAutoBracketInsertWanted() {
		return (autoInsertOverrides & AUTO_INSERT_BRACKETS) == 0;
	}

	public boolean isAutoDqStringInsertWanted() {
		return (autoInsertOverrides & AUTO_INSERT_DQ) == 0;
	}

	public boolean isAutoMLCommentInsertWanted() {
		return (autoInsertOverrides & AUTO_INSERT_COMMENT) == 0;
	}

	public boolean isAutoParenthesisInsertWanted() {
		return (autoInsertOverrides & AUTO_INSERT_PARENTHESES) == 0;
	}

	public boolean isAutoSqStringInsertWanted() {
		return (autoInsertOverrides & AUTO_INSERT_SQ) == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// System.err.println("Preference changed value: " + event.getProperty());
		if(PPPreferenceConstants.AUTO_EDIT_STRATEGY.equals(event.getProperty()))
			autoInsertOverrides = Integer.valueOf((String) event.getNewValue());
	}
}
