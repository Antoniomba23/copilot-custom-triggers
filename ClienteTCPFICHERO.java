
import java.io.*;
import java.net.*;
import java.awt.Desktop;

public class ClienteTCPFICHERO {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Uso: java ClienteTCP <puerto> <archivo.pdf>");
            return;
        }
        
        int puerto = Integer.parseInt(args[0]);
        String archivo = args[1];
        Socket socket = null;
        
        // Verificar puerto local libre
        while (socket == null) {
            try {
                socket = new Socket();
                socket.bind(new InetSocketAddress(puerto));
                System.out.println("Puerto libre. Socket creado en puerto " + puerto);
            } catch (IOException e) {
                System.out.println("Puerto ocupado, esperando...");
                try { Thread.sleep(1000); } 
                catch (InterruptedException ex) { /* Ignorar */ }
            }
        }
        
        // Conectar al servidor (localhost en este ejemplo)
        socket.connect(new InetSocketAddress("localhost", 40000));
        System.out.println("Conectado al servidor");
        
        // Enviar archivo PDF
        File file = new File(archivo);
        FileInputStream fis = new FileInputStream(file);
        OutputStream os = socket.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        
        System.out.println("Enviando " + archivo + "...");
        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        fis.close();
        System.out.println("PDF enviado");
        
        // Recibir archivo procesado
        System.out.println("Esperando fichero procesado...");
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream("OriginalProcesado.pdf");
        
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        System.out.println("PDF recibido y guardado");
        
        // Abrir archivo para verificación
        File pdfFile = new File("OriginalProcesado.pdf");
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(pdfFile);
            System.out.println("Abriendo archivo...");
        }
        socket.close();
    }
}