/*
 * ResourceClassifier.java
 *
 * Created on February 3, 2003, 5:52 PM
 */

package psl.memento.server.vem;

import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.rdb.*;
import psl.memento.server.frax.*;
import psl.memento.server.frax.vocabulary.*;
import java.net.*;

/**
 *
 * @author  vs299
 */
public class ResourceClassifier {
	
	private Model mModel;
	private DataReader dr;
	
	/** Creates a new instance of ResourceClassifier */
	public ResourceClassifier(Model iModel, DataReader idr) 
	{
		mModel = iModel;
		dr = idr;
	}
	
	public void convertToRoomObjects() throws URISyntaxException, RDFException {
		RoomObject ro;
		URI link;
		String path = "";
		Property prop;
		NodeIterator iter;
		
		Property properties[] = {
			HTMLVocab.kLinks.getProperty(),
			HTMLVocab.kImages.getProperty(),
			FileVocab.kContents.getProperty()
		};
		
		dr.setRoom(300, 300, 300);
		
		for (int i=0; i<properties.length; i++) {
			prop = properties[i];
			
			iter = mModel.listObjectsOfProperty(prop);
			if (!iter.hasNext())
				continue;
			
			RDFNode n = iter.next();
			Container c = (Container)n;
			iter = c.iterator();

			while (iter.hasNext()) {
				ro = new RoomObject(15, 15, 15);
				link = new URI(iter.next().toString());
				path = link.getPath();

				if (path.indexOf("@") != -1) ro.type = "email";
				else if (path.endsWith("/")) ro.type = "directory";
				else if (path.indexOf(".") != -1)
					ro.type = path.substring(path.lastIndexOf("."));
				else ro.type = "misc";

				dr.addRoomObject(ro);
			}
		}
	}
}
