package com.anan.petadmin.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 商品实体类 Product
 * 对应数据库表：product
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品 ID（主键） - 数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    @TableField("product_name")
    private String productName;

    /** 关联分类 ID */
    private Long categoryId;

    /** 进价 */
    private Integer costPrice;

    /** 售价 */
    private Integer price;

    /** 库存 */
    private Integer stock;

    /** 主图 URL */
    private String imageUrl;

    /** 商品详情/描述 */
    private String description;

    /** 上架状态（0 - 下架，1 - 上架） */
    private Integer status;

    /** 逻辑删除标识（0 - 未删除，1 - 已删除） */
    private Integer isDeleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;

}