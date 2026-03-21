package com.ruoyi.system.domain.valueobject;

import com.ruoyi.system.domain.entity.DomainException;
import lombok.Data;

/**
 * 数据集编码值对象
 */
@Data
public class DatasetCode {
    private final String value;

    public DatasetCode(String value) {
        if (value == null || value.isEmpty()) {
            throw new DomainException("数据集编码不能为空");
        }
        if (value.length() != 6) {
            throw new DomainException("数据集编码长度必须为6位");
        }
        this.value = value.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatasetCode that = (DatasetCode) o;
        return java.util.Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
