import java.io.*;
import java.net.*;
import java.awt.Desktop;

public class ClienteTCPFICHERO {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ClienteTCP <puerto> <archivoPDF>");
            return;
        }
        
        int puertoLocal = Integer.parseInt(args[0]);
        String nombreArchivo = args[1];
        Socket socket = null;

        // Verificar y esperar si el puerto está ocupado
        while (socket == null) {
            try {
                socket = new Socket();
                socket.bind(new InetSocketAddress(puertoLocal));
                System.out.println("Puerto " + puertoLocal + " libre. Creando socket...");
            } catch (IOException e) {
                System.out.println("Puerto ocupado, esperando...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Conectar al servidor (puerto 40000 fijo)
        try {
            System.out.println("Conectando con el servidor en el puerto 40000...");
            socket.connect(new InetSocketAddress("localhost", 40000));
            
            // Enviar archivo PDF
            File archivo = new File(nombreArchivo);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(archivo.getName());
            dos.writeLong(archivo.length());
            
            try (FileInputStream fis = new FileInputStream(archivo)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Fichero PDF enviado correctamente.");
            
            // Recibir archivo procesado
            System.out.println("Esperando fichero del servidor...");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String nombreRecibido = dis.readUTF();
            long tamanoRecibido = dis.readLong();
            
            try (FileOutputStream fos = new FileOutputStream(nombreRecibido)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;
                while (totalRead < tamanoRecibido && 
                      (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, tamanoRecibido - totalRead))) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }
            System.out.println("Fichero PDF recibido. Guardando como \"" + nombreRecibido + "\"...");
            
            // Verificar apertura del archivo
            File archivoRecibido = new File(nombreRecibido);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoRecibido);
                System.out.println("Fichero abierto correctamente para verificación.");
            } else {
                System.out.println("No se pudo abrir el archivo automáticamente.");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}