package com.ruoyi.system.domain.valueobject;

import com.ruoyi.system.domain.entity.DomainException;
import lombok.Data;
import java.util.Objects;

/**
 * 标签编码值对象
 */
@Data
public class TagCode {
    private final String value;

    public TagCode(String value) {
        if (value == null || value.isEmpty()) {
            throw new DomainException("标签编码不能为空");
        }
        if (value.length() != 8) {
            throw new DomainException("标签编码长度必须为8位");
        }
        this.value = value.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagCode that = (TagCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
