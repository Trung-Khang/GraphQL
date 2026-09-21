package vn.iotstar.service;

import vn.iotstar.dto.input.*;
import vn.iotstar.entity.*;
import vn.iotstar.exception.*;
import vn.iotstar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class CatalogService {
    private final CategoryRepository categories; private final ProductRepository products; private final UserRepository users; private final PasswordEncoder passwordEncoder;
    @Transactional(readOnly = true) public List<Product> products() { return products.findAll(); }
    @Transactional(readOnly = true) public Product product(Long id) { return products.findById(id).orElse(null); }
    @Transactional(readOnly = true) public List<Product> productsByPrice() { return products.findAllByOrderByPriceAsc(); }
    @Transactional(readOnly = true) public List<Product> productsByCategory(Long categoryId) { requireCategory(categoryId); return products.findByCategoryIdOrderByPriceAsc(categoryId); }
    @Transactional(readOnly = true) public List<Category> categories() { return categories.findAll(); }
    @Transactional(readOnly = true) public Category category(Long id) { return categories.findById(id).orElse(null); }
    @Transactional(readOnly = true) public List<User> users() { return users.findAll(); }
    @Transactional(readOnly = true) public User user(Long id) { return users.findById(id).orElse(null); }
    public User createUser(CreateUserInput input) {
        required(input.fullname(), "Họ tên không được để trống"); required(input.email(), "Email không được để trống");
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new BusinessValidationException("Email không đúng định dạng");
        if (users.findByEmailIgnoreCase(email).isPresent()) throw new ConflictException("Email đã tồn tại");
        required(input.password(), "Mật khẩu không được để trống"); if (input.password().length() < 6) throw new BusinessValidationException("Mật khẩu tối thiểu 6 ký tự");
        if (input.phone() != null && !input.phone().isBlank() && !input.phone().trim().matches("^[0-9+() .-]{6,30}$")) throw new BusinessValidationException("Số điện thoại không hợp lệ");
        User user = new User(); user.setFullname(input.fullname().trim()); user.setEmail(email); user.setPassword(passwordEncoder.encode(input.password())); user.setPhone(blankToNull(input.phone())); return users.save(user);
    }
    public Category createCategory(CreateCategoryInput input) { Category category = new Category(); applyCategory(category, input.name(), input.images(), input.userIds(), true); return categories.save(category); }
    public Category updateCategory(Long id, UpdateCategoryInput input) { Category category = requireCategory(id); applyCategory(category, input.name(), input.images(), input.userIds(), false); return categories.save(category); }
    public boolean deleteCategory(Long id) { if (products.existsByCategoryId(id)) throw new BusinessValidationException("Không thể xóa Category vì vẫn còn Product"); Category c = requireCategory(id); c.getUsers().forEach(u -> u.getCategories().remove(c)); categories.delete(c); return true; }
    public Category assignUsers(Long id, List<Long> userIds) { Category c = requireCategory(id); setUsers(c, userIds); return categories.save(c); }
    public Product createProduct(CreateProductInput input) { Product product = new Product(); applyProduct(product, input.title(), input.quantity(), input.desc(), input.price(), input.userId(), input.categoryId()); return products.save(product); }
    public Product updateProduct(Long id, UpdateProductInput input) { Product p = requireProduct(id); applyProduct(p, input.title(), input.quantity(), input.desc(), input.price(), input.userId(), input.categoryId()); return products.save(p); }
    public boolean deleteProduct(Long id) { products.delete(requireProduct(id)); return true; }
    private void applyCategory(Category c, String name, String images, List<Long> userIds, boolean create) {
        if (create || name != null) { required(name, "Tên Category không được để trống"); String clean = name.trim(); if (categories.existsByNameIgnoreCase(clean) && (c.getId() == null || !clean.equalsIgnoreCase(c.getName()))) throw new ConflictException("Tên Category đã tồn tại"); c.setName(clean); }
        if (images != null) c.setImages(blankToNull(images)); if (userIds != null) setUsers(c, userIds);
    }
    private void setUsers(Category c, List<Long> ids) { Set<User> selected = new LinkedHashSet<>(); for (Long id : ids) selected.add(requireUser(id)); c.getUsers().clear(); c.getUsers().addAll(selected); }
    private void applyProduct(Product p, String title, Integer quantity, String desc, BigDecimal price, Long userId, Long categoryId) {
        if (title != null) { required(title, "Tiêu đề Product không được để trống"); p.setTitle(title.trim()); }
        else if (p.getId() == null) throw new BusinessValidationException("Tiêu đề Product không được để trống");
        if (quantity != null) { if (quantity < 0) throw new BusinessValidationException("Số lượng phải lớn hơn hoặc bằng 0"); p.setQuantity(quantity); } else if (p.getId() == null) throw new BusinessValidationException("Số lượng là bắt buộc");
        if (price != null) { if (price.signum() < 0) throw new BusinessValidationException("Giá phải lớn hơn hoặc bằng 0"); p.setPrice(price); } else if (p.getId() == null) throw new BusinessValidationException("Giá là bắt buộc");
        if (desc != null) { if (desc.length() > 2000) throw new BusinessValidationException("Mô tả tối đa 2000 ký tự"); p.setDescription(blankToNull(desc)); }
        if (userId != null) p.setUser(requireUser(userId)); else if (p.getId() == null) throw new BusinessValidationException("User là bắt buộc");
        if (categoryId != null) p.setCategory(requireCategory(categoryId)); else if (p.getId() == null) throw new BusinessValidationException("Category là bắt buộc");
    }
    private Category requireCategory(Long id) { if (id == null) throw new BusinessValidationException("Category là bắt buộc"); return categories.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy Category: " + id)); }
    private Product requireProduct(Long id) { return products.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy Product: " + id)); }
    private User requireUser(Long id) { if (id == null) throw new BusinessValidationException("User là bắt buộc"); return users.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy User: " + id)); }
    private void required(String value, String message) { if (value == null || value.isBlank()) throw new BusinessValidationException(message); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
