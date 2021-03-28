import java.io.*;
import java.net.*;

class UDPClient {
  public static void main(String args[]) throws Exception {
    try {
      String serverHostname = new String("18.224.220.128");

      if (args.length > 0)
        serverHostname = args[0];

      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

      DatagramSocket clientSocket = new DatagramSocket();

      InetAddress IPAddress = InetAddress.getByName(serverHostname);
      System.out.println("Attemping to connect to " + IPAddress + ") via UDP port 6000");

      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];

      System.out.print("Enter Message: ");
      String sentence = inFromUser.readLine();
      sendData = sentence.getBytes();
      long averageRTT = 0; // var para o averageRTT -- tive que usar long porcausa do metodo de tempo usado
      int packageRate = 0; // contador de Pacotes enviado e recebido com sucesso
      for (int i = 0; i < 10; i++) { // enviar mensagem 10 vezes

        System.out.print("\n[" + (i + 1) + "]" + "Sending data to " + sendData.length + " bytes to server. -- ");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6000);

        clientSocket.send(sendPacket);
        long startTime = System.currentTimeMillis(); // tempo de inicio de envio

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        // System.out.println("Waiting for return packet");
        clientSocket.setSoTimeout(250); // definido para 250ms

        try {
          clientSocket.receive(receivePacket);
          long rtt = System.currentTimeMillis() - startTime; // recebeu o pacote
          String modifiedSentence = new String(receivePacket.getData());

          InetAddress returnIPAddress = receivePacket.getAddress();

          int port = receivePacket.getPort();

          System.out.print("From server at: " + returnIPAddress + ":" + port + "-- ");
          System.out.println("Message: " + modifiedSentence + " -- Time: " + rtt + "ms");

          packageRate++; // pacote enviado e recebido com sucesso
          averageRTT += rtt;// calculo parcial do RTT MEDIO
          Thread.sleep(1000); // espera 1 segundo para enviar proximo pacote

        } catch (SocketTimeoutException ste) {
          System.out.println("Timeout Occurred: Packet assumed lost");
        }
      }

      System.out.println("\n10 pacotes transmitidos -- " + packageRate + " pacotes recebidos -- "
          + (100 - (packageRate * 10)) + "% de perda de pacote ");
      System.out.println("RTT MEDIO: " + averageRTT / packageRate + "ms"); // calcular media connsiderando tempo de
      // pacotes
      // recebido com sucesso

      clientSocket.close();
    } catch (UnknownHostException ex) {
      System.err.println(ex);
    } catch (IOException ex) {
      System.err.println(ex);
    }
  }
}
