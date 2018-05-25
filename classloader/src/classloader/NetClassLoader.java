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
        // ���ó�ʱ��Ϊ3��
        conn.setConnectTimeout(3 * 1000);
        // ��ֹ���γ���ץȡ������403����
        conn.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // �õ�������
        InputStream inputStream = conn.getInputStream();
        // ��ȡ�Լ�����
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
                System.out.println("�����ʧ�ܣ�");
                return;
            }
            try {
                Object obj = c.newInstance();
                Method method = c.getDeclaredMethod("sayHello", null);
                // ͨ��������÷���
                method.invoke(obj, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

}
