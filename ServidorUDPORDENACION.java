import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ServidorUDPORDENACION {
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
                System.out.println("Puerto ocupado, esperando...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Esperando datagramas...");

        try {
            while (true) {
                // Recibir datagrama
                byte[] buffer = new byte[1024];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                
                // Obtener información del cliente
                String direccionCliente = paquete.getAddress().getHostAddress() + ":" + paquete.getPort();
                System.out.println("Recibido datagrama del host " + direccionCliente);
                
                // Procesar en un hilo separado
                new Thread(new ProcesadorPeticion(socket, paquete, direccionCliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class ProcesadorPeticion implements Runnable {
        private final DatagramSocket socket;
        private final DatagramPacket paquete;
        private final String direccionCliente;
        
        public ProcesadorPeticion(DatagramSocket socket, DatagramPacket paquete, String direccionCliente) {
            this.socket = socket;
            this.paquete = paquete;
            this.direccionCliente = direccionCliente;
        }
        
        @Override
        public void run() {
            try {
                System.out.println("Procesando petición del host " + direccionCliente + "...");
                
                // Procesar datos
                String datos = new String(paquete.getData(), 0, paquete.getLength());
                if (!datos.startsWith("#") || !datos.endsWith("#")) {
                    System.out.println("Formato de datagrama incorrecto");
                    return;
                }
                
                // Extraer números
                String numerosStr = datos.substring(1, datos.length() - 1);
                String[] numerosArray = numerosStr.split(",");
                int[] numeros = new int[numerosArray.length];
                for (int i = 0; i < numerosArray.length; i++) {
                    numeros[i] = Integer.parseInt(numerosArray[i]);
                }
                
                // Ordenar array
                Arrays.sort(numeros);
                
                // Construir respuesta
                StringBuilder respuesta = new StringBuilder("#");
                for (int i = 0; i < numeros.length; i++) {
                    respuesta.append(numeros[i]);
                    if (i < numeros.length - 1) respuesta.append(",");
                }
                respuesta.append("#");
                
                // Enviar respuesta
                byte[] respuestaBytes = respuesta.toString().getBytes();
                DatagramPacket paqueteRespuesta = new DatagramPacket(
                    respuestaBytes,
                    respuestaBytes.length,
                    paquete.getAddress(),
                    paquete.getPort()
                );
                
                socket.send(paqueteRespuesta);
                System.out.println("Enviado datagrama al host " + direccionCliente);
                
            } catch (NumberFormatException e) {
                System.out.println("Error en formato de números");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}