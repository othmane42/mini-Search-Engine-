package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FileDirectory;

/**
 *
 * @author otmane42
 */
public class DirectoryWatcher implements Runnable,Serializable {

    public static int TIME_OUT = 5;
    private Thread thread;
    private  AtomicBoolean running = new AtomicBoolean(false);
    private FileDirectory fileDir;
    private HashMap<WatchKey,Path> map=new HashMap<>();
    
  //  private DirectoryDataBaseManager dirManager;
    public DirectoryWatcher(FileDirectory fileDir){//, DirectoryDataBaseManager dirManager) {
        this.fileDir = fileDir;
 //       this.dirManager = dirManager;
//        startWatch();
    }

    public void stopWatch() {
        running.set(false);
        if(thread!=null) 
            thread.interrupt();
    }

    public void startWatch() {
        thread = new Thread(this);
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        if (!running.get()) {
            running.set(true);
            watch();
        }

    }

    public void stop() {
    }

    private void watch() {
        try {
            Path path = Paths.get(fileDir.getPath());
            FileSystem fileSystem = path.getFileSystem();
            WatchService newWatchService = fileSystem.newWatchService();
            path.register(newWatchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            System.out.println("watching path : " + fileDir.getPath());
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    register(dir,newWatchService);
                    return FileVisitResult.CONTINUE;
                }

            
            });
            while (running.get()) {
                WatchKey take = newWatchService.take();
                for (WatchEvent<?> event : take.pollEvents()) {
                    processEvent(event,take);

                }
                if (!take.reset()) {
                    break;
                }

            }
            System.out.println("stopped ");

        } catch (IOException ex) {
            Logger.getLogger(DirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("thread stopped");
            //  Logger.getLogger(DirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void register(Path dir, WatchService newWatchService) {
        try {
            WatchKey key = dir.register(newWatchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            map.put(key,dir);
        } catch (IOException ex) {
            Logger.getLogger(DirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    }
          
    
    private void processEvent(WatchEvent<?> event,WatchKey key) {
        Path rootPath = map.get(key);
        System.out.println("count event :" +event.count());
        Path path = (Path) event.context();
        
        System.out.println("abs  path :"+rootPath.resolve(path).toAbsolutePath());
        System.out.print("got an event ! ");
        WatchEvent.Kind<?> eventKind = event.kind();
        if (eventKind == ENTRY_CREATE) {
            System.out.println("entry create : path " + path);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            DirectoryDataBaseManager.getInstance().addFile(rootPath.resolve(path).toAbsolutePath().toString(), true);
        } else if (eventKind == ENTRY_MODIFY) {
            System.out.println("entry modify : path " + path);
            DirectoryDataBaseManager.getInstance().updateFile(rootPath.resolve(path).toAbsolutePath().toString());

        } else if (eventKind == ENTRY_DELETE) {
            System.out.println("entry delete : path " + path);
            //call to weka
            DirectoryDataBaseManager.getInstance().deleteFile(rootPath.resolve(path).toAbsolutePath().toString(), true);
        }

    }
    
  
}
