/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.delegater;

import java.io.BufferedReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.NLaboModuleConverter;
import open.dolphin.infomodel.*;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * Labo関連のDelegaterクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 *
 */
public final class LaboDelegater extends BusinessDelegater {

    //=========================================================
    // 新 LabMozule
    //=========================================================
    public List<PatientLiteModel> getConstrainedPatients(List<String> idList) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/patient/");
        for (String pid : idList) {
            sb.append(pid);
            sb.append(CAMMA);
        }
        int len = sb.length();
        sb.setLength(len - 1);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        PatientLiteList result;
        try (BufferedReader br = getReader(response)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            result = mapper.readValue(br, PatientLiteList.class);
        }

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));
        // List
        return result.getList();
    }

    /**
     * 検査結果を追加する。
     *
     * @param value 追加する検査モジュール
     * @return 患者オブジェクト
     * @throws java.lang.Exception
     */
    public PatientModel putNLaboModule(NLaboModule value) throws Exception {

//        System.err.println(value.getPatientId());
//        System.err.println(value.getPatientName());
//        System.err.println(value.getPatientSex());
//        List<NLaboItem> items = value.getItems();
//        for (NLaboItem item : items) {
//            System.err.println(item.getItemCode());
//            System.err.println(item.getItemName());
//        }
        // PATH
        String path = "/lab/module";
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // Converter
        NLaboModuleConverter conv = new NLaboModuleConverter();
        conv.setModel(value);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);

        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.post(String.class);
        PatientModel patient;
        try (BufferedReader br = getReader(response)) {
            ObjectMapper mapper2 = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            patient = mapper2.readValue(br, PatientModel.class);
        }

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON, json);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        return patient;
    }

    /**
     * ラボモジュールを検索する。
     *
     * @param patientId 対象患者のID
     * @param firstResult 取得結果リストの最初の番号
     * @param maxResult 取得する件数の最大値
     * @return ラボモジュールを採取日で降順に格納したリスト
     * @throws java.lang.Exception
     */
    public List<NLaboModule> getLaboTest(String patientId, int firstResult, int maxResult) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/module/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstResult));
        sb.append(CAMMA);
        sb.append(String.valueOf(maxResult));
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        NLaboModuleList result;
        try (BufferedReader br = getReader(response)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            result = mapper.readValue(br, NLaboModuleList.class);
        }

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // List
        return result.getList();
    }

//s.oh^ 2013/09/18 ラボデータの高速化
    public String getLaboTestCount(String pid) {
        // PATH
        String path = "/lab/module/count/";
        path += pid;

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.TEXT_PLAIN);
        ClientResponse<String> response = null;
        String entityStr = "0";
        try {
            response = request.get(String.class);
            entityStr = getString(response);
        } catch (Exception ex) {
            Logger.getLogger(LaboDelegater.class.getName()).log(Level.SEVERE, null, ex);
        }

        return entityStr;
    }
//s.oh$

    /**
     * 指定された検査コードの結果を取得する。
     *
     * @param patientId 対象患者
     * @param firstResult 全件数のなかで最初に返す番号
     * @param maxResult 戻す最大件数
     * @param itemCode 検索する検査コード
     * @return 検査結果項目を採取日で降順に格納したリスト
     * @throws java.lang.Exception
     */
    public List<NLaboItem> getLaboTestItem(String patientId, int firstResult, int maxResult, String itemCode) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/item/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstResult));
        sb.append(CAMMA);
        sb.append(String.valueOf(maxResult));
        sb.append(CAMMA);
        sb.append(itemCode);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        NLaboItemList result;
        try (BufferedReader br = getReader(response)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            result = mapper.readValue(br, NLaboItemList.class);
        }

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // List
        return result.getList();
    }

    // ラボデータの削除 2013/06/24
    public int deleteLabTest(long moduleId) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/module/");
        sb.append(moduleId);
        String path = sb.toString();

        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);

        // Check
        checkStatus(response);

        // Count
        return 1;
    }
}
