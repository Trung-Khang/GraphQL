# Ghi chú nghiên cứu GraphQL

Tài liệu `05_GraphQL.pdf` giới thiệu GraphQL là ngôn ngữ truy vấn và thao tác dữ liệu mã nguồn mở cho API, do Facebook phát triển năm 2012. Client tự chọn chính xác các field cần dùng; vì vậy response có thể vừa đủ, dự đoán được và không buộc server trả một cấu trúc cố định.

## Đặc trưng và thành phần

- API được mô tả bằng **type system** mạnh: type, field và input xác định dữ liệu hợp lệ trước khi thực thi.
- Một endpoint GraphQL (dự án này là `POST /graphql`) phục vụ toàn bộ truy vấn, thay cho nhiều URL tài nguyên.
- **Query** đọc dữ liệu, **Mutation** thay đổi dữ liệu, **Schema** là hợp đồng, còn **Resolver** ánh xạ field/query/mutation tới nghiệp vụ.
- Một request có thể lấy dữ liệu liên quan lồng nhau, ví dụ Product cùng User và Category, rất hữu ích trên mạng chậm, mobile hoặc IoT.
- Trong Spring for GraphQL, `@Controller` đánh dấu resolver; `@QueryMapping` và `@MutationMapping` ánh xạ operation theo tên; `@Argument` nhận biến/input trong request. `@SchemaMapping`/`@BatchMapping` phù hợp với field quan hệ.
- GraphiQL là giao diện khám phá schema và gửi query/mutation; dự án bật tại `/graphiql`.

## REST và GraphQL

Cả REST lẫn GraphQL đều là API client-server, không trạng thái, thao tác dữ liệu, thường trao đổi JSON, có thể cache, và không phụ thuộc ngôn ngữ hay cơ sở dữ liệu. REST phù hợp nguồn dữ liệu đơn giản, tài nguyên rõ ràng và nhiều endpoint URL; server ấn định cấu trúc response. GraphQL hợp dữ liệu lớn, phức tạp/liên quan; client ấn định cấu trúc field và thường dùng một endpoint.

REST dễ **over-fetching** khi trả cả field không cần, và **under-fetching** khi cần gọi nhiều endpoint mới đủ dữ liệu liên quan. GraphQL giảm hai vấn đề này nhờ field selection và query lồng nhau. Đổi lại schema, phân quyền, độ phức tạp query và cache cần được thiết kế cẩn thận. GraphQL thường trả HTTP 200 kể cả khi operation có lỗi nghiệp vụ; client phải đọc mảng `errors`.

## Ví dụ cho dự án

```graphql
query {
  productsByPriceAsc {
    id title price
    category { id name }
    user { id fullname email }
  }
}
```

```graphql
mutation CreateProduct($input: CreateProductInput!) {
  createProduct(input: $input) { id title price }
}
```

```json
{"input":{"title":"Bàn phím","quantity":5,"desc":"Cơ","price":499000,"userId":"1","categoryId":"1"}}
```
