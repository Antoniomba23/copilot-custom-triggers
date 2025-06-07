//ServidorUDPFICHERO
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ServidorUDPFICHERO {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Uso: java ServidorUDP <puerto>");
            return;
        }

        int puerto = Integer.parseInt(args[0]);
        DatagramSocket socket = null;
        byte[] buffer = new byte[65507]; // Tamaño máximo UDP

        // Verificar puerto
        while (socket == null) {
            try {
                socket = new DatagramSocket(puerto);
                System.out.println("Servidor UDP iniciado en puerto " + puerto);
            } catch (SocketException e) {
                System.out.println("Puerto ocupado, esperando...");
                try { Thread.sleep(1000); } 
                catch (InterruptedException ex) { /* Ignorar */ }
            }
        }

        while (true) {
            try {
                // Recibir archivo
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                System.out.println("Cliente conectado: " + paquete.getAddress());
                
                // Guardar archivo recibido
                byte[] datos = Arrays.copyOf(paquete.getData(), paquete.getLength());
                FileOutputStream fos = new FileOutputStream("temp.pdf");
                fos.write(datos);
                fos.close();
                System.out.println("PDF recibido (" + datos.length + " bytes)");

                // Procesamiento (espera 5s)
                System.out.println("Procesando fichero...");
                Thread.sleep(5000);

                // Enviar de vuelta
                File file = new File("temp.pdf");
                FileInputStream fis = new FileInputStream(file);
                byte[] fileData = fis.readAllBytes();
                fis.close();
                
                DatagramPacket respuesta = new DatagramPacket(
                    fileData, 
                    fileData.length, 
                    paquete.getAddress(), 
                    paquete.getPort()
                );
                
                socket.send(respuesta);
                System.out.println("Archivo enviado como OriginalProcesado.pdf");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}