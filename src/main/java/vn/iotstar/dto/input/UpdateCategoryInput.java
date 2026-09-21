package vn.iotstar.dto.input;
import java.util.List;
public record UpdateCategoryInput(String name, String images, List<Long> userIds) {}
