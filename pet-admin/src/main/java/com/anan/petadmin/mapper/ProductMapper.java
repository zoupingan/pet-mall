package com.anan.petadmin.mapper;

import com.anan.petadmin.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteProductById(@Param("id") Long id);

}
