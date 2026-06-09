package com.anan.petadmin.service.impl;

import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.AddressPageQueryDTO;
import com.anan.petadmin.entity.AddressBook;
import com.anan.petadmin.mapper.AddressBookMapper;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Resource
    private AddressBookMapper addressBookMapper;
    @Override
    public PageResult pageQuery(AddressPageQueryDTO addressPageQueryDTO) {
        int page = addressPageQueryDTO.getPage();
        int pageSize = addressPageQueryDTO.getPageSize();
        log.info("分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.like(addressPageQueryDTO.getUserId() != null, AddressBook::getUserId, addressPageQueryDTO.getUserId())
                .like(addressPageQueryDTO.getConsignee() != null, AddressBook::getConsignee, addressPageQueryDTO.getConsignee());

        List<AddressBook> addressBookList = addressBookMapper.selectList(qw);
        PageInfo<AddressBook> pageInfo = new PageInfo<>(addressBookList);
        log.info("查询结果：total={}, size={}", pageInfo.getTotal(), pageInfo.getList().size());
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public AddressBook update(AddressBook addressBook) {
        log.info("更新用户信息：{}", addressBook);
        int row = addressBookMapper.updateById(addressBook);
        if (row <= 0){
            throw new RuntimeException("更新失败");
        }
        return addressBookMapper.selectById(addressBook.getId());
    }

    @Override
    public Integer delete(List<Long> ids) {
        int row = addressBookMapper.deleteByIds(ids);
        if (row <= 0){
            throw new RuntimeException("地址簿删除失败");
        }
        return row;
    }

    @Override
    public AddressBook add(AddressBook addressBook) {
        int row = addressBookMapper.insert(addressBook);
        if (row <=0){
            throw new RuntimeException("地址簿添加失败");
        }
        return addressBookMapper.selectById(addressBook.getId());
    }

    @Override
    public AddressBook getById(Long id) {
        return null;
    }

    @Override
    public void setDefault(AddressBook addressBook) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<AddressBook> list() {
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        return addressBookMapper.selectList(qw);
    }
}
