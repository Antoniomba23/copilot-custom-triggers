//ClienteUDPORDENACION
import java.io.IOException;
import java.net.*;
import java.util.Random;

public class ClienteUDPORDENACION {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java ClienteUDP <puerto-inicial> <cantidad-elementos>");
            return;
        }

        int puertoInicial = Integer.parseInt(args[0]);
        int cantidad = Integer.parseInt(args[1]);
        
        // Validar cantidad
        if (cantidad < 15 || cantidad > 30) {
            System.out.println("Error: La cantidad debe estar entre 15 y 30");
            return;
        }
        
        int puertoActual = puertoInicial;
        DatagramSocket socket = null;
        
        // Buscar puerto libre
        while (socket == null) {
            try {
                socket = new DatagramSocket(puertoActual);
                System.out.println("Puerto " + puertoActual + " libre.");
                System.out.println("Creando socket en el puerto " + puertoActual + "...");
            } catch (SocketException e) {
                System.out.println("Puerto " + puertoActual + " ocupado, seleccionando siguiente puerto libre...");
                puertoActual++;
            }
        }
        
        System.out.println("Cliente arrancado por puerto " + puertoActual + ".");
        
        try {
            // Generar números aleatorios
            System.out.println("Generando array de " + cantidad + " elementos aleatorios...");
            int[] numeros = generarNumerosAleatorios(cantidad);
            
            // Mostrar array generado
            System.out.print("Array generado: ");
            for (int i = 0; i < numeros.length; i++) {
                System.out.print(numeros[i]);
                if (i < numeros.length - 1) System.out.print(",");
            }
            System.out.println();
            
            // Construir mensaje
            StringBuilder mensaje = new StringBuilder("#");
            for (int i = 0; i < numeros.length; i++) {
                mensaje.append(numeros[i]);
                if (i < numeros.length - 1) mensaje.append(",");
            }
            mensaje.append("#");
            
            // Enviar al servidor
            byte[] datosEnvio = mensaje.toString().getBytes();
            InetAddress direccion = InetAddress.getByName("localhost");
            DatagramPacket paqueteEnvio = new DatagramPacket(
                datosEnvio, 
                datosEnvio.length, 
                direccion, 
                40000  // Puerto del servidor
            );
            
            System.out.println("Enviando datagrama...");
            socket.send(paqueteEnvio);
            
            // Recibir respuesta
            byte[] buffer = new byte[1024];
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length);
            System.out.println("Esperando respuesta del servidor...");
            socket.receive(paqueteRespuesta);
            
            // Procesar respuesta
            String respuesta = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength());
            System.out.println("Datagrama recibido. Array ordenado:");
            
            if (respuesta.startsWith("#") && respuesta.endsWith("#")) {
                String numerosOrdenados = respuesta.substring(1, respuesta.length() - 1);
                System.out.println(numerosOrdenados);
            } else {
                System.out.println("Respuesta inválida: " + respuesta);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    
    private static int[] generarNumerosAleatorios(int cantidad) {
        int[] numeros = new int[cantidad];
        Random rand = new Random();
        
        for (int i = 0; i < cantidad; i++) {
            numeros[i] = rand.nextInt(500);  // Números entre 0-499
        }
        
        return numeros;
    }
}