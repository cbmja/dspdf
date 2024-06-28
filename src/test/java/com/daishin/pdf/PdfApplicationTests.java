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
		Detail detail = new Detail();
		detail.setPK("dv");
		detail.setTR_KEY("asdf");
		detail.setDLV_TYPE_CD("asdf");
		detail.setPRINT_TYPE_NM("asdf");
		detail.setRECV_NUM("asdf");
		detail.setDM_LINK_KEY("asdf");
		detail.setPDF_NM("asdf");
		detail.setRECV_NM("asdf");
		detail.setRECV_POST_CD("asdf");
		detail.setRECV_ADDR("asdf");

		int i = detailSaveService.save(detail);

		System.out.println(i+"/////////////////////////////////////////////////////");
*/

	}
}
