package com.opisir.bill;

import com.opisir.bill.model.Record;
import com.opisir.bill.repository.BillRepository;
import com.opisir.bill.service.AssayService;
import com.opisir.bill.service.BudgetService;
import com.opisir.bill.service.BillService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@SpringBootTest
class AccountApplicationTests {

    @Autowired
    BillRepository billRepository;

    @Autowired
    BillService billService;

    @Autowired
    AssayService assayService;

    @Autowired
    BudgetService budgetService;

    @Test
    void contextLoads() {
        Page<Record> page = billRepository.findAllByRecordTime(PageRequest.of(0, 10), "2020-11", 2L);
        System.out.println(page);
    }

//    @Test
//    void getAmount() {
//        BillAmountVO recordAmount = billService.getBillAmount("2020-10");
//        System.out.println(recordAmount);
//    }
//
//    @Test
//    void assay() {
//        assayService.assay("2020-10", 0, 0);
//    }


    @Test
    void add() {
        BigDecimal bigDecimal = new BigDecimal(0);
        BigDecimal bigDecimal1 = new BigDecimal("12.4");
        BigDecimal add = bigDecimal.add(bigDecimal1);
        System.out.println(add.doubleValue());

    }

}
