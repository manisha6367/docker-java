package assignment3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Server_Output {

public static void main(String[] args) throws IOException {
	
	/**
	 * This records data entered from a microphone and immediately plays it back
	 * 
	 * @author Brian Kelly
	 * @since 6-22-2020
	 * @version 2.0
	 */

	//This will take the raw data from my microphone and than format it as assigned
	//The format is a wave file (PCM Signed), than it is taking the highest sample rate, 16 bits as a sample size, 2 channels (stereo), 
	//frame size of 4 bytes, and a frame rate the size of the sample rate
    AudioFormat formatWave = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
   
    //Target Data Lines will capture the audio
    TargetDataLine microphone;
    
    //Source Data Lines will read the audio and play the files back
    SourceDataLine speakers;
    try {
        microphone = AudioSystem.getTargetDataLine(formatWave);

      //Given the class argument and passing the desired audio format
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, formatWave);
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(formatWave);

     	int bytes_Read;
        int byte_Syze = 1024;
        
      //Dividing by 5 will pull small segments at a time and be more efficient
      //Returns the actual buffer size used for the response
        byte[] data = new byte[microphone.getBufferSize() / 5];
        microphone.start();

        //This establishes and receives the parameters we need for including the format
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, formatWave);
        speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        speakers.open(formatWave);
        speakers.start();


        // Configure the ip and port
        String hostname = "localhost";
        int port = 1000;

      //INetAddress Returns the address of the local host. This is achieved by retrieving the name of the host from the system, then resolving that name into an InetAddress.
        InetAddress address = InetAddress.getByName(hostname);
        
      //create the connection and then send data, every packet needs to know where it is going. 
      //A socket is an end point for communication between two machines
        @SuppressWarnings("resource")
		DatagramSocket socket = new DatagramSocket();
        //This creates an infinite For-Loop
        for(;;) {               
        	
        	bytes_Read = microphone.read(data, 0, byte_Syze);
            
        	//this writes the data received the from microphone into the byteArray
            speakers.write(data, 0, bytes_Read); 
            
            //this outputs the data read from 'microphone' and sends the information back to the client
            DatagramPacket request = new DatagramPacket(data,bytes_Read, address, port);
            socket.send(request);

        }

    } catch (LineUnavailableException e) {
        e.printStackTrace();
    } 
}}