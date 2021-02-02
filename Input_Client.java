package assignment3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;   
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Input_Client {

public static void main(String[] args) throws LineUnavailableException, InterruptedException {
	
	/**
	 * This records data entered from a microphone and immediately plays it back
	 * 
	 * @author Brian Kelly
	 * @since 6-22-2020
	 * @version 2.0
	 */
	
	//Code Used to Find port in cmd.exe: netstat -ano | findstr :1000
	//Code used to kill port: taskkill /PID _______ /F
	
	
	//This will take the raw data from my microphone and than format it as assigned
	//The format is a wave file (PCM Signed), than it is taking the highest sample rate, 16 bits as a sample size, 2 channels (stereo), 
	//frame size of 4 bytes, and a frame rate the size of the sample rate
    AudioFormat formatWave = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
    
    //Target Data Lines will capture the audio
    TargetDataLine microphone;
    
  //Source Data Lines will read the audio and play the files back
    SourceDataLine speakers;
    
    microphone = AudioSystem.getTargetDataLine(formatWave);

    
    //Given the class argument and passing the desired audio format
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, formatWave);
    microphone = (TargetDataLine) AudioSystem.getLine(info);
    microphone.open(formatWave);

    //Byte Array does not have a fixed length and has an adjustable buffer
    ByteArrayOutputStream audioStream = new ByteArrayOutputStream();
    microphone.start();

    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, formatWave);
    speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
    speakers.open(formatWave);
    speakers.start();

    try {
    	//create the connection and then send data, every packet needs to know where it is going. 
        //A socket is an end point for communication between two machines
        @SuppressWarnings("resource")
        //This is used to connect to the Server established, a socket is the sending or receiving point
		DatagramSocket serverSocket = new DatagramSocket(1000);
        while (true) {

            byte[] buffer = new byte[1024];
            
			//Datagram constructor requires content and content length
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            
            //This is the data received from the server and then executed
            serverSocket.receive(response);

            audioStream.write(response.getData(), 0, response.getData().length);
            speakers.write(response.getData(), 0, response.getData().length);
            String output = new String(buffer, 0, response.getLength());

            System.out.println(output);
            System.out.println();

            //Thread goes to sleep after 10 seconds
            Thread.sleep(1000);
            
        }

    } catch (SocketTimeoutException ex) {
        System.out.println("Timeout error: " + ex.getMessage());
        ex.printStackTrace();
    } catch (IOException ex) {
        System.out.println("Client error: " + ex.getMessage());
        ex.printStackTrace();
    }catch (InterruptedException ex) {
        ex.printStackTrace();
    }
}}