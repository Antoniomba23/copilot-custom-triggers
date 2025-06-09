import java.io.*;
import java.net.*;
import java.util.Random;

public class ClienteTCPORDENACION {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ClienteTCP <puerto_inicial> <cantidad_numeros>");
            return;
        }
        
        int puertoInicial = Integer.parseInt(args[0]);
        int cantidad = Integer.parseInt(args[1]);
        
        
        // Validar cantidad
        if (cantidad < 15 || cantidad > 30) {
            System.out.println("Error: La cantidad debe estar entre 15 y 30");
            return;
        }
        
        Socket socket = null;
        int puertoActual = puertoInicial;
        
        // Buscar puerto libre
        while (socket == null) {
            try {
                socket = new Socket();
                socket.bind(new InetSocketAddress(puertoActual));
                System.out.println("Puerto " + puertoActual + " libre. Creando socket...");
            } catch (IOException e) {
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
            // Conectar al servidor
            socket.connect(new InetSocketAddress("localhost", 50000));
            
            // Construir mensaje
            StringBuilder mensaje = new StringBuilder("#");
            for (int i = 0; i < numeros.length; i++) {
                mensaje.append(numeros[i]);
                if (i < numeros.length - 1) mensaje.append(",");
            }
            mensaje.append("#");
            
            // Enviar datos
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Enviando datos...");
            dos.writeUTF(mensaje.toString());
            
            // Recibir respuesta
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            System.out.println("Esperando respuesta del servidor...");
            String respuesta = dis.readUTF();
            
            // Procesar respuesta
            System.out.println("Datos recibidos.");
            if (respuesta.startsWith("#") && respuesta.endsWith("#")) {
                String numerosOrdenados = respuesta.substring(1, respuesta.length() - 1);
                System.out.println("Array ordenado: " + numerosOrdenados);
            } else {
                System.out.println("Respuesta en formato incorrecto: " + respuesta);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}