/*
 * DataServer.java
 *
 * Copyright (c) 2002: The Trustees of Columbia University
 * in the City of New York.  All Rights Reserved.
 */

package psl.chime4.server.data;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import psl.chime.sienautils.*;	// for FRAX
import psl.chime.frax.*;	// for FRAX
import psl.chime4.server.data.metadata.*;
import psl.chime4.server.di.*;
import psl.chime4.server.librarian.*;

/**
 * The <code>DataServer</code> class represents the central access point for
 * the public API, through which other components like the librarian, world
 * manager, and zone manager communicate with the data server.  Each CHIME
 * server contains exactly one data server, created and destroyed by the
 * world manager.
 *
 * @author Mark Ayzenshtat
 */
public class DataServer {
	public static final DIType kLibrarianSearchMessageType
    = new DIType("LibrarianSearch");
	private static final ResourceDescriptor[] kZeroResDes =
		new ResourceDescriptor[0];
	
	private boolean mInitialized;
	private DAOFactory mDAOFactory;
	private PersistenceBroker mPersistenceBroker;
	private DirectoryInterface mDirInterface;
	private DIEventReceiver mDirEventReceiver;
	
	public DataServer() {
		mInitialized = false;
		mDAOFactory = new JdbcDAOFactory();
		mPersistenceBroker = 
			new PersistenceBroker(this, new NaivePersistenceScheme(this));
		// mDirInterface = (acquire this ref somehow
		// ...probably passed to constructor...
		// ...assume that DirectoryInterface is already connected when we get it)
		mDirEventReceiver = new DataServerEventReceiver(this);
	}
	
	/**
	 * Initializes this data server.
	 */
	public void startup() {
		if (mInitialized) {
			return;
		}
		
		// subscribe to directory service
		mDirInterface.subscribe(mDirEventReceiver, kLibrarianSearchMessageType);
		
		mInitialized = true;
	}
	
	/**
	 * Shuts down this data server.
	 */
	public void shutdown() {
		if (!mInitialized) {
			return;
		}
		
		// unsubscribe from directory service
		mDirInterface.unsubscribe(mDirEventReceiver, kLibrarianSearchMessageType);
		
		mInitialized = false;
	}
	
	/**
	 * Retrieves this data server's DAO factory.  The DAO factory enables client
	 * code to obtain data access objects with which to read/write persistent
	 * objects from/to the underlying data store.
	 *
	 * @return this data server's DAO factory
	 */
	public DAOFactory getDAOFactory() {
		return mDAOFactory;
	}
	
	/**
	 * Given a resource descriptor that contains only a protocol and a path,
	 * this method queries Frax to fill in all of the other fields.
	 *
	 * @param iRD the resource descriptor to complete
	 */
	public void completeMetadata(ResourceDescriptor iRD) {
		URI u = null;		
		try {			
			u = new URI(iRD.getProtocol().toLowerCase() + "://" + iRD.getName());
		} catch (URISyntaxException ex) {
			throw new 
				RuntimeException("Could not form URI from resource descriptor.");
		}
		
		String metaData = xmlQueryFrax(u);
		iRD = fillRDFromXML(metaData, iRD);
	}
	
	/**
	 * Carries out a librarian search.  The data server first checks its own
	 * data store before contacting FRAX and other CHIME servers (via service
	 * discovery) as needed.
	 *
	 * @param iReq an object that encapsulates the search constraints
	 */
	public void doLibrarianSearch(LibrarianRequest iReq) {
		// check own data store
		librarianSearchLocalDataStore(iReq);
		
		// send library request URLs to FRAX
		librarianSendToFrax(iReq);
		
		// send librarian request across network
		librarianSearchNetworkDataStores(iReq);
	}
	
	private void notifyLibrarianOfResult(LibrarianResult iResult) {
		// FIXME: Correct and uncomment the following code
		//        once Peter finishes the librarian API.		
/*
		// notify the librarian of the result
		mLibrarian.addLibrarianResult(iResult);
*/
	}
	
	private void librarianSearchLocalDataStore(LibrarianRequest iReq) {
		// get the appropriate DAO from the DAO factory
		ResourceDescriptorDAO dao = 
			(ResourceDescriptorDAO) mDAOFactory.getDAO(ResourceDescriptor.class);
		
		// carry out the search on the local data store
		LibrarianResult result = dao.doLibrarianSearch(iReq);
		notifyLibrarianOfResult(result);
	}
	
	private void librarianSearchNetworkDataStores(LibrarianRequest iReq) {		
    // convert entire LibrarianRequest object into a byte array
    ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
    
    try {
      ObjectOutputStream oos = new ObjectOutputStream(baos);        
      oos.writeObject(iReq);
    } catch (IOException ex) {
      throw new
        RuntimeException("Couldn't package librarian request for transport.");
    }
    
    byte[] libReqAsBytes = baos.toByteArray();
    
    // publish library request for other data servers to discover
    mDirInterface.publish(kLibrarianSearchMessageType, 
      new DIMessageBody(libReqAsBytes));
        
/*
		// save results to local data store using
		// some intelligent persistence scheme
		try {
			mPersistenceBroker.smartPersistObjects(resultArray);
		} catch (DataAccessException ex) {
			throw new RuntimeException("Could not intelligently persist librarian " +
				"results from network-wide data stores.");
		}

		notifyLibrarianOfResult(result);
*/
 }
	
