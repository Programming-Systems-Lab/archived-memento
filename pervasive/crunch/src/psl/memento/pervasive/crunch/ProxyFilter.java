package psl.memento.pervasive.crunch;

import java.io.*;

public interface ProxyFilter {
	public File process(File in) throws IOException;
	public ProxyFilterSettings getSettingsGUI();
	public String getContentType();
}
