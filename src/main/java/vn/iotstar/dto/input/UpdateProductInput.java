package vn.iotstar.dto.input;
import java.math.BigDecimal;
public record UpdateProductInput(String title, Integer quantity, String desc, BigDecimal price, Long userId, Long categoryId) {}
