package com.ruoyi.system.domain.valueobject;

import com.ruoyi.system.domain.entity.DomainException;
import lombok.Data;

/**
 * 图片数量值对象
 */
@Data
public class ImageCount {
    private int value;

    public ImageCount(int value) {
        if (value < 0) {
            throw new DomainException("图片数量不能为负数");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isEmpty() {
        return this.value == 0;
    }
}
