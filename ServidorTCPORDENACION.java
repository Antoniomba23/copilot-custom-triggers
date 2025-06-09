import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ServidorTCPORDENACION {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java ServidorTCP <puerto>");
            return;
        }
        
        int puerto = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;

        // Verificar y esperar si el puerto está ocupado
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(puerto);
                System.out.println("Puerto libre. Creando socket en el puerto " + puerto + "...");
            } catch (IOException e) {
                System.out.println("Puerto ocupado ...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Esperando datos...");

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try (
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                // Obtener información del cliente
                String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                System.out.println("Recibidos datos del host " + clientInfo);
                
                // Procesar solicitud
                System.out.println("Procesando petición del host " + clientInfo + "...");
                String datos = dis.readUTF();
                
                // Verificar formato
                if (!datos.startsWith("#") || !datos.endsWith("#")) {
                    System.out.println("Formato de datos incorrecto");
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
                dos.writeUTF(respuesta.toString());
                System.out.println("Enviado datos al host " + clientInfo);
                
            } catch (IOException | NumberFormatException e) {
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
}