package com.example.demo.controller;


import com.example.demo.service.DocSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class DocSearchController  {

    @Autowired
    private DocSearchService docSearchService;

    Map<String, ArrayList<Float>> documentDataLists = new HashMap<>();

    /*
    * 조건2 : Maximum 100개의 query를 랜덤하게 생성한 후,
    *        이를 문서 데이터와 비교하여 가장 유사도 높은 문서를 추출
    * */
    @GetMapping("/query")
    @ResponseBody
    public ArrayList<String> getResultWithQuery() {

        ArrayList<String> result = new ArrayList<>();

        // query : 1~100 개의 query 생성
        int queryRange = getRandomNumberUsingInts(1, 100);
        List<ArrayList<Float>> queryDataLists = docSearchService.createQueryData(queryRange);

        for (int i = 0; i < queryDataLists.size(); i++) {
            ArrayList<Float> inputQuery = queryDataLists.get(i);

            long beforeTime = System.currentTimeMillis();
            ArrayList<String> resultDocs = docSearchService.getResults(documentDataLists, inputQuery);
            long afterTime = System.currentTimeMillis();
            log.info("Sequence " + i + " executed in : " + (afterTime - beforeTime) + " ms");

            result.add(resultDocs.toString());
        }

        return result;
    }

    /*
    * 조건1: Maximum 1M개의 문서 데이터를 랜덤하게 생성하는 부분.
    * */
    @GetMapping("/document")
    public void createMaxDocumentData() {
        // document : 1~1M (1,000,000) 개의 문서 데이터 생성
        int docRange = getRandomNumberUsingInts(1, 1000000);
        documentDataLists = docSearchService.createDocumentData(docRange);
    }

    // Ref: https://www.baeldung.com/java-generating-random-numbers-in-range
    public int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }
}
