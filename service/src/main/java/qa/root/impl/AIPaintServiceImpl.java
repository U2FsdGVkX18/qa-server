package qa.root.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.common.RestTemplateTool;
import qa.root.AIPaint;
import qa.root.service.AIPaintService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class AIPaintServiceImpl implements AIPaintService {
    @Resource
    private RestTemplateTool restTemplateTool;

    @Override
    public Map<String, Object> execute(AIPaint aiPaint) {
        //启动机器,拿到nodeId
        Object nodeId = machineRun(aiPaint);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //查询机器状态,拿到指定机器和信息
        Map<String, Object> machineInfo = getMachineInfo(nodeId, aiPaint);
        log.info("日志->>>service:execute->>>获取机器信息成功!机器信息:{}", machineInfo);

        //连接机器获取session
//        Session session = getSession(machineInfo);
        //执行命令,返回结果
//        String command1 = "nvidia-smi --query-gpu=timestamp,memory.total,memory.free,memory.used --format=csv,noheader,nounits";
//        StringBuffer returnMsg = getReturnMsg(session, command1);

        return machineInfo;
    }


    /**
     * 根据jsch.session创建channel并执行命令
     *
     * @param session jsch.session
     * @param command 执行的命令
     * @return 输出命令结果
     */
    public StringBuffer getReturnMsg(Session session, String command) {
        StringBuffer sb = new StringBuffer();
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
            InputStream is = channel.getInputStream();
            int i;
            while ((i = is.read()) != -1) {
                sb.append((char) i);
            }
            log.info("日志->>>service:getReturnMsg->>>创建channel成功!,执行命令并获取输出:{}", sb);
            return sb;
        } catch (Exception e) {
            log.error("日志->>>service:getReturnMsg->>>创建channel失败!,信息:{}", e.getMessage());
            return sb;
        }
    }

    /**
     * 连接机器,获取jsch.session
     *
     * @param machineInfo 机器信息
     * @return jsch.session
     */
    public Session getSession(Map<String, Object> machineInfo) {
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession((String) machineInfo.get("username"), (String) machineInfo.get("host"), (Integer) machineInfo.get("port"));
            session.setPassword((String) machineInfo.get("password"));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
            boolean connected = session.isConnected();
            if (connected) {
                log.info("日志->>>service:getSession->>>连接机器成功!,获取session成功!");
            } else {
                log.error("日志->>>service:getSession->>>连接机器失败!,获取session失败!");
            }
            return session;
        } catch (Exception e) {
            log.error("日志->>>service:getSession->>>连接机器失败!,获取session失败:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 拿到指定机器信息
     *
     * @param nodeId nodeId
     * @return map
     */
    public Map<String, Object> getMachineInfo(Object nodeId, AIPaint aiPaint) {
        HashMap<String, Object> machineInfo = new HashMap<>();
        //初始status,方便判断
        int status = 0;
        //初始机器信息
        JSONObject machineJson = new JSONObject();
        //轮询获取机器状态
        while (true) {
            //等机器变为启动中的状态则可以使用
            if (status == 2) {
                log.info("日志->>>service:getMachineInfo->>>机器启动成功!获取机器信息并返回...");
                //初始url
                String urlText = "";
                //获取机器port数组
                JSONArray jsonArray = machineJson.getJSONObject("node").getJSONArray("ports");
                for (Object obj : jsonArray) {
                    JSONObject port = (JSONObject) obj;
                    if (22 == (int) port.get("srcPort")) {
                        urlText = (String) port.get("url");
                        break;
                    }
                }
                //分割url,提取对应值
                String[] split = urlText.split("/")[2].split(":");
                //获取机器host
                machineInfo.put("host", split[0]);
                //获取机器port
                machineInfo.put("port", Integer.parseInt(split[1]));
                //获取机器password
                machineInfo.put("password", machineJson.getJSONArray("sshAuths").getJSONObject(0).getJSONObject("BaseOn").getJSONObject("Cred").get("password"));
                //返回机器信息
                machineInfo.put("username", "root");
                return machineInfo;
            }
            try {
                Thread.sleep(3000);
                //查询租用列表并获取指定机器的状态
                JSONArray machineArray = (JSONArray) machineStatus(aiPaint);
                //从租用列表中获取指定机器和指定机器状态
                machineJson = getMachine(machineArray, nodeId);
                //重置status值,进行下一次判断
                status = (int) machineJson.get("status");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从租用列表中获取指定机器
     *
     * @param machineArray 机器列表
     * @param nodeId       nodeID
     * @return JSONObject
     */
    public JSONObject getMachine(JSONArray machineArray, Object nodeId) {
        JSONObject json = new JSONObject();
        for (Object machine : machineArray) {
            JSONObject machineJson = JSONObject.parseObject(String.valueOf(machine));
            if ((int) machineJson.getJSONObject("node").get("id") == (int) nodeId) {
                json = machineJson;
                break;
            }
        }
        return json;
    }

    /**
     * 查询租用列表并获取指定机器的状态
     *
     * @param aiPaint 包含机器信息
     * @return 接口响应体对象
     */
    public Object machineStatus(AIPaint aiPaint) {
        //查询机器状态接口
        String machineStatusUrl = "https://matpool.com/api/nodes?order={order}&page={page}&per_page={per_page}";
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("order", false);
        paramsMap.put("page", 1);
        paramsMap.put("per_page", 10);
        ResponseEntity<String> responseEntity = restTemplateTool.sendGetRequest(machineStatusUrl, getHeader(aiPaint.getAuthorization()), paramsMap);
        log.info("日志->>>service:machineStatus->>>查询机器状态...");
        JSONObject response = JSONObject.parseObject(String.valueOf(responseEntity.getBody()));
        return response.getJSONArray("userNodes");
    }

    /**
     * 启动机器,并返回nodeId
     *
     * @param aiPaint 包含机器信息
     * @return 接口响应体对象
     */
    public Object machineRun(AIPaint aiPaint) {
        //租用机器接口
        String machineRunUrl = "https://matpool.com/api/node";
        //获取封装后的参数
        Map<String, Object> machineRunParam = machineRunParam(aiPaint);
        ResponseEntity<JSONObject> response = restTemplateTool.sendPostRequest(machineRunUrl, getHeader(aiPaint.getAuthorization()), new JSONObject(machineRunParam));
        log.info("日志->>>service:machineRun->>>开始启动机器...机器参数:{}", machineRunParam);
        return Objects.requireNonNull(response.getBody()).getJSONObject("node").get("id");
    }

    /**
     * 封装启动机器所需参数
     *
     * @param aiPaint 包含机器信息
     * @return 接口所需参数对象
     */
    public Map<String, Object> machineRunParam(AIPaint aiPaint) {
        //封装参数
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("agent_id", aiPaint.getAgentId());
        hashMap.put("auto_password", true);
        hashMap.put("from_snapshot_id", aiPaint.getSnapId());
        hashMap.put("hardware_qty", 1);
        hashMap.put("image_id", aiPaint.getImageId());
        hashMap.put("machine_category", 0);
        hashMap.put("mount_infos", "[{\"source\":\"/\",\"dest\":\"/mnt\"}]");
        hashMap.put("vnc_switcher", true);
        return hashMap;
    }

    /**
     * 初始化header头部信息
     *
     * @return Headers
     */
    private HttpHeaders getHeader(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);
        return headers;
    }
}
