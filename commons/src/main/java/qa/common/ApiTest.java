package qa.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class ApiTest {
    public static void main(String[] args) {

        RestTemplateTool restTemplateTool = new RestTemplateTool();
        String url = "https://welm.weixin.qq.com/v1/completions";

        String text =
                "李⽩，字太⽩，号⻘莲居⼠，⼜号“谪仙⼈”，唐代伟⼤的浪漫主义\n" +
                        "诗⼈，被后⼈誉为“诗仙”。\n" +
                        "我：今天我们穿越时空连线李⽩，请问李⽩你爱喝酒吗？\n" +
                        "李⽩：当然。花间⼀壶酒，独酌⽆相亲。举杯邀明⽉，对影成三⼈。\n" +
                        "我：你觉得杜甫怎么样？\n" +
                        "李⽩：他很仰慕我，但他有属于⾃⼰的⻛采。\n" +
                        "我：你为何能如此逍遥？\n" +
                        "李⽩：天⽣我材必有⽤，千⾦散尽还复来！\n" +
                        "我：你都去过哪些地方？\n" +
                        "李白：\n";

        HashMap<String, Object> p_map = new HashMap<>();
        p_map.put("prompt", text);
        p_map.put("model", "xl");
        p_map.put("max_tokens", 25);
        p_map.put("temperature", 0.0);
        p_map.put("top_p", 0.0);
        p_map.put("top_k", 10);
        p_map.put("n", 1);
        p_map.put("echo", false);
        p_map.put("stop", ",，.。\n");

        ResponseEntity<JSONObject> response = restTemplateTool.sendPostRequest(url, new JSONObject(p_map));
        System.out.println(response.getBody());
    }
}
