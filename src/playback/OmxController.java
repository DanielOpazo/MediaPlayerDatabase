package playback;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Scanner;

import database.CoreDataAccess;

public class OmxController {
	
	public StreamWrapper getStreamWrapper(InputStream is, String type){
	            return new StreamWrapper(is, type);
	}
	private class StreamWrapper extends Thread {
	    InputStream is = null;
	    String type = null;          
	    String message = null;
	 
	    public String getMessage() {
	            return message;
	    }
	 
	    StreamWrapper(InputStream is, String type) {
	        this.is = is;
	        this.type = type;
	    }
	 
	    public void run() {
	        try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            StringBuffer buffer = new StringBuffer();
	            String line = null;
	            while ( (line = br.readLine()) != null) {
	                buffer.append(line);//.append("\n");
	            }
	            message = buffer.toString();
	        } catch (IOException ioe) {
	            ioe.printStackTrace();  
	        }
	    }
	}

	public void executeOMXcmd(String command) {
		Runtime rt = Runtime.getRuntime();
        OmxController rte = new OmxController();
        StreamWrapper error, output;
 
        try {
            Process proc = rt.exec(command);
            error = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
            output = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;
 
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
            System.out.println("Output: "+output.message+"\nError: "+error.message);
        } catch (IOException e) {
                    e.printStackTrace();
        } catch (InterruptedException e) {
                    e.printStackTrace();
        }
	}
	
	public void startOMX(String filePath) {
		Runtime rt = Runtime.getRuntime();
        OmxController rte = new OmxController();
        StreamWrapper error, output;
 
        try {
            Process proc = rt.exec("omxplayer /home/pi/media_storage/05\\ -\\ Sunday\\ Morning.mp3");
            error = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
            output = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;
 
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
            System.out.println("Output: "+output.message+"\nError: "+error.message);
        } catch (IOException e) {
                    e.printStackTrace();
        } catch (InterruptedException e) {
                    e.printStackTrace();
        }
	}
	public static void main(String[] args) {
		OmxController oc = new OmxController();
		CoreDataAccess cda = new CoreDataAccess();
		String filePath = cda.getFilePathForSong(10);
		System.out.println(filePath);
		filePath = filePath.replaceAll("(\\s)", "\\\\ ");
		System.out.println(filePath);
		//oc.startOMX(filePath);
		//ProcessBuilder pb = new ProcessBuilder("bash", "-c", "omxplayer " + filePath);
		Scanner scan = new Scanner(System.in);
		
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/omxplayer", filePath);
		pb.redirectErrorStream(true);
		 try {
			Process p = pb.start();
			OutputStream stdin = p.getOutputStream();
			InputStream stdout = p.getInputStream();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
			
			//writer.write(filePath);
			//writer.flush();
			//writer.write("p");
			//writer.flush();
			//writer.write("p");
			/*
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Waited 3 seconds. attempting to pause omxplayer");
			writer.write("p");
			writer.flush();
			*/
			while (scan.hasNext()) {
				String input = scan.nextLine();
				writer.write("((" + input + ") && echo --EOF-- ) || echo --EOF--\n");
				writer.flush();
				
				line = reader.readLine();
				while (line != null && ! line.trim().equals("--EOF--")) {
					System.out.println("Stdout: " + line);
					line = reader.readLine();
				}
				if (line == null) {
					break;
				}
			}
			
			/*
			BufferedOutputStream bis = new BufferedOutputStream(p.getOutputStream());
			try {
			Thread.sleep(4);
			bis.write("p".getBytes());
			Thread.sleep(2);
			bis.write("p".getBytes());
			}catch (Exception e) {}
			bis.close();
			*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
