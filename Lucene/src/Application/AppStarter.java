package Application;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AppStarter {
	
	
	
	private static void deleteDirectoryContents(String directoryPath) throws IOException {
        Path directory = Paths.get(directoryPath);
        Files.walk(directory)
             .filter(Files::isRegularFile)
             .map(Path::toFile)
             .forEach(File::delete);
    }

	public static void main(String[] args) throws IOException { 
		AppUI rootFrame = new AppUI();
		rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rootFrame.setSize(1200, 800); 
		rootFrame.setVisible(true);     
		
    	Index app = new Index("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\index");
    	app.initializeMultipleArticle();
    	app.closeIndexWriter();
    	
    	
    	rootFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Application is closing.");
                try {
                    deleteDirectoryContents("C:\\Users\\giannis\\Documents\\8osemester\\InformationRetrival\\index");
                } catch (IOException er) {
                    er.printStackTrace();
                }
            }
        });
    	
    	
		
	}
	
	
	

}
