
import java.io.*;
import java.net.*;

public class ServidorTCPFICHERO {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Uso: java ServidorTCP <puerto>");
            return;
        }
        
        int puerto = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
        
        // Verificar puerto libre
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(puerto);
                System.out.println("Puerto libre. Creando socket en puerto " + puerto);
            } catch (IOException e) {
                System.out.println("Puerto ocupado, esperando...");
                try { Thread.sleep(1000); } 
                catch (InterruptedException ex) { /* Ignorar */ }
            }
        }
        
        System.out.println("Esperando conexiones...");
        
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Cliente conectado: " + socket.getInetAddress());
                
                // Recibir archivo
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream("temp.pdf");
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
                System.out.println("PDF recibido");
                
                // Procesar (espera 5 segundos)
                System.out.println("Procesando fichero...");
                Thread.sleep(5000);
                
                // Enviar de vuelta (renombrado)
                System.out.println("Enviando archivo procesado...");
                File file = new File("temp.pdf");
                FileInputStream fis = new FileInputStream(file);
                OutputStream os = socket.getOutputStream();
                
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                fis.close();
                System.out.println("Archivo enviado como OriginalProcesado.pdf");
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}