package psl.memento.server.frax;

public class FraxException extends Exception {
  public FraxException(String iMessage) {
    super(iMessage);
  }
  
  public FraxException(String iMessage, Throwable iCause) {
    super(iMessage, iCause);
  }
}