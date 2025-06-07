
import java.io.*;
import java.net.*;
import java.awt.Desktop;

public class ClienteUDPFICHERO {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Uso: java ClienteUDP <puerto-local> <puerto-servidor> <archivo>");
            return;
        }

        int puertoLocal = Integer.parseInt(args[0]);
        int puertoServidor = Integer.parseInt(args[1]);
        String archivo = args[2];
        DatagramSocket socket = null;

        // Verificar puerto local
        while (socket == null) {
            try {
                socket = new DatagramSocket(puertoLocal);
                System.out.println("Socket UDP creado en puerto " + puertoLocal);
            } catch (SocketException e) {
                System.out.println("Puerto ocupado, esperando...");
                try { Thread.sleep(1000); } 
                catch (InterruptedException ex) { /* Ignorar */ }
            }
        }

        // Leer archivo PDF
        File file = new File(archivo);
        FileInputStream fis = new FileInputStream(file);
        byte[] fileData = fis.readAllBytes();
        fis.close();
        
        // Enviar al servidor
        InetAddress direccion = InetAddress.getByName("localhost");
        DatagramPacket paquete = new DatagramPacket(
            fileData, 
            fileData.length, 
            direccion, 
            puertoServidor
        );
        
        socket.send(paquete);
        System.out.println("PDF enviado (" + fileData.length + " bytes)");

        // Recibir respuesta
        byte[] buffer = new byte[65507];
        DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
        socket.receive(respuesta);
        
        // Guardar archivo
        FileOutputStream fos = new FileOutputStream("OriginalProcesado.pdf");
        fos.write(respuesta.getData(), 0, respuesta.getLength());
        fos.close();
        System.out.println("PDF recibido (" + respuesta.getLength() + " bytes)");
        
        // Abrir para verificación
        File pdfFile = new File("OriginalProcesado.pdf");
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(pdfFile);
        }
        socket.close();
    }
}