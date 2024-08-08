package com.groupware.view;


import com.groupware.dto.UserFileBoxDto;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractView;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

public class UserFileDownloadView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest req, HttpServletResponse resp) throws Exception {

        // Controller에서 model 객체에 담아준 데이터 받기
        UserFileBoxDto attach = (UserFileBoxDto) model.get("attach");

        //브라우저가 응답 컨텐츠를 다운로드로 처리하도록 정보 설정
        resp.setContentType("application/octet-stream;charset=utf-8");
        //브라우저에게 다운로드하는 파일의 이름을 알려주는 코드
        resp.addHeader("Content-Disposition",
                "Attachment;filename=\"" +
                        new String(attach.getUserFileName().getBytes("utf-8"), "ISO-8859-1") + "\"");

        //ServletContext : JSP의 application객체와 동일한 객체
        // ServletContext application = this.getServletContext(); // View2는 이렇게도 가능
        ServletContext application = req.getServletContext();
        String path = application.getRealPath("/user-filebox/" + attach.getSavedFileName()); // 웹경로 --> 컴퓨터 경로

        FileInputStream fis = new FileInputStream(path);    //파일을 읽는 도구
        OutputStream fos = resp.getOutputStream();            //브라우저에게 전송하는 도구

        while (true) {
            int data = fis.read();  //파일에서 1byte 읽기
            if (data == -1) { //더 이상 읽을 데이터가 없다면 (EOF)
                break;
            }
            fos.write(data); //응답 스트림에 1byte 쓰기
        }

        fis.close();
        fos.close();


    }
}
