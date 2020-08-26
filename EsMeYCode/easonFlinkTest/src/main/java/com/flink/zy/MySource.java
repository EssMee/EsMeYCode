package com.flink.zy;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.*;

public class MySource implements SourceFunction<String> {
    private boolean running = true;
    private String filePath;

    public MySource(String file) {filePath = file;}
    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        while (running) {
            String res = new FileRead(filePath).readFile();
            Thread.sleep(3000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    static class FileRead {
        private String str = "";
        private String str1 = "";
        private String filePath;
        public FileRead(String path) {
            filePath = path;
        }
        public String readFile() {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;

            try {
                fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);

                while ((str = br.readLine()) != null) {
                    str1 += str + "\n";
                }
            } catch (FileNotFoundException e) {
                System.out.printf("找不到%s文件~~~", filePath);
            } catch (IOException e) {
                System.out.println("读取文件失败~~~");
            } finally {
                try {
                    br.close();
                    isr.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str1;
        }
    }
}
