package com.argo.r2eln.repo.web.scripts.freeproject;

import com.argo.r2eln.repo.dao.R2ElnCommonDAO;
import com.argo.r2eln.repo.freeproject.CreateFreeProjectService;
import com.argo.r2eln.repo.web.scripts.authority.AbstractAuthorityWebScript;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.lucene.analysis.ko.KoreanFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostFreeProjects extends AbstractAuthorityWebScript {

    public static Log log = LogFactory.getLog(PostFreeProjects.class);

    private R2ElnCommonDAO commonDao;
    private PersonService personService;
    private CreateFreeProjectService createFreeProjectService;

    public void setCommonDAO(R2ElnCommonDAO dao) {
        this.commonDao = dao;
    }
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
    public void setCreateFreeProjectService(CreateFreeProjectService createFreeProjectService) {
        this.createFreeProjectService = createFreeProjectService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        log.info("자유과제 생성 시작합니다.");

        JSONParser jsonParser = new JSONParser();
        String projectName;
        Date startDate;
        Date endDate;
        String leaderId;
        JSONArray partResearcherIds;
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            log.info("====== 유효성 검증 중 =======");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            JSONObject body = (JSONObject) jsonParser.parse(req.getContent().getContent());
            projectName = (String) body.get("projectName");
            startDate = formatter.parse((String) body.get("startDate"));
            endDate = formatter.parse((String) body.get("endDate"));
            leaderId = (String) body.get("leaderId");

            log.info("Checking the response : " + body.get("partResearcherIds"));
            log.info("leaderId : " + leaderId+" projectname : "+projectName+" startDate : "+startDate+" endDate : "+endDate);

            partResearcherIds = (JSONArray)(JSONArray) JSONValue.parse((String) body.get("partResearcherIds"));

            System.out.println("partResearcherIds : " + partResearcherIds);


            log.info(body);

            //유효성 검증
            if (
                    !StringUtils.hasLength(projectName)
                            || !StringUtils.hasLength(leaderId)
                            || partResearcherIds == null
            ) throw new RuntimeException("파라미터가 잘못되었습니다.");

            for (Object partResearcherId : partResearcherIds) {
                boolean exists = personService.personExists((String) partResearcherId);
                if (!exists) throw new RuntimeException("없는 사람입니다. userId : " + partResearcherId);
            }
            if (!personService.personExists(leaderId)){
                throw new RuntimeException("없는 사람입니다. userId : " + leaderId);
            }

        } catch (Exception e) {
            e.printStackTrace();

            status.setCode(HttpStatus.SC_BAD_REQUEST);
            model.put("isSucess", false);

            return model;
        }

        log.info("====== 유효성 검증 끝 =======");


        log.info("====== 자유과제 생성중 =======");
        try {
            // 포맷터

            // 문자열 -> Date
            createFreeProjectService.create(projectName,
                    startDate,
                    endDate,
                    leaderId, partResearcherIds);

        } catch (Exception e) {
            log.info("====== 자유과제 생성 실패 =======");
            e.printStackTrace();

            status.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            model.put("isSucess", false);
            return model;
        }

        log.info("====== 자유과제 생성 성공 =======");

        status.setCode(HttpStatus.SC_OK);
        model.put("isSucess", true);

        return model;
    }
}
