package cn.huan.mall.product.entity;

import cn.huan.common.validate.ListValues;
import cn.huan.common.validate.SaveValidate;
import cn.huan.common.validate.UpdateStatusValidate;
import cn.huan.common.validate.UpdateValidate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-14 23:09:39
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    @Null(message = "品牌id必须为空", groups = {SaveValidate.class})
    @NotNull(message = "品牌id不能为空", groups = {UpdateValidate.class, UpdateStatusValidate.class})
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名不能为空", groups = {UpdateValidate.class, SaveValidate.class})
    private String name;
    /**
     * 品牌logo地址
     */
    @Null(message = "你不能修改该字段", groups = {UpdateStatusValidate.class})
    @NotBlank(message = "品牌logo不能为空", groups = {SaveValidate.class})
    @URL(message = "品牌logo必须是一个有效的url", groups = {SaveValidate.class, UpdateValidate.class})
    private String logo;
    /**
     * 介绍
     */
    @Null(message = "你不能修改该字段", groups = {UpdateStatusValidate.class})
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @ListValues(values = {0, 1},groups = {SaveValidate.class,
            UpdateValidate.class,UpdateStatusValidate.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @Null(message = "你不能修改该字段", groups = {UpdateStatusValidate.class})
    @NotNull(message = "首字母必须填写", groups = {SaveValidate.class})
    @Pattern(regexp = "^[a-zA-z]$", message = "首字母只能包含一个字母(a-z,A-Z)", groups = {SaveValidate.class,
            UpdateValidate.class
    })
    private String firstLetter;
    /**
     * 排序
     */
    @Null(message = "你不能修改该字段", groups = {UpdateStatusValidate.class})
    @NotNull(message = "排序字段必须填写", groups = {SaveValidate.class})
    @Min(value = 0, message = "排序字段只能是大于等于0的整数", groups = {SaveValidate.class,
            UpdateValidate.class
    })
    private Integer sort;

}
