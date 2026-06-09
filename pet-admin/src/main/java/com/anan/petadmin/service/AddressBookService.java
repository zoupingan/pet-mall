package com.anan.petadmin.service;

import com.anan.petadmin.dto.AddressPageQueryDTO;
import com.anan.petadmin.entity.AddressBook;
import com.anan.petadmin.result.PageResult;

import java.util.List;

public interface AddressBookService {
    PageResult pageQuery(AddressPageQueryDTO addressPageQueryDTO);

    AddressBook update(AddressBook addressBook);

    Integer delete(List<Long> ids);

    AddressBook add(AddressBook addressBook);

    AddressBook getById(Long id);

    void setDefault(AddressBook addressBook);

    void deleteById(Long id);

    List<AddressBook> list();
}
