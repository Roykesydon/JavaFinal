package sample;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RequestController {
    public static HttpResponse post(String url,String[]... args) throws IOException {
        DefaultHttpClient appacheHttp = new DefaultHttpClient();
        List nameValuePairs = new ArrayList();
        HttpPost httpPost = new HttpPost(url);

        for(String[] i:args)
            nameValuePairs.add(new BasicNameValuePair(i[0],i[1] ));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = appacheHttp.execute(httpPost);
        return response;
    }
}
