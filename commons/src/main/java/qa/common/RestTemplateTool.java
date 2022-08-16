package qa.common;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 封装http请求工具,用于调用第三方接口
 */
@Slf4j
public class RestTemplateTool {

    /**
     * 初始化RestTemplate对象
     *
     * @return RestTemplate
     */
    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(1000);
        clientHttpRequestFactory.setReadTimeout(40);
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        return restTemplate;
    }

    /**
     * 初始化header头部信息
     *
     * @return Headers
     */
    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 发送请求主体-GET请求
     *
     * @param url    地址
     * @param params 参数
     * @return 响应
     */
    public ResponseEntity<String> sendGetRequest(String url, JSONObject params) {
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, getHeader());
        try {
            return getRestTemplate().getForEntity(url, String.class, requestEntity);
        } catch (RestClientException e) {
            log.info("日志->>>RestTemplateTool:sendGetRequest->>>请求异常: [{}]", e.getMessage());
            return null;
        }
    }

    /**
     * 发送请求主体-POST请求
     *
     * @param url    地址
     * @param params 参数
     * @return 响应
     */
    public ResponseEntity<JSONObject> sendPostRequest(String url, JSONObject params) {
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, getHeader());
        try {
            return getRestTemplate().postForEntity(url, requestEntity, JSONObject.class);
        } catch (RestClientException e) {
            log.info("日志->>>RestTemplateTool:sendPostRequest->>>请求异常: [{}]", e.getMessage());
            return null;
        }
    }
}