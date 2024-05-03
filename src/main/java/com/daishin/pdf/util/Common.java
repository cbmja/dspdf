package com.daishin.pdf.util;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Common {

    /**
     * 파일 저장 일시 포맷
     * @return
     */
    public static String getCurrnetYYYYMM() {
        SimpleDateFormat sdfyyyymm = new SimpleDateFormat("yyyyMM");
        Date now = new Date();
        return sdfyyyymm.format(now);
    }

    /**
     * multipartFile을 entity 객체로 변환
     * @param jsonFilePath / json파일 경로
     * @return Master , List<Detail> 들어있는 map
     */
    public static Map txtToEntity(String jsonFilePath){

        // txt -> json 파싱
        try {
            // MultipartFile을 읽어서 Map 객체로 변환
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(new File(jsonFilePath), Map.class);

            // json -> map
            List<Map> details = (List)map.get("detail");
            List<Detail> detailList = mapToDetailList(details);

            //map -> entity
            Master master = mapToMaster((Map)map.get("master"));

            Map entity = new HashMap<>();
            entity.put("master" , master);
            entity.put("detailList" , detailList);
            return entity;

        } catch (IOException e) {
            e.printStackTrace();
        }

       return null;
    }


    public static Master mapToMaster(Map map){

        Master master = new Master();
        master.setTrKey((String)map.get("TR_KEY"));
        master.setTotalSendCnt((String)map.get("TOTAL_SEND_CNT"));
        master.setDlvTypeCd((String)map.get("DLV_TYPE_CD"));
        master.setPrintTypeNm((String)map.get("PRINT_TYPE_NM"));
        master.setPageCnt((String)map.get("PAGE_CNT"));
        master.setRetYn((String)map.get("RET_YN"));
        master.setPdfYn((String)map.get("PDF_YN"));

        return master;
    }

    public static List<Detail> mapToDetailList(List<Map> list){

        List<Map> details = list;

        List<Detail> detailList = new ArrayList<>();

        for(Map map : details){
            Detail detail = new Detail();

            detail.setTrKey((String)map.get("TR_KEY"));
            detail.setRecvNum((String)map.get("RECV_NUM"));
            detail.setDmLinkKey((String)map.get("DM_LINK_KEY"));
            detail.setDlvCd((String)map.get("DLV_CD"));
            detail.setRecvNm((String)map.get("RECV_NM"));
            detail.setRecvPostCd((String)map.get("RECV_POST_CD"));
            detail.setRecvAddr((String)map.get("RECV_ADDR"));
            detail.setRecvAddrDetail((String)map.get("RECV_ADDR_DETAIL"));

            detailList.add(detail);
        }

        return detailList;
    }

    //////////////////////파일 압축 해제 후 저장 SSSSSSSSSS //////////////////////

    /**
     * 압축해제 후 저장
     * @param file / 저장할 zip 파일
     * @param path / 저장 경로
     */
    public static void unZipAndSave(MultipartFile file , String path){
        // 임시 디렉토리 생성 및 파일 저장
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    // 파일이 있는 경로 추출
                    File newFile = newFile(new File(path), zipEntry);
                    // 디렉토리가 없으면 생성
                    if (newFile.getParentFile() != null && !newFile.getParentFile().exists()) {
                        newFile.getParentFile().mkdirs();
                    }
                    // 파일 쓰기
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        IOUtils.copy(zipInputStream, fos);
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return ;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

//////////////////////파일 압축 해제 후 저장 EEEEEEEEEE //////////////////////

//////////////////////파일 압축 해제 후 저장한 파일명 찾기 SSSSSSSSSS //////////////////////

    /**
     * 
     * @param directoryPath / 압축파일 저장 경로
     */
    public static List<String> findJsonFiles(String directoryPath) {
        File directory = new File(directoryPath);

        List<String> fileNames = new ArrayList<>();
        // 파일 필터 정의
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        };

        // 필터 사용하여 .json 파일만 찾기
        String[] files = directory.list(filter);
        if (files != null) {
            for (String file : files) {
                fileNames.add(file);
            }
        } else {
            System.out.println("No .json files found or the directory is empty.");
        }
        return fileNames;
    }

//////////////////////파일 압축 해제 후 저장한 파일명 찾기 EEEEEEEEEE //////////////////////
//////////////////////json파일 삭제 SSSSSSSSSS //////////////////////
    /**
     * jsonFile 삭제
     * @param directoryPath
     */
    public static void deleteJsonFiles(String directoryPath) {
        File directory = new File(directoryPath);

        // 파일 필터를 사용하여 .json 확장자를 가진 파일만 찾기
        FilenameFilter jsonFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        };
        File[] files = directory.listFiles(jsonFilter);
        // 파일 필터 정의

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        } else {
            System.out.println("No .json files found or the directory is empty.");
        }

    }
    //////////////////////json파일 삭제 EEEEEEEEEE //////////////////////
    //////////////////////파일 이동 SSSSSSSSSS //////////////////////
    /**
     * path1 에 있는 파일들을 path2로 옮김
     * @param path1
     * @param path2
     */
    public static void moveFile(String path1 , String path2){
        Path sourceDir = Paths.get(path1);
        Path targetDir = Paths.get(path2);

        try {
            // 대상 디렉토리 존재 확인 및 생성
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                System.out.println("대상 디렉토리 생성: " + targetDir);
            }

            // 원본 디렉토리에서 모든 파일을 반복하여 처리
            Files.list(sourceDir).forEach(sourcePath -> {
                try {
                    // 대상 디렉토리 경로 생성
                    Path targetPath = targetDir.resolve(sourcePath.getFileName());

                    // 파일 존재 확인
                    if (Files.exists(sourcePath)) {
                        // 파일 이동
                        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("파일 이동: " + sourcePath + " -> " + targetPath);
                    } else {
                        System.out.println("이동할 파일이 존재하지 않습니다: " + sourcePath);
                    }
                } catch (IOException e) {
                    System.err.println("파일 이동 중 에러 발생: " + sourcePath);
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("디렉토리 읽기 중 에러 발생");
            e.printStackTrace();
        }
    }
//////////////////////파일 이동 EEEEEEEEEE //////////////////////

}