	private void librarianSendToFrax(LibrarianRequest iReq) {
		URI[] targets = iReq.getURIs();		
		ResourceDescriptor metadata;
		LibrarianResult result = new LibrarianResult();
		List resultList = new ArrayList(30);
		
		result.setSourceRequest(iReq);
		for (int i = 0; i < targets.length; i++) {
			metadata = queryFrax(targets[i]);	
			resultList.add(metadata);
		}
		
		ResourceDescriptor[] resultArray = 
			(ResourceDescriptor[]) resultList.toArray(kZeroResDes);
		
		// save results to local data store using
		// some intelligent persistence scheme
		try {
			mPersistenceBroker.smartPersistObjects(resultArray);
		} catch (DataAccessException ex) {
			throw new RuntimeException("Could not intelligently persist librarian " +
				"results from Frax.");
		}
		
		result.setSearchResults(resultArray);
		notifyLibrarianOfResult(result);
	}
	
	private String xmlQueryFrax(URI iTarget) {
		// right now, we need to construct a SienaObject to pass data to FRAX,
		// which is awful and really disgusting but will have to remain
		// until we replace it with WellWrittenFrax (tm) in Fall 2002
		// NOTE: We just use a SienaObject as a container; we're not actually
		//       putting any data on the Siena bus
		SienaObject s = new SienaObject(iTarget.getScheme(), iTarget.toString(),
			"Chime4DataServer", null, null, null, false);
						
		try {
			FRAXProtLoader fpl = new FRAXProtLoader();
			String xmlMetadata = fpl.runProtExpectingReturn(s);			
			
			return xmlMetadata;
		} catch (Exception ex) {
			throw new RuntimeException("Error querying FRAX.", ex);
		}
	}
	
	private ResourceDescriptor queryFrax(URI iTarget) {
		return fillRDFromXML(xmlQueryFrax(iTarget),
			ResourceDescriptorFactory.getInstance().newRD(iTarget.getScheme()));
	}
	
	/**
	 * Given some XML from Frax and a ResourceDescriptor, this method parses
	 * the XML and fills up the ResourceDescriptor fields accordingly.  This
	 * method is pretty ugly (e.g. hardcoded tag names), but once NewFrax is
	 * around, it will go the way of the dodo.
	 */
	private ResourceDescriptor fillRDFromXML(String iXML,
			ResourceDescriptor iRD) {    
		System.out.println(iXML);
				
		Document doc = null;
				
		// create a Document object from the supplied XML string
		try {			
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(new StringReader(iXML));
		} catch (JDOMException ex) {
			throw new RuntimeException("Could not build document from raw XML.");
		}
    
    iRD.completeFromDocument(doc);
		
		return iRD;
	}
  
  /**
   * An implementation of the directory service <code>DIEventReceiver</code>
   * interface, through which this data server communicates with other data
   * servers across the network.
   *
   * @author Mark Ayzenshtat
   */
  private static class DataServerEventReceiver implements DIEventReceiver {
    private DataServer mDataServer;

    DataServerEventReceiver(DataServer iDataServer) {
      mDataServer = iDataServer;
    }

    public void receiveMessage(DIMessage iMessage) {                
      LibrarianRequest request;
      ByteArrayInputStream bais = new ByteArrayInputStream(
        iMessage.getBody().toBytes());

      try {
        ObjectInputStream ois = new ObjectInputStream(bais);
        request = (LibrarianRequest) ois.readObject();
      } catch (Exception ex) {
        throw new
          RuntimeException(
            "Couldn't unpackage librarian request from transport."
          );
      }
      
      mDataServer.librarianSearchLocalDataStore(request);
      
      // FIXME: We need to get the result, package it up, and send it back
    }

    public void receiveEvent(DIEvent iEvent) {
      // we don't care about events
    }

    public void receiveResult(DIHost iResult) {
      // we don't care about results
    }
  }
	
	public static void main(String[] args) {
		DataServer ds = new DataServer();
    
    ds.startup();
		
		ResourceDescriptor rd = ResourceDescriptorFactory
			.getInstance().newRD("text/html");		
		rd.setProtocol("HTTP");
    rd.setName("www.cs.columbia.edu");
		
		ds.completeMetadata(rd);
	
    System.out.println("HEMLOCK");
    System.out.println(rd.getName());
    System.out.println(rd.getProtocol());
    System.out.println(rd.getSize());
    System.out.println(rd.getType());
    System.out.println(rd.getWhenLastModified());
    System.out.println();
    HtmlResourceDescriptor hrd = (HtmlResourceDescriptor) rd;
    String[] links = hrd.getLinks();
    for (int i = 0; i < links.length; i++) {
      System.out.println(links[i]);
    }
  }  
}