package vn.iotstar.dto.input;
import java.util.List;
public record CreateCategoryInput(String name, String images, List<Long> userIds) {}
