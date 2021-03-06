/*
* generated by Xtext
*/
package com.puppetlabs.geppetto.pp.dsl.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import com.puppetlabs.geppetto.pp.dsl.services.PPGrammarAccess;

public class PPParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private PPGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_SL_COMMENT", "RULE_ML_COMMENT");
	}
	
	@Override
	protected com.puppetlabs.geppetto.pp.dsl.parser.antlr.internal.InternalPPParser createParser(XtextTokenStream stream) {
		return new com.puppetlabs.geppetto.pp.dsl.parser.antlr.internal.InternalPPParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "PuppetManifest";
	}
	
	public PPGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(PPGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}
