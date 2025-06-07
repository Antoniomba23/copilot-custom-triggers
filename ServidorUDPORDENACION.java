
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ServidorUDPORDENACION {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java ServidorUDP <puerto>");
            return;
        }

        int puerto = Integer.parseInt(args[0]);
        DatagramSocket tempSocket = null;

        // Verificar disponibilidad del puerto
        while (tempSocket == null) {
            try {
                tempSocket = new DatagramSocket(puerto);
                System.out.println("Iniciando servidor en el puerto " + puerto + "...");
                System.out.println("Puerto libre. Creando socket en el puerto " + puerto + "...");
            } catch (SocketException e) {
                System.out.println("Puerto ocupado, esperando...");
                try { Thread.sleep(1000); }
                catch (InterruptedException ex) { /* Ignorar */ }
            }
        }

        final DatagramSocket socket = tempSocket;

        System.out.println("Esperando datagramas...");
        byte[] buffer = new byte[1024];

        while (true) {
            try {
                // Recibir datagrama
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                // Mostrar información del cliente
                String ipCliente = paquete.getAddress().getHostAddress();
                int puertoCliente = paquete.getPort();
                System.out.println("Recibido datagrama del host [" + ipCliente + ":" + puertoCliente + "]");

                // Procesar en hilo separado
                new Thread(() -> procesarPeticion(socket, paquete)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void procesarPeticion(DatagramSocket socket, DatagramPacket paquete) {
        try {
            String ipCliente = paquete.getAddress().getHostAddress();
            int puertoCliente = paquete.getPort();

            System.out.println("Procesando petición del host [" + ipCliente + ":" + puertoCliente + "]");

            // Extraer datos
            String datos = new String(paquete.getData(), 0, paquete.getLength());
            if (!datos.startsWith("#") || !datos.endsWith("#")) {
                System.err.println("Formato inválido: " + datos);
                return;
            }

            // Procesar números
            String numerosStr = datos.substring(1, datos.length() - 1);
            String[] partes = numerosStr.split(",");
            int[] numeros = new int[partes.length];

            for (int i = 0; i < partes.length; i++) {
                numeros[i] = Integer.parseInt(partes[i]);
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
            System.out.println("Enviado datagrama al host [" + ipCliente + ":" + puertoCliente + "]");
            System.out.println("Datagrama: " + respuesta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
