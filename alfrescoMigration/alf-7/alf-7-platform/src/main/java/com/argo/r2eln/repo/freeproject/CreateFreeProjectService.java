package com.argo.r2eln.repo.freeproject;

import java.util.Date;
import java.util.List;

public interface CreateFreeProjectService {
    void create(String projectName, Date startDate, Date endDate, String leaderId, List<String > partResearcherIds);
}
