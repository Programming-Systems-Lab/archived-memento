/*
 * VemCommandLineTool.java
 *
 * Created on February 9, 2003, 12:29 AM
 */

package psl.memento.server.vem.ui;

import psl.memento.server.vem.*;
import psl.memento.server.vem.layout.*;
import psl.memento.server.vem.util.*;
import com.hp.hpl.mesa.rdf.jena.model.Model;

/**
 *
 * @author  vlad
 */
public class VemCommandLineTool {

	private static final String usage =
		"Usage: java VemCommandLineTool <Base Dir> <URL>";

	/** Creates a new instance of VemCommandLineTool */
	public VemCommandLineTool(String uri) {
		System.out.println("Querying " + uri);

		FraxAdapter fraxAdapter = new FraxAdapter();
		Model m = fraxAdapter.getModel(uri);
		ObjectModeler om = new DefaultObjectModeler();
		Layout layout = new AdvancedLayout();
		VemService service = new VemService(null);

		service.createSector(m, om, layout);
		System.out.println("done.");
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println(usage);
			return;
		}

		ResourceFileManager.getInstance(args[0], true);
		new VemCommandLineTool(args[1]);
	}
}
