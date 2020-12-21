package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
     * @param ncores The number of cores that are available to your
     *               multi-threaded file server. Using this argument is entirely
     *               optional. You are free to use this information to change
     *               how you create your threads, or ignore it.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs,
            final int ncores) throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ncores);

        while (true) {

            Socket s = socket.accept();

            executor.submit(() -> {
                try {
                    BufferedReader buffered = new BufferedReader(new InputStreamReader(s.getInputStream()));
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
