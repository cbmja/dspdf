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
			if(i%3 ==0){
				master.setSTATUS(1);
			}else if(i%3 ==1){
				master.setSTATUS(2);
			}else {
				master.setSTATUS(3);
			}
			master.setSTATUS_TIME(LocalDateTime.now().minusHours(2L));
			masterSaveService.save(master);
		}

	}
}
