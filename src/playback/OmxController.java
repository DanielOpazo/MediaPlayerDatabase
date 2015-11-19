package playback;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

	public static void executeOMXcmd(String command) {
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
	public static void startOMX(String filepath) {
		Runtime rt = Runtime.getRuntime();
        OmxController rte = new OmxController();
        StreamWrapper error, output;
 
        try {
            Process proc = rt.exec("omxplayer " + "/home/pi/sftp_dump/Boat.mp4");
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
		startOMX(null);
	}
}
