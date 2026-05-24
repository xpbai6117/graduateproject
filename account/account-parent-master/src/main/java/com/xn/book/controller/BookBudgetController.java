package com.xn.book.controller;



import com.xn.book.entity.BookBudgetInfo;
import com.xn.book.service.BookBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/book/budget")
public class BookBudgetController {

    @Autowired
    private BookBudgetService bookBudgetService;

    @PostMapping
    public Boolean saveOrUpdateBudget(@RequestBody @Valid BookBudgetInfo bookBudgetInfo){
        return bookBudgetService.saveOrUpdateBudget(bookBudgetInfo);
    }

    @GetMapping("/{bookId}")
    public BookBudgetInfo bookBudgetInfo(@PathVariable("bookId") Integer bookId){
        return bookBudgetService.bookBudgetInfo(bookId);
    }
}
