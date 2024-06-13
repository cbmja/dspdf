package com.daishin.pdf;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.MasterInfoService;
import com.daishin.pdf.service.MasterSaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class PdfApplicationTests {

	@Autowired
	private MasterSaveService masterSaveService;
	@Autowired
	private MasterInfoService masterInfoService;

	@Test
	void contextLoads() {
/*
		for (int i = 0; i < 10; i++) {
			Master master = new Master();
			master.setMASTER_KEY(i + "");
			master.setTOTAL_SEND_CNT(10+"");
			master.setSEND_CNT(10);
			//master.setRECEIVED_TIME(LocalDateTime.now().minusHours(2L));
			//master.setSTATUS_TIME(LocalDateTime.now().minusHours(2L));
			master.setSTATUS(2);
			master.setTYPE("ARRANGEMENT");
			masterSaveService.save(master);
		}
		for (int i = 10; i < 20; i++) {
			Master master = new Master();
			master.setMASTER_KEY(i + "");
			master.setTOTAL_SEND_CNT(10+"");
			master.setSEND_CNT(10);
			//master.setRECEIVED_TIME(LocalDateTime.now().minusHours(2L));
			//master.setSTATUS_TIME(LocalDateTime.now().minusHours(2L));
			master.setSTATUS(2);
			master.setTYPE("REAL_TIME");
			masterSaveService.save(master);
		}
*/
	}
}
