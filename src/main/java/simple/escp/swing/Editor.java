package simple.escp.swing;

import simple.escp.data.DataSource;
import simple.escp.data.EmptyDataSource;
import simple.escp.data.JsonDataSource;
import simple.escp.json.JsonTemplate;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.PlainDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * A simple editor that uses template and data source in form of JSON string.
 */
public class Editor extends JFrame {

    private static final int DEFAULT_FONT_SIZE = 12;
    private static final Color EDITOR_BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final int TAB_SIZE = 4;
    public static final double SPLIT_WEIGHT = 0.85;

    private Font editorFont;
    private JTabbedPane tabbedPane;
    private PrintPreviewPane printPreviewPane;
    private JEditorPane templateEditor;
    private JEditorPane dataSourceEditor;
    private JTextArea consoleOutput;
    private JButton clearConsoleButton;

    /**
     * Create this <code>Editor</code>.
     */
    public Editor() {
        super("simple-escp Editor");

        try {
            editorFont = Font.createFont(Font.TRUETYPE_FONT, Thread.currentThread().getContextClassLoader().
                getResourceAsStream("DejaVuSansMono.ttf")).deriveFont(Font.PLAIN, DEFAULT_FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            editorFont = new Font(Font.MONOSPACED, Font.PLAIN, DEFAULT_FONT_SIZE);
        }

        printPreviewPane = new PrintPreviewPane();
        templateEditor = createEditor();
        dataSourceEditor = createEditor();
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new TabbedPaneChange());
        tabbedPane.addTab("Template Editor", new JScrollPane(templateEditor));
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.addTab("Data Source Editor", new JScrollPane(dataSourceEditor));
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        tabbedPane.addTab("Preview", printPreviewPane);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        ErrorConsoleStream consoleStream = new ErrorConsoleStream();
        PrintStream consolePrintStream = null;
        try {
            consolePrintStream = new PrintStream(consoleStream, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(null, "Can't create console stream: " + e.getMessage());
        }
        System.setOut(consolePrintStream);
        System.setErr(consolePrintStream);
        JPanel consoleArea = new JPanel();
        consoleArea.setLayout(new BorderLayout());
        JPanel consoleArea_buttons = new JPanel();
        consoleArea_buttons.setLayout(new FlowLayout(FlowLayout.LEADING));
        consoleArea_buttons.add(new JLabel("Console Output  "));
        clearConsoleButton = new JButton("Clear");
        clearConsoleButton.addActionListener(new ClearConsoleAction());
        consoleArea_buttons.add(clearConsoleButton);
        consoleArea.add(consoleArea_buttons, BorderLayout.PAGE_START);
        consoleArea.add(new JScrollPane(consoleOutput), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, consoleArea);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(SPLIT_WEIGHT);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Create editor.
     *
     * @return an instance of <code>JEditorPane</code>.
     */
    private JEditorPane createEditor() {
        JEditorPane editor = new JEditorPane();
        editor.setFont(editorFont);
        editor.getDocument().putProperty(PlainDocument.tabSizeAttribute, TAB_SIZE);
        UIDefaults defaults = new UIDefaults();
        defaults.put("EditorPane[Enabled].backgroundPainter", EDITOR_BACKGROUND_COLOR);
        editor.putClientProperty("Nimbus.Overrides", defaults);
        editor.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        editor.setBackground(EDITOR_BACKGROUND_COLOR);
        return editor;
    }

    /**
     * Everything starts from this method.
     *
     * @param args the arguments passed from command line.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't use Nimbus Look And Feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Editor editor = new Editor();
                    editor.pack();
                    editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    editor.setVisible(true);
                    editor.setExtendedState(JFrame.MAXIMIZED_BOTH);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * This is an event handler that will refresh the preview content when user switched to that tab.
     */
    private class TabbedPaneChange implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (tabbedPane.getSelectedIndex() == 2) {
                JsonTemplate template = new JsonTemplate(templateEditor.getText());
                String jsonSource = dataSourceEditor.getText().trim();
                DataSource ds = "".equals(jsonSource) ? new EmptyDataSource() : new JsonDataSource(jsonSource);
                printPreviewPane.display(template, ds);
            }
        }

    }

    /**
     * This <code>OutputStream</code> will capture stream and display it in text area.
     */
    private class ErrorConsoleStream extends OutputStream {

        private StringBuffer buffer = new StringBuffer();

        @Override
        public void write(int b) throws IOException {
            buffer.append((char) b);
        }

        @Override
        public void flush() throws IOException {
            consoleOutput.append(buffer.toString());
            buffer = new StringBuffer();
        }
    }

    /**
     *  This action will clear the content of console text area.
     */
    private class ClearConsoleAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            consoleOutput.setText(null);
        }

    }


}
