import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.CloseStatus;
import com.teamdev.jxbrowser.chromium.FileChooserMode;
import com.teamdev.jxbrowser.chromium.FileChooserParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import java.awt.BorderLayout;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to register your DialogHandler and
 * override the functionality that displays file chooser when
 * user uploads file using INPUT TYPE="file" HTML element on a web page.
 */
public class FileUploadExample {
    public static void main(String[] args) {
        Browser browser = new Browser();
        final BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setDialogHandler(new DefaultDialogHandler(view) {
            @Override
            public CloseStatus onFileChooser(final FileChooserParams params) {
                final AtomicReference<CloseStatus> result = new AtomicReference<CloseStatus>(
                        CloseStatus.CANCEL);

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            if (params.getMode() == FileChooserMode.Open) {
                                JFileChooser fileChooser = new JFileChooser();
                                if (fileChooser.showOpenDialog(view)
                                        == JFileChooser.APPROVE_OPTION) {
                                    File selectedFile = fileChooser.getSelectedFile();
                                    params.setSelectedFiles(selectedFile.getAbsolutePath());
                                    result.set(CloseStatus.OK);
                                }
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                return result.get();
            }
        });
        browser.loadURL("http://www.cs.tut.fi/~jkorpela/forms/file.html");
    }
}