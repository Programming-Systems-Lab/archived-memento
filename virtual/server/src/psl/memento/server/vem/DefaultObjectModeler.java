/*
 * DefaultObjectModeler.java
 *
 * Created on February 8, 2003, 12:30 PM
 */

package psl.memento.server.vem;

import java.net.URI;
import java.net.URISyntaxException;

import psl.memento.server.frax.vocabulary.FileVocab;
import psl.memento.server.frax.vocabulary.HTMLVocab;
import psl.memento.server.frax.vocabulary.Vocab;

import com.hp.hpl.mesa.rdf.jena.model.Property;

/**
 *
 * @author  vlad
 */
public class DefaultObjectModeler
	extends ObjectModelerBase
	implements ObjectModeler {

	/** Creates a new instance of DefaultObjectModeler */
	public DefaultObjectModeler() {
		super();
		initialize();
	}

	protected void initialize() {
		defineRules();
		defineViews();
	}

	protected void defineRules() {
		addRuleEndsWith("/", "directory");
		addRuleContains("@", "email");
		addRule(MATCH_EXTENSION, null);
		addRuleMatchAll(null);
	}

	protected void defineViews() {
		setView("directory", rfm.getRF("violin_case.3ds"));
		setView("email", rfm.getRF("stool.3ds"));
		setView("unknown", rfm.getRF("cube.3ds"));
		setView(".gif", rfm.getRF("cube.3ds"));
		setView(".html", rfm.getRF("stool.3ds"));
	}

	protected String preprocess(Property prop, String iName) {

		Property links = HTMLVocab.kLinks.getProperty();
		Property images = HTMLVocab.kImages.getProperty();

		if (prop == links || prop == images) {
			try {
				return new URI(iName).getPath();
			} catch (URISyntaxException use) {
			}
		}

		return iName;
	}

	public Vocab[] getVocabsToSearch() {
		return properties;
	}

	protected static Vocab[] properties =	{
			HTMLVocab.kLinks,
			HTMLVocab.kImages,
			FileVocab.kContents
	};
}
