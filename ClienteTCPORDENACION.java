//ClienteTCPORDENACION
import java.io.*;
import java.net.*;
import java.util.Random;

public class ClienteTCPORDENACION {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso: java ClienteTCP <puerto-servidor> <puerto-local> <cantidad-elementos>");
            return;
        }

        int puertoServidor = Integer.parseInt(args[0]);
        int puertoLocal = Integer.parseInt(args[1]);
        int cantidad = Integer.parseInt(args[2]);
        
        // Validar cantidad
        if (cantidad < 15 || cantidad > 30) {
            System.out.println("Error: La cantidad debe estar entre 15 y 30");
            return;
        }
        
        Socket socket = null;
        int puertoActual = puertoLocal;
        
        // Buscar puerto local libre
        while (socket == null) {
            try {
                // Intentar crear socket vinculado al puerto local
                socket = new Socket();
                socket.bind(new InetSocketAddress("localhost", puertoActual));
                System.out.println("Puerto " + puertoActual + " libre.");
                System.out.println("Creando socket en el puerto " + puertoActual + "...");
            } catch (IOException e) {
                System.out.println("Puerto " + puertoActual + " ocupado, seleccionando siguiente puerto libre...");
                puertoActual++;
            }
        }
        
        System.out.println("Cliente arrancado por puerto " + puertoActual + ".");
        
        try {
            // Conectar al servidor
            socket.connect(new InetSocketAddress("localhost", puertoServidor));
            System.out.println("Conectado al servidor en puerto " + puertoServidor);
            
            // Obtener streams de entrada/salida
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
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
            System.out.println("Enviando datos...");
            out.println(mensaje.toString());
            
            // Recibir respuesta
            System.out.println("Esperando respuesta del servidor...");
            String respuesta = in.readLine();
            System.out.println("Respuesta recibida. Array ordenado:");
            
            if (respuesta != null && respuesta.startsWith("#") && respuesta.endsWith("#")) {
                String numerosOrdenados = respuesta.substring(1, respuesta.length() - 1);
                System.out.println(numerosOrdenados);
            } else {
                System.out.println("Respuesta inválida: " + respuesta);
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
    
    private static int[] generarNumerosAleatorios(int cantidad) {
        int[] numeros = new int[cantidad];
        Random rand = new Random();
        
        for (int i = 0; i < cantidad; i++) {
            numeros[i] = rand.nextInt(500);  // Números entre 0-499
        }
        
        return numeros;
    }
}