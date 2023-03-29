package featuretest.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class DownloadEUtil {
    private static DownloadEUtil downloadEUtil;

    private HttpClient httpClient;

    private HttpHeaders httpHeaders;

    private HttpRequest httpRequest;
    private HttpResponse<InputStream> httpResponse;


    public static DownloadEUtil get() {
        if (downloadEUtil == null) {
            downloadEUtil = new DownloadEUtil();
        }
        return downloadEUtil;
    }

    private DownloadEUtil() {
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称，后面记得拼接后缀，否则手机没法识别文件类型
     * @param listener     下载监听
     */

    public void download(final String url, final String destFileDir, final String destFileName,
                         final OnDownloadListener listener) throws IOException, InterruptedException {
        executeAsync(url, destFileDir, destFileName, listener);
    }

    private void executeAsync(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {
        httpRequest = HttpRequest.newBuilder().uri(URI.create(url))
                .GET().build();
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                .thenAccept(response -> {
                    byte[] buf = new byte[2048];
                    InputStream is = response.body();
                    long total = Long.parseLong(response.headers().map().get("content-length").get(0));
                    int len;
                    FileOutputStream fos = null;
                    File dir = new File(destFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, destFileName);

                    System.out.println(Thread.currentThread().toString());
                    System.out.println(Thread.currentThread().getId());
                    System.out.println("==============async up=====================");
                    try {
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            //下载中更新进度条
                            int progress = (int) (sum * 1.0f / total * 100);
                            //下载中更新进度条
                            listener.onDownloading(progress);
                        }
                        fos.flush();
                        //下载完成
                        listener.onDownloadSuccess(file);

                    } catch (Exception e) {
                        listener.onDownloadFailed(e);
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).join();

        System.out.println("==============main bellow=====================");
        System.out.println(Thread.currentThread().toString());
        System.out.println(Thread.currentThread().getId());
    }

    public interface OnDownloadListener {

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */

        void onDownloadFailed(Exception e);
    }

}

class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        DownloadEUtil downloadEUtil = DownloadEUtil.get();

        var url =
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201911%2F03%2F20191103153147_xymks.jpeg&refer=http%3A%2F" +
                        "%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1644727911&t=2073433f685b0645a376f6d6dd8a79c6";
        downloadEUtil.download(url, "test", "hello.jpg", new DownloadEUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                System.out.println(file.getPath() + "/" + file.getName());
            }

            @Override
            public void onDownloading(int progress) {
                System.out.println(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
