package qa.common;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 封装http请求工具,用于调用第三方接口
 */
@Slf4j
@Component
public class RestTemplateTool {

    /**
     * 初始化RestTemplate对象
     *
     * @return RestTemplate
     */
    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(5000);
        clientHttpRequestFactory.setReadTimeout(5000);
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        return restTemplate;
    }

    /**
     * 发送请求主体-GET请求
     *
     * @param url       url地址
     * @param headers   请求头
     * @param paramsMap query参数
     * @return
     */
    public ResponseEntity<String> sendGetRequest(String url, HttpHeaders headers, Map<String, Object> paramsMap) {
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
        try {
            return getRestTemplate().exchange(url, HttpMethod.GET, httpEntity, String.class, paramsMap);
        } catch (Exception e) {
            log.info("日志->>>RestTemplateTool:sendGetRequest->>>请求异常: [{}]", e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    /**
     * 发送请求主体-POST请求
     *
     * @param url    地址
     * @param params 参数
     * @return 响应
     */
    public ResponseEntity<JSONObject> sendPostRequest(String url, HttpHeaders headers, JSONObject params) {
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, headers);
        try {
            return getRestTemplate().postForEntity(url, requestEntity, JSONObject.class);
        } catch (Exception e) {
            log.info("日志->>>RestTemplateTool:sendPostRequest->>>请求异常: [{}]", e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }
}
