import java.io.*;
import javax.swing.JPanel;

public interface ProxyFilter {
    public File process(File in) throws IOException;
    public ProxyFilterSettings getSettingsGUI();
    public String getContentType();
}
