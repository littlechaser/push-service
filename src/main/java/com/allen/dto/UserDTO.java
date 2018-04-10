package com.allen.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * @author yang_tao@<yangtao.letzgo.com.cn>
 * @version 1.0
 * @date 2018-04-10 9:16
 */
@Data
public class UserDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Range(min = 0, max = 120, message = "年龄必须在{min}和{max}之间")
    private Integer age;

    @NotNull(message = "性别不能为空")
    @Range(min = 0, max = 1, message = "性别必须在{min}和{max}之间")
    private Integer sex;

    @NotNull(message = "生日不能为空")
    @Past(message = "生日必须为当前时间之前的一个时间")
    private Date birthday;
}
