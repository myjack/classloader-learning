package classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetClassLoader extends ClassLoader {

    private static String WEBCLASSPATH = "";

    public NetClassLoader(String webClassPath) {
        WEBCLASSPATH = webClassPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = getFileFromNet(WEBCLASSPATH);
            return defineClass(name, data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.findClass(name);
    }

    private byte[] getFileFromNet(String URL) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        // 防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // 得到输入流
        InputStream inputStream = conn.getInputStream();
        // 获取自己数组
        return readInputStream(inputStream);
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        ClassLoader cl = new NetworkClassLoader("http://97.64.124.191:8080/");
        try {
            Class c = cl.loadClass("Hello");
            if (c == null) {
                System.out.println("类加载失败！");
                return;
            }
            try {
                Object obj = c.newInstance();
                Method method = c.getDeclaredMethod("sayHello", null);
                // 通过反射调用方法
                method.invoke(obj, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

}
