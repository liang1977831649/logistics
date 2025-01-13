package com.logistics.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

import java.io.InputStream;

public class AliOssUtil {
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private static String endpoint = "https://oss-cn-chengdu.aliyuncs.com";

    // 填写Bucket名称，例如examplebucket。
    private static String bucketName = "baise212lz";
    // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
    private static String path = "logistics";

    public static String upload(String objectName, InputStream fileInputStream)  {
        String url=null;

        try {

            // 强烈建议不要把访问凭证保存到工程代码里，否则可能导致访问凭证泄露，威胁您账号下所有资源的安全。本代码示例以从环境变量中获取访问凭证为例。运行本代码示例之前，请先配置环境变量。
            //EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();


            // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);


            // 创建PutObjectRequest对象。"/"+DateCreateUtil.dateFileName()+
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path+"/"+DateCreateUtil.dateFileName()+"/"+objectName, fileInputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 创建PutObjectRequest对象。
            //PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(fileInputStream.readAllBytes()));

            // 上传字符串。
            //PutObjectResult result = ossClient.putObject(putObjectRequest);"/"+DateCreateUtil.dateFileName()+
            url="https://"+bucketName+"."+endpoint.substring(endpoint.lastIndexOf("/")+1)+"/"+path+"/"+DateCreateUtil.dateFileName()+"/"+objectName;
            System.out.println("url="+url);
            return url;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }
        return null;
    }
}