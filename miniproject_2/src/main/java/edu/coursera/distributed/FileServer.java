package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

            Socket s = socket.accept();

            InputStream stream = s.getInputStream();
            BufferedReader buffered = new BufferedReader(new InputStreamReader(stream));
            String[] line = buffered.readLine().split(" ");
            if (!line[0].equals("GET")) {
                s.close();
            }
            String contents = fs.readFile(new PCDPPath(line[1]));

            PrintWriter printer = new PrintWriter(s.getOutputStream());
            String result;
            if (contents == null) {
                result = "HTTP/1.0 404 Not Found\r\n" +
                        "Server: FileServer\r\n" +
                        "\r\n";
            }
            else {
                result = "HTTP/1.0 200 OK\r\n" +
                        "Server: FileServer\r\n" +
                        "\r\n" +
                        contents + "\r\n" +
                        "\r\n";
            }
            printer.write(result);
            printer.close();
        }
    }
}
