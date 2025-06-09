import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Arrays;
import java.awt.Desktop;

public class ClienteUDPFICHERO {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ClienteUDP <puerto> <archivoPDF>");
            return;
        }
        
        int puerto = Integer.parseInt(args[0]);
        String nombreArchivo = args[1];
        DatagramSocket socket = null;

        // Verificar y esperar si el puerto está ocupado
        while (socket == null) {
            try {
                socket = new DatagramSocket(puerto);
                System.out.println("Puerto " + puerto + " libre. Creando socket en el puerto " + puerto + "...");
            } catch (SocketException e) {
                System.out.println("Puerto " + puerto + " ocupado...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Cliente arrancado en el puerto " + puerto + ".");
        
        try {
            // Leer archivo PDF
            File archivo = new File(nombreArchivo);
            byte[] fileContent = Files.readAllBytes(archivo.toPath());
            
            // Preparar datagrama para servidor
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            System.out.println("Enviando datagrama al servidor por el puerto 50000...");
            DatagramPacket paqueteEnvio = new DatagramPacket(
                fileContent,
                fileContent.length,
                direccionServidor,
                50000
            );
            socket.send(paqueteEnvio);
            System.out.println("Fichero PDF enviado correctamente.");
            
            // Recibir respuesta
            System.out.println("Esperando datagrama fichero del servidor...");
            byte[] buffer = new byte[65507];
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteRespuesta);
            
            // Guardar archivo procesado
            String nuevoNombre = "OriginalProcesado.pdf";
            Files.write(Paths.get(nuevoNombre), 
                      Arrays.copyOf(paqueteRespuesta.getData(), paqueteRespuesta.getLength()));
            
            System.out.println("Fichero PDF recibido.");
            System.out.println("Guardando como \"" + nuevoNombre + "\"...");
            
            // Verificar apertura del archivo
            File archivoRecibido = new File(nuevoNombre);
            if (Desktop.isDesktopSupported()) {
                System.out.println("Abriendo fichero para verificación...");
                Desktop.getDesktop().open(archivoRecibido);
                System.out.println("(Fichero abierto correctamente.)");
            } else {
                System.out.println("No se puede abrir el archivo automáticamente.");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}