package psl.memento.pervasive.recommendation.keywordfinder.centroid;

import psl.conversation.ConversationLogIterator;
import psl.conversation.KeywordContainer;
import psl.conversation.KeywordFinder;
import psl.conversation.SuggestionManagerKeywordCallback;
import psl.conversation.XMLContainer;

/**
 * 
 */
public class TestStub implements SuggestionManagerKeywordCallback {
	/**
	 * Constructor for TestStub.
	 */
	public TestStub() {
		super();
	}

	/**
	 * @see psl.conversation.SuggestionManagerKeywordCallback#signal(psl.conversation.KeywordContainer)
	 */
	public void signal(KeywordContainer kc) {
	}
	
	private void runTest() throws KeywordException
	{
		KeywordFinder kf = new implKeywordFinder();
		XMLContainer configData = new implXMLContainer();
		ConversationLogIterator cli = TestDataGenerator.getInstance().getConversation();

		try {
			// load training data	
			kf.start(configData, cli);
		} catch (Exception e) {
			throw new KeywordException("Encountered problem starting the keyword finder: ", e);
		}
		
		Thread t = new Thread(kf);
		t.start();
		
		KeywordContainer kc = new KeywordContainer(this);
		for(int i=0;i<2;i++) {
			try {
				Thread.sleep(5000);	
				System.out.println("\nWAKING UP AFTER KICKING OFF KEYWORD FINDER");	
				kf.signal(kc);
			} catch (InterruptedException e) {
				
			} catch (Exception e) {
				throw new KeywordException("Encountered problem requesting keywords: ", e);
			}
		}		
	}

	private void init() throws KeywordException
	{
		implXMLContainer configData = new implXMLContainer();
	}

	public static void main(String[] args) {
		TestStub testStub = new TestStub();
		try {
			testStub.runTest();
		} catch (KeywordException e) {
			e.printStackTrace();
		}
	}
}
