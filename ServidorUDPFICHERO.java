import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorUDPFICHERO {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java ServidorUDP <puerto>");
            return;
        }
        
        int puerto = Integer.parseInt(args[0]);
        DatagramSocket socket = null;

        // Verificar y esperar si el puerto está ocupado
        while (socket == null) {
            try {
                socket = new DatagramSocket(puerto);
                System.out.println("Puerto libre. Creando socket en el puerto " + puerto + "...");
            } catch (SocketException e) {
                System.out.println("Puerto ocupado ...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Esperando conexiones de clientes...");
        byte[] buffer = new byte[65507]; // Tamaño máximo para UDP

        try {
            while (true) {
                // Recibir datagrama
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                
                // Mostrar información del cliente
                InetAddress direccionCliente = paquete.getAddress();
                int puertoCliente = paquete.getPort();
                System.out.println("Cliente conectado desde " + direccionCliente.getHostAddress());
                
                // Obtener datos del archivo
                byte[] datosRecibidos = Arrays.copyOf(paquete.getData(), paquete.getLength());
                System.out.println("Recibiendo datagrama con archivo PDF...");
                System.out.println("Fichero recibido: original.pdf");
                
                // Procesamiento con espera de 10 segundos
                System.out.println("Procesando fichero...");
                Thread.sleep(10000);
                
                // Enviar archivo renombrado
                System.out.println("Archivo procesado. Enviando datagrama de datos al cliente...");
                DatagramPacket paqueteRespuesta = new DatagramPacket(
                    datosRecibidos,
                    datosRecibidos.length,
                    direccionCliente,
                    puertoCliente
                );
                socket.send(paqueteRespuesta);
                
                System.out.println("Fichero \"OriginalProcesado.pdf\" enviado correctamente al cliente " + 
                                  direccionCliente.getHostAddress());
                System.out.println("Finalizando comunicación con el cliente...");
                System.out.println("Esperando nuevas conexiones...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}