import java.io.*;
import java.net.*;

public class ServidorTCPFICHERO {
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
                System.out.println("Puerto ocupado, esperando...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Esperando conexiones de clientes...");

        try {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Cliente conectado desde " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Recibir archivo PDF
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    String nombreArchivo = dis.readUTF();
                    long tamanoArchivo = dis.readLong();
                    
                    System.out.println("Recibiendo archivo PDF: " + nombreArchivo);
                    
                    // Guardar archivo recibido temporalmente
                    File tempFile = File.createTempFile("temp", ".pdf");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long totalRead = 0;
                        while (totalRead < tamanoArchivo && 
                              (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, tamanoArchivo - totalRead))) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                    }
                    
                    // Procesamiento con espera de 5 segundos
                    System.out.println("Procesando fichero...");
                    Thread.sleep(5000);
                    
                    // Enviar archivo renombrado
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    dos.writeUTF("OriginalProcesado.pdf");
                    dos.writeLong(tempFile.length());
                    
                    try (FileInputStream fis = new FileInputStream(tempFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                        }
                    }
                    System.out.println("Archivo procesado. Enviando al cliente...");
                    tempFile.delete();
                    
                    System.out.println("Fichero \"OriginalProcesado.pdf\" enviado correctamente.");
                }
                System.out.println("Finalizando comunicación con el cliente...");
                System.out.println("Esperando nuevas conexiones...");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}