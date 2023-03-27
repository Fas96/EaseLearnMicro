package com.argo.r2eln.repo.freeproject;

import java.util.Random;

public class ProjectCodeGenerator {
    public String createNewFreeProjectCode() {
        Random random = new Random();

        int randomNum = random.nextInt(1000000);

        return "FR" + String.format("%07d", randomNum);
    }
}
