package org.etsntesla.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RunShellCommand {


    public static void main(String[] args) {
        //testing...
        execute(new String[]{"dir"});
    }


     static public boolean execute(String[] command) {
        try{
            String[] winCmd = Stream.of(new String[]{"cmd.exe","/c"},command).flatMap(Stream::of).toArray(String[]::new);
            Process process = Runtime.getRuntime().exec(winCmd);
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(),System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
            int code=process.waitFor();
            future.get();
            return code==0;
        }catch (IOException | InterruptedException | ExecutionException e){
            throw new RuntimeException(e.getMessage());
        }
    }


    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

}
