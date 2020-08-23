package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
@Slf4j
public class DocSearchService {

    Random random = new Random();

    // query random data 생성
    public List<ArrayList<Float>> createQueryData(int queryRange) {

        // query init
        List<ArrayList<Float>> queryMap = new ArrayList<>();

        // query random data 생성
        for(int i=0; i<queryRange; i++) {

            // query value
            ArrayList<Float> queryValFloats = new ArrayList<>();

            // value 생성
            IntStream.range(0, 120).forEach(j -> {
                float queryVal = -1 + random.nextFloat() * (1 - (-1));
                queryValFloats.add(queryVal);
            });

            // query 져장
            queryMap.add(queryValFloats);
        }
        return queryMap;
    }


    // document random data 생성
    public Map<String, ArrayList<Float>> createDocumentData(int docRange) {

        // document init
        Map<String, ArrayList<Float>> docMap = new TreeMap<>();

        float gaussianStd = 0.5f;
        float gaussianMean = 0.0f;

        // document random data 생성
        for(int i=0; i<docRange; i++) {

            // document value
            ArrayList<Float> docValFloats = new ArrayList<>();

            // unique key 생성
            UUID uuid = UUID.randomUUID();

            // value 생성 (10개 난수)
            IntStream.range(0, 10).forEach(j -> {
                float generatedDocNum = (float) (random.nextGaussian() * gaussianStd + gaussianMean);
                docValFloats.add(generatedDocNum);
            });

            // document 저장
            docMap.put(String.format("doc_%s", uuid), docValFloats);
        }
        return docMap;
    }

    // 상위 10개 유사도 document 반환
    public ArrayList<String> getResults(Map<String, ArrayList<Float>> documentDataLists, ArrayList<Float> inputQuery) {

        Map<String, Float> cosineSimilarityResults = new HashMap<>();

        for (String key : documentDataLists.keySet()) {
            float cosineSimilarity = this.cosineSimilarity(documentDataLists.get(key), inputQuery);
            cosineSimilarityResults.put(key, cosineSimilarity);
        }

        // cosine Similarity 기준으로 정렬
        List<String> sortedList = sortByValue(cosineSimilarityResults);
        // 상위 10개만 추출
        List<String> sortedListWinners = sortedList.subList(0, 10);

        return new ArrayList<>(sortedListWinners);
    }

    // Map 정렬
    public static List<String> sortByValue(final Map<String, Float> map) {

        List<String> list = new ArrayList<>(map.keySet());

        list.sort((Comparator<String>) (o1, o2) -> {
            Object v1 = map.get(o1);
            Object v2 = map.get(o2);
            return ((Comparable) v1).compareTo(v2);
        });

        Collections.reverse(list);
        return list;
    }

    // cosine similarity 구현
    public float cosineSimilarity(ArrayList<Float> documents, ArrayList<Float> queries) {

        float sumProduct = 0;
        float sumASq = 0;
        float sumBSq = 0;

        // 제약조건에 따라 documents, queries 의 size 는 120으로 동일하다.
        for (int i = 0; i < documents.size(); i++) {

            sumProduct += (documents.get(i) * queries.get(i));
            sumASq += (documents.get(i) * documents.get(i));
            sumBSq += (queries.get(i) * queries.get(i));
        }

        return (float) (sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq)));
    }
}
