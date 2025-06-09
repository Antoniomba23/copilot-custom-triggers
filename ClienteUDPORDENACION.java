import java.io.IOException;
import java.net.*;
import java.util.Random;

public class ClienteUDPORDENACION {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ClienteUDP <puerto_inicial> <cantidad_numeros>");
            return;
        }
        
        int puertoInicial = Integer.parseInt(args[0]);
        int cantidad = Integer.parseInt(args[1]);
        
        // Validar cantidad
        if (cantidad < 15 || cantidad > 30) {
            System.out.println("Error: La cantidad debe estar entre 15 y 30");
            return;
        }
        
        DatagramSocket socket = null;
        int puertoActual = puertoInicial;
        
        // Buscar puerto libre
        while (socket == null) {
            try {
                socket = new DatagramSocket(puertoActual);
                System.out.println("Puerto " + puertoActual + " libre. Creando socket...");
            } catch (SocketException e) {
                System.out.println("Puerto " + puertoActual + " ocupado, seleccionando siguiente puerto libre...");
                puertoActual++;
            }
        }
        
        System.out.println("Cliente arrancado por puerto " + puertoActual + ".");
        
        // Generar números aleatorios
        int[] numeros = new int[cantidad];
        Random rand = new Random();
        for (int i = 0; i < cantidad; i++) {
            numeros[i] = rand.nextInt(500); // Números entre 0-499
        }
        
        // Mostrar array generado
        System.out.print("Array generado: ");
        for (int i = 0; i < numeros.length; i++) {
            System.out.print(numeros[i]);
            if (i < numeros.length - 1) System.out.print(",");
        }
        System.out.println();
        
        try {
            // Construir mensaje
            StringBuilder mensaje = new StringBuilder("#");
            for (int i = 0; i < numeros.length; i++) {
                mensaje.append(numeros[i]);
                if (i < numeros.length - 1) mensaje.append(",");
            }
            mensaje.append("#");
            
            byte[] datos = mensaje.toString().getBytes();
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            
            // Enviar datagrama
            System.out.println("Enviando datagrama...");
            DatagramPacket paqueteEnvio = new DatagramPacket(
                datos,
                datos.length,
                direccionServidor,
                40000 // Puerto fijo del servidor
            );
            socket.send(paqueteEnvio);
            
            // Recibir respuesta
            byte[] buffer = new byte[1024];
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length);
            System.out.println("Esperando respuesta del servidor...");
            socket.receive(paqueteRespuesta);
            
            // Procesar respuesta
            String respuesta = new String(
                paqueteRespuesta.getData(),
                0,
                paqueteRespuesta.getLength()
            );
            
            System.out.println("Datagrama recibido.");
            if (respuesta.startsWith("#") && respuesta.endsWith("#")) {
                String numerosOrdenados = respuesta.substring(1, respuesta.length() - 1);
                System.out.println("Array ordenado: " + numerosOrdenados);
            } else {
                System.out.println("Respuesta en formato incorrecto: " + respuesta);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}