package com.daishin.pdf;

import com.daishin.pdf.dto.Detail;
import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.DetailSaveService;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class PdfApplicationTests {

	@Autowired
	private MasterSaveService masterSaveService;
	@Autowired
	private MasterInfoService masterInfoService;
	@Autowired
	private DetailSaveService detailSaveService;

	@Test
	void contextLoads() {

/*
		for(int i=1; i<=300; i++){
			Master master = new Master();
			master.setMASTER_KEY(i+"");
			master.setTOTAL_SEND_CNT("10");
			master.setSEND_CNT(2);
			master.setSTATUS(100);
			master.setRECEIVED_TIME(LocalDateTime.now());
			master.setSTATUS_TIME(LocalDateTime.now());
			master.setTYPE("df");

			masterSaveService.save(master);
		}
*/






	}
}
