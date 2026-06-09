package com.anan.petadmin.controller.admin;

import com.anan.petadmin.entity.Category;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @GetMapping("/list")
    public Result<PageResult> list(@RequestParam Integer page, @RequestParam Integer pageSize) {
        PageResult result = categoryService.list(page, pageSize);
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Category> update(@RequestBody Category category) {
        Category result = categoryService.update(category);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{ids}")
    public Result<Integer> delete(@PathVariable List<Long> ids) {
        Integer row = categoryService.delete(ids);
        return Result.success(row);
    }

    @PostMapping("/add")
    public Result<Category> add(@RequestBody Category category) {
        Category result = categoryService.add(category);
        return Result.success(result);
    }
}
