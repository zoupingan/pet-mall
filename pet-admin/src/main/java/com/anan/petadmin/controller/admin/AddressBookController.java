package com.anan.petadmin.controller.admin;

import com.anan.petadmin.dto.AddressPageQueryDTO;
import com.anan.petadmin.entity.AddressBook;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.AddressBookService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/address")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;
    @GetMapping("/list")
    public Result<PageResult> list(AddressPageQueryDTO addressPageQueryDTO) {
        PageResult result = addressBookService.pageQuery(addressPageQueryDTO);
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<AddressBook> update(@RequestBody AddressBook addressBook) {
        AddressBook result = addressBookService.update(addressBook);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{ids}")
    public Result<Integer> delete(@PathVariable List<Long> ids) {
        Integer row =addressBookService.delete(ids);
        return Result.success(row);
    }

    @PostMapping("/add")
    public Result<AddressBook> add(@RequestBody AddressBook addressBook) {
        AddressBook result = addressBookService.add(addressBook);
        return Result.success(result);
    }
}
