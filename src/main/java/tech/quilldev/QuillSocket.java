package tech.quilldev;

import java.io.IOException;
import java.net.Socket;

public class QuillSocket extends Socket{

    //the time since the last ping
    public long lastping = 0;

    //the socket we're using for printing data etc.
    private final Socket socket;

    /**
     * Constructor for a new QuillSocket
     * @param socket to base the quillsocket on
     */
    public QuillSocket(Socket socket){
        this.socket = socket;
        this.updateLastPing();

    }

    /**
     * Read the socket asyncronously (wrapped in a thread)
     */
    public void readSocketAsync(){
        new Thread( () -> {
            readSocketSync();
        }).start();
    }

    /**
     * Return whether the socket is alive
     */
    public boolean alive(){
        try {
            var stream = this.socket.getOutputStream();

            //write the bytes to this sockets string
            stream.write("{QS:KEEP_ALIVE}\n".getBytes());
        }
        catch(IOException ignored){
            return false;
        }

        //if the socket is closed, return false
        if(this.isClosed()){
            return false;
        }

        return true;
    }
    /**
     * Read data from the socket syncronously
     */
    public void readSocketSync(){
        try {
            var stream = this.socket.getInputStream();
            var available = stream.available();

            //if there are no bytes available exit
            if(available == 0){
                return;
            }

            //get the bytes from the stream
            var bytes = stream.readNBytes(available);

            //create the string builder for the bytes
            var byteStringBuilder = new StringBuilder();

            //create strings from the bytes
            for(var b : bytes){

                //turn the byte into a character
                byteStringBuilder.append((char) b);
            }

            //create the data string from the byte string builder
            var dataString = byteStringBuilder.toString();

            //get all commands from the data string
            var commands = dataString.split("\n");

            //print all of the commands
            for(var command : commands){

                //close the socket if we do the end socket packet protocol
                if(command.contains("{QP:ES}")){
                    this.close();
                    return;
                }
                System.out.println(command);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Write a line to the socket syncronously
     * @param data the data to send
     */
    public void writeLineSync(String data){
        try {
            var stream = this.socket.getOutputStream();
            var packet = data + "\n";

            //write the bytes to this sockets string
            stream.write(packet.getBytes());
        }
        catch(IOException e){
            //close the socket
            this.close();

            //print the first exceptions stack trace
            e.printStackTrace();
        }
    }

    /**
     * Close the socket and handle exceptions locally
     */
    @Override
    public void close(){

        //if the socket is already closed, dw about it 
        if(this.isClosed()){
            return;
        }

        //try to close the socket 
        try {
            super.close();
        }
        catch (IOException ignored){}
    }

    public void isAlive(){

    }
    /**
     * Write a line to the output string asyncronously
     * @param data to write to the line
     */
    public void writeLineAsync(String data){
        new Thread( () -> {
            writeLineSync(data);
        }).start();
    }

    /**
     * Update the last ping time
     */
    public void updateLastPing(){
        this.lastping = System.currentTimeMillis();
    }

    /**
     * Get a "pretty" address for printing
     */
    public String getAddress(){
        return socket.getInetAddress().toString() + ":" + socket.getPort();
    }
}
