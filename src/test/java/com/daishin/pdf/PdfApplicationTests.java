package com.daishin.pdf;

import com.daishin.pdf.dto.Master;
import com.daishin.pdf.service.MasterSaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class PdfApplicationTests {

	@Autowired
	private MasterSaveService masterSaveService;

	@Test
	void contextLoads() {

		for (int i = 0; i < 118; i++) {
			Master master = new Master();
			master.setMASTER_KEY(i + "");
			master.setRECEIVED_TIME(LocalDateTime.now().plusDays(i));
			masterSaveService.save(master);
		}

	}
}
