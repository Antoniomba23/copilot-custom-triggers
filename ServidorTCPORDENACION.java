//ServidorTCPORDENACION
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ServidorTCPORDENACION {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java ServidorTCP <puerto>");
            return;
        }

        int puerto = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;

        // Verificar disponibilidad del puerto
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(puerto);
                System.out.println("Iniciando servidor en el puerto " + puerto + "...");
                System.out.println("Puerto libre. Creando socket en el puerto " + puerto + "...");
            } catch (IOException e) {
                System.out.println("Puerto ocupado, esperando...");
                try { Thread.sleep(1000); } 
                catch (InterruptedException ex) { /* Ignorar */ }
            }
        }

        System.out.println("Esperando conexiones de clientes...");
        
        while (true) {
            try {
                // Aceptar conexión de cliente
                Socket clientSocket = serverSocket.accept();
                String ipCliente = clientSocket.getInetAddress().getHostAddress();
                int puertoCliente = clientSocket.getPort();
                System.out.println("Recibida conexión del host [" + ipCliente + ":" + puertoCliente + "]");
                
                // Procesar en hilo separado
                new Thread(() -> procesarConexion(clientSocket)).start();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void procesarConexion(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String ipCliente = clientSocket.getInetAddress().getHostAddress();
            int puertoCliente = clientSocket.getPort();
            
            System.out.println("Procesando petición del host [" + ipCliente + ":" + puertoCliente + "]");
            
            // Leer datos
            String datos = in.readLine();
            if (datos == null || !datos.startsWith("#") || !datos.endsWith("#")) {
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
            out.println(respuesta.toString());
            System.out.println("Enviada respuesta al host [" + ipCliente + ":" + puertoCliente + "]");
            System.out.println("Respuesta: " + respuesta);
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}