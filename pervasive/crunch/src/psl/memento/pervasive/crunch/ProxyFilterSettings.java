import javax.swing.JPanel;

public abstract class ProxyFilterSettings extends JPanel{
    public abstract void commitSettings();
    public abstract void revertSettings();
    public abstract String getTabName();
}