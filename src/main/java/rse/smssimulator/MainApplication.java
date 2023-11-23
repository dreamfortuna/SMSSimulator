package rse.smssimulator;
	
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import javafx.scene.image.Image;
import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class MainApplication extends Application {
	private static final Logger logger = Logger.getLogger(MainApplication.class);
	private static FileOutputStream fileLocker;
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader=new FXMLLoader(getClass().getResource("/mainView.fxml"));
			Parent root = loader.load();//��ȡFXML
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("外挂物仿真系统模拟器");
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image("file:SMSS.png"));
			MainController simulatorPane=loader.getController();
			simulatorPane.init(primaryStage);
			primaryStage.show();
		    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		        @Override
		        public void handle(WindowEvent event) {
		        	System.exit(0);
		        }
		      });
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(instanceLock()) {
			logger.info("只能运行一个模拟器实例！");
			System.exit(0);
		}
		launch(args);
	}
    private static boolean instanceLock() {
    	try {
    		fileLocker = new FileOutputStream("locker");
			FileLock lck = fileLocker.getChannel().tryLock();
			if(lck == null) {
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    	return false;
    }
}
